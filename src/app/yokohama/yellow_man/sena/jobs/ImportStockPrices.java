package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;
import java.util.Random;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.StockPricesEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.models.Stocks;
import yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument;

/**
 * 株価情報インポートバッチクラス。
 * <p>銘柄情報（stocks）を元に株価情報を取得し、
 * 株価（stock_prices）テーブルにインポートする。
 * 相手方にアクセス負荷とならぬよう、1件取得ごとにランダムでインターバル(2秒～5秒)を設けている。
 *
 * @author yellow-man
 * @since 1.1.0-1.2
 */
public class ImportStockPrices extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code stock_prices.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCK_PRICES_MAIL_TITLE    = Play.application().configuration().getString("stock_prices.mail_title", "[sena]株価情報インポートバッチ実行結果");

	/**
	 * 株価情報インポートバッチクラスコンストラクタ。
	 * @since 1.1.0-1.2
	 */
	public ImportStockPrices() {
		// メールタイトル
		this.emailTitle  = IMPORT_STOCK_PRICES_MAIL_TITLE;
		// ログファイル名
		this.logFileName = getClass().getName() + "." + this.logDateFormat.format(new Date()) + ".log";
		// ログファイルpath
		this.logFilePath = LOG_FILE_PATH + getClass().getName() + "/" + this.logFileName;
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(java.util.List)
	 * @since 1.1.0-1.2
	 */
	@Override
	protected void run(JobArgument args) {
		AppLogger.info("株価情報インポートバッチ　開始");

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
					// 外部サイトから株価情報を取得
					List<StockPricesEntity> stockPricesEntityList = ScrapingComponent.getStockPricesList(stockCode, args.startDate, args.endDate);

					if (CheckUtils.isEmpty(stockPricesEntityList)) {
						AppLogger.warn(new StringBuffer("株価リストが取得できませんでした。：")
								.append(stockCode).append(":").append(stockName)
								.toString());

						skip++;
					} else {

						// モデルに詰め替えDBに保存
						int ret = _saveStockPrices(stockCode, stockPricesEntityList);
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
					AppLogger.error(new StringBuffer("外部サイトから株価情報が取得できませんでした。：")
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
		AppLogger.info("株価情報インポートバッチ　終了：処理件数=" + String.valueOf(success + error + skip) + ", 成功件数=" + success + ", 失敗件数=" + error + ", スキップ件数=" + skip);
	}

	/**
	 * エンティティから、モデルにデータを詰め替えDBに保存する。
	 * 時間はかかるが、バルクインサートは使用せず1件ずつ処理を行う。
	 *
	 * @param stockCode 銘柄コード
	 * @param stockPricesEntityList 株価エンティティのリスト
	 * @return 1件インポートが成功したら（0..成功）、ただし例外が発生していたら（1..失敗）、1件も処理しなかったら（2..スキップ）
	 * @since 1.1.0-1.2
	 */
	private int _saveStockPrices(Integer stockCode, List<StockPricesEntity> stockPricesEntityList) {
		int ret = 2;
		return ret;
	}
}
