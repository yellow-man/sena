package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;

import yokohama.yellow_man.common_tools.util.CheckUtils;
import yokohama.yellow_man.common_tools.util.FieldUtils;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.StockPricesEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument;

/**
 * バッチ処理テストクラス。
 * <p>バッチ疎通確認等に使用する。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.0-1.2
 */
public class TestJob extends AppLoggerMailJob {

	/**
	 * バッチ処理テストクラスコンストラクタ。
	 * @since 1.0.0-1.0
	 */
	public TestJob() {
		// メールタイトル
		this.emailTitle  = "[sena]" + "バッチテスト";
		// ログファイル名
		this.logFileName = getClass().getName() + "." + this.logDateFormat.format(new Date()) + ".log";
		// ログファイルpath
		this.logFilePath = LOG_FILE_PATH + getClass().getName() + "/" + this.logFileName;
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument)
	 * @since 1.1.0-1.2
	 */
	@Override
	protected void run(JobArgument args) {

		// ログ出力テスト
		AppLogger.debug("debugテスト");
		AppLogger.info("infoテスト");
		AppLogger.warn("warnテスト");
		AppLogger.error("errorテスト");
		AppLogger.error("errorテスト", new Exception());

		AppLogger.info("infoテスト:" + FieldUtils.toStringField(args));

		try {
			List<StockPricesEntity> stockPricesEntityList = ScrapingComponent.getStockPricesList(1712, args.startDate, args.endDate, null);

			int size = 0;
			if (!CheckUtils.isEmpty(stockPricesEntityList)) {
				size = stockPricesEntityList.size();
				for (StockPricesEntity stockPricesEntity : stockPricesEntityList) {
					AppLogger.debug(stockPricesEntity.toString());
				}
			}
			System.out.println("取得件数は " + size + " 件です。");
		} catch (ScrapingException e) {
			e.printStackTrace();
		}
	}
}
