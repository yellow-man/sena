package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;
import java.util.Random;

import play.Play;
import yokohama.yellow_man.common_tools.ListUtils;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.IndicatorsEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.models.Indicators;
import yokohama.yellow_man.sena.core.models.Stocks;

/**
 * 企業指標インポートバッチクラス。
 * <p>銘柄情報（stocks）を元に企業指標を取得し、
 * 指標（indicators）テーブルにインポートする。
 * 相手方にアクセス負荷とならぬよう、1件取得ごとにランダムでインターバル(2秒～5秒)を設けている。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ImportIndicators extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code import_indicators.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_INDICATORS_MAIL_TITLE    = Play.application().configuration().getString("import_indicators.mail_title", "[sena]企業指標インポートバッチ実行結果");

	/**
	 * 企業指標インポートバッチクラスコンストラクタ。
	 * @since 1.0
	 */
	public ImportIndicators() {
		// メールタイトル
		this.emailTitle  = IMPORT_INDICATORS_MAIL_TITLE;
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
		AppLogger.info("企業指標インポートバッチ　開始");

		// 現在日時
		Date now = new Date();
		// 成功件数
		int success = 0;
		// エラー件数
		int error = 0;

		// 銘柄情報取得
		List<Stocks> stocksList = StocksComponent.getStocksList();
		if (ListUtils.isEmpty(stocksList)) {
			AppLogger.warn("銘柄情報が取得できませんでした。");

		} else {
			for (Stocks stocks : stocksList) {
				Integer stockCode = stocks.stockCode;
				String stockName = stocks.stockName;

				try {

					// 外部サイトから企業指標を取得
					IndicatorsEntity indicatorsEntity = ScrapingComponent.getIndicators(stockCode);

					// モデルに詰め替えDBに保存
					boolean ret = _saveIndicators(now, indicatorsEntity);
					if (ret) {
						success++;
					} else {
						error++;
					}

				} catch (ScrapingException e) {

					// 取得できない場合は、処理を飛ばす。
					AppLogger.error(new StringBuffer("外部サイトから企業指標が取得できませんでした。：")
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
		AppLogger.info("企業指標インポートバッチ　終了：処理件数=" + String.valueOf(success + error) + ", 成功件数=" + success + ", 失敗件数=" + error);
	}

	/**
	 * エンティティから、モデルにデータを詰め替えDBに保存する。
	 * @param date 取得日
	 * @param indicatorsEntity 信用残エンティティのリスト
	 * @return 1件インポートが成功したら{@code true}、全件失敗したら{@code false}
	 * @since 1.0
	 */
	private boolean _saveIndicators(Date date, IndicatorsEntity indicatorsEntity) {
		boolean ret = false;

		Indicators indicators = new Indicators();
		try {
			indicators.date                = date;
			indicators.stockCode           = indicatorsEntity.stockCode;
			indicators.dividendYield       = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.dividendYield, null);
			indicators.priceEarningsRatio  = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.priceEarningsRatio, null);
			indicators.priceBookValueRatio = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.priceBookValueRatio, null);
			indicators.earningsPerShare    = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.earningsPerShare, null);
			indicators.bookValuePerShare   = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.bookValuePerShare, null);
			indicators.returnOnEquity      = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.returnOnEquity, null);
			indicators.capitalRatio        = yokohama.yellow_man.common_tools.NumberUtils.toBigDecimal(indicatorsEntity.capitalRatio, null);
			indicators.created             = new Date();
			indicators.modified            = new Date();
			indicators.deleteFlg           = false;
			indicators.save();
			ret = true;
		} catch (Exception e) {
			AppLogger.error("信用残DB保存時にエラーが発生しました。：indicatorsEntity=" + indicatorsEntity, e);
		}
		return ret;
	}
}
