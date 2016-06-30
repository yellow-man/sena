package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;
import java.util.Random;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.components.db.FinancesComponent;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.FinancesEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.models.Finances;
import yokohama.yellow_man.sena.core.models.Stocks;

/**
 * 企業財務インポートバッチクラス。
 * <p>銘柄情報（stocks）を元に企業財務を取得し、
 * 財務（finances）テーブルにインポートする。
 * 相手方にアクセス負荷とならぬよう、1件取得ごとにランダムでインターバル(2秒～5秒)を設けている。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ImportFinances extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code import_finances.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_FINANCES_MAIL_TITLE    = Play.application().configuration().getString("import_finances.mail_title", "[sena]企業財務インポートバッチ実行結果");

	/**
	 * 企業財務インポートバッチクラスコンストラクタ。
	 * @since 1.0
	 */
	public ImportFinances() {
		// メールタイトル
		this.emailTitle  = IMPORT_FINANCES_MAIL_TITLE;
		// ログファイル名
		this.logFileName = getClass().getName() + "." + this.logDateFormat.format(new Date()) + ".log";
		// ログファイルpath
		this.logFilePath = LOG_FILE_PATH + getClass().getName() + "/" + this.logFileName;
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(java.util.List)
	 * @since 1.0
	 */
	@Override
	protected void run(List<String> args) {
		AppLogger.info("企業財務インポートバッチ　開始");

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

					// 外部サイトから企業財務を取得
					List<FinancesEntity> financesEntityList = ScrapingComponent.getFinancesList(stockCode);

					if (CheckUtils.isEmpty(financesEntityList)) {
						AppLogger.warn(new StringBuffer("財務リストが取得できませんでした。：")
								.append(stockCode).append(":").append(stockName)
								.toString());

						skip++;
					} else {

						// モデルに詰め替えDBに保存
						int ret = _saveFinances(stockCode, financesEntityList);
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
					AppLogger.error(new StringBuffer("外部サイトから企業財務が取得できませんでした。：")
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
		AppLogger.info("企業財務インポートバッチ　終了：処理件数=" + String.valueOf(success + error + skip) + ", 成功件数=" + success + ", 失敗件数=" + error + ", スキップ件数=" + skip);
	}

	/**
	 * エンティティから、モデルにデータを詰め替えDBに保存する。
	 * 時間はかかるが、バルクインサートは使用せず1件ずつ処理を行う。
	 *
	 * @param stockCode 銘柄コード
	 * @param financesEntityList 財務エンティティのリスト
	 * @return 1件インポートが成功したら（0..成功）、ただし例外が発生していたら（1..失敗）、1件も処理しなかったら（2..スキップ）
	 * @since 1.0
	 */
	private int _saveFinances(Integer stockCode, List<FinancesEntity> financesEntityList) {
		int ret = 2;

		// 登録済みの信用残を取得する。
		List<String> yearSettlementTypesIdList = FinancesComponent.getYearSettlementTypesIdByStockCode(stockCode);

		for (FinancesEntity financesEntity : financesEntityList) {
			String yearSettlementTypesId = new StringBuffer(financesEntity.year).append("_").append(financesEntity.settlementTypesId).toString();

			if ((financesEntity.year == null || financesEntity.settlementTypesId == null)
					|| (yearSettlementTypesIdList != null && yearSettlementTypesIdList.contains(yearSettlementTypesId))) {
				AppLogger.info("日付が変換できないか、既に登録済みの為処理をスキップしました。：financesEntity=" + financesEntity);
				continue;
			}

			Finances finances = new Finances();
			try {
				finances.year                 = financesEntity.year;
				finances.settlementTypesId    = financesEntity.settlementTypesId;
				finances.stockCode            = financesEntity.stockCode;
				finances.sales                = financesEntity.sales;
				finances.operatingProfit      = financesEntity.operatingProfit;
				finances.netProfit            = financesEntity.netProfit;
				finances.created              = new Date();
				finances.modified             = new Date();
				finances.deleteFlg            = false;
				finances.save();

				// 例外が発生していたら、失敗として扱う
				if (ret != 1) {
					ret = 0;
				}
			} catch (Exception e) {
				AppLogger.error("財務DB保存時にエラーが発生しました。：financesEntity=" + financesEntity, e);
				ret = 1;
			}
		}
		return ret;
	}
}
