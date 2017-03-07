package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.components.db.DebitBalancesComponent;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.DebitBalancesEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.models.DebitBalances;
import yokohama.yellow_man.sena.core.models.Stocks;

/**
 * 信用残インポートバッチクラス。
 * <p>銘柄情報（stocks）を元に信用残を取得し、
 * 信用残（debit_balances）テーブルにインポートする。
 * 相手方にアクセス負荷とならぬよう、1件取得ごとにランダムでインターバル(2秒～5秒)を設けている。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 */
public class ImportDebitBalances extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code import_debit_balances.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_DEBIT_BALANCES_MAIL_TITLE    = Play.application().configuration().getString("import_debit_balances.mail_title", "[sena]信用残インポートバッチ実行結果");

	/**
	 * 信用残インポートバッチクラスコンストラクタ。
	 * @since 1.0.0-1.0
	 */
	public ImportDebitBalances() {
		// メールタイトル
		this.emailTitle  = IMPORT_DEBIT_BALANCES_MAIL_TITLE;
		// ログファイル名
		this.logFileName = getClass().getName() + "." + this.logDateFormat.format(new Date()) + ".log";
		// ログファイルpath
		this.logFilePath = LOG_FILE_PATH + getClass().getName() + "/" + this.logFileName;
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(java.util.List)
	 * @since 1.0.0-1.0
	 */
	@Override
	protected void run(List<String> args) {
		AppLogger.info("信用残インポートバッチ　開始");

		// 成功件数
		int success = 0;
		// エラー件数
		int error = 0;
		// スキップ件数
		int skip = 0;

		// 銘柄情報取得
		List<Stocks> stocksList = StocksComponent.getStocksList();
		if (CheckUtils.isEmpty(stocksList)) {
			AppLogger.warn("銘柄情報が取得できませんでした。");

		} else {
			for (Stocks stocks : stocksList) {
				Integer stockCode = stocks.stockCode;
				String stockName = stocks.stockName;

				try {

					// 外部サイトから信用残を取得
					List<DebitBalancesEntity> debitBalancesEntityList = ScrapingComponent.getDebitBalancesList(stockCode);

					if (CheckUtils.isEmpty(debitBalancesEntityList)) {
						AppLogger.warn(new StringBuffer("信用残リストが取得できませんでした。：")
								.append(stockCode).append(":").append(stockName)
								.toString());

						skip++;
					} else {

						// モデルに詰め替えDBに保存
						int ret = _saveDebitBalances(stockCode, debitBalancesEntityList);
						switch (ret) {
							case 0:
								success++;
								break;

							case 1:
								error++;
								break;

							case 2:
								skip++;
								break;
						}
					}

				} catch (ScrapingException e) {

					// 取得できない場合は、処理を飛ばす。
					AppLogger.error(new StringBuffer("外部サイトから信用残が取得できませんでした。：")
							.append(stockCode).append(":").append(stockName)
							.toString(), e);

					error++;
					continue;
				}

				// インターバル(2秒～5秒)
				try {
					Random rnd = new Random();
					int ran = rnd.nextInt(3) + 2;
					Thread.sleep(1000 * ran);
				} catch (InterruptedException e) {
					AppLogger.error("インターバル取得時にエラーが発生しました。", e);
				}
			}
		}
		AppLogger.info("信用残インポートバッチ　終了：処理件数=" + String.valueOf(success + error + skip) + ", 成功件数=" + success + ", 失敗件数=" + error + ", スキップ件数=" + skip);
	}

	/**
	 * エンティティから、モデルにデータを詰め替えDBに保存する。
	 * 時間はかかるが、バルクインサートは使用せず1件ずつ処理を行う。
	 *
	 * @param stockCode 銘柄コード
	 * @param debitBalancesEntityList 信用残エンティティのリスト
	 * @return 1件インポートが成功したら（0..成功）、ただし例外が発生していたら（1..失敗）、1件も処理しなかったら（2..スキップ）
	 * @since 1.0.0-1.0
	 */
	private int _saveDebitBalances(Integer stockCode, List<DebitBalancesEntity> debitBalancesEntityList) {
		int ret = 2;

		// 登録済みの信用残を取得する。
		List<Long> releaseDateTimeList = DebitBalancesComponent.getReleaseDateTimeByStockCode(stockCode);

		for (DebitBalancesEntity debitBalancesEntity : debitBalancesEntityList) {
			Date releaseDate = debitBalancesEntity.releaseDate;

			if (releaseDate == null
					|| (releaseDateTimeList != null && releaseDateTimeList.contains(Long.valueOf(releaseDate.getTime())))) {
				AppLogger.info("日付が変換できないか、既に登録済みの為処理をスキップしました。：debitBalancesEntity=" + debitBalancesEntity);
				continue;
			}

			DebitBalances debitBalances = new DebitBalances();
			try {
				debitBalances.releaseDate          = debitBalancesEntity.releaseDate;
				debitBalances.stockCode            = debitBalancesEntity.stockCode;
				debitBalances.marginSellingBalance = NumberUtils.toInt(debitBalancesEntity.marginSellingBalance, -1);
				debitBalances.marginDebtBalance    = NumberUtils.toInt(debitBalancesEntity.marginDebtBalance, -1);
				debitBalances.ratioMarginBalance   = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(debitBalancesEntity.ratioMarginBalance, "-1");
				debitBalances.created              = new Date();
				debitBalances.modified             = new Date();
				debitBalances.deleteFlg            = false;
				debitBalances.save();

				// 例外が発生していたら、失敗として扱う
				if (ret != 1) {
					ret = 0;
				}
			} catch (Exception e) {
				AppLogger.error("信用残DB保存時にエラーが発生しました。：debitBalancesEntity=" + debitBalancesEntity, e);
				ret = 1;
			}
		}
		return ret;
	}
}
