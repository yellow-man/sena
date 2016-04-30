package yokohama.yellow_man.sena.jobs;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import au.com.bytecode.opencsv.CSVReader;
import yokohama.yellow_man.common_tools.ListUtils;
import yokohama.yellow_man.common_tools.StringUtils;
import yokohama.yellow_man.sena.components.AppLogger;
import yokohama.yellow_man.sena.components.HttpComponents;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.models.Stocks;

/**
 * 銘柄一覧インポートバッチクラス。
 * <p>銘柄一覧csvファイルを取得し、銘柄（stocks）テーブルにインポートする。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ImportStocks extends AppLoggerJob {

	/** 銘柄一覧インポートURL */
	private static final String IMPORT_STOCKS_CSV_URL = "http://kabusapo.com/dl-file/dl-stocklist.php";

	/**
	 * 銘柄一覧インポートバッチクラスコンストラクタ。
	 * @since 1.0
	 */
	public ImportStocks() {
		// ログ：ログファイル名
		this.logFileName = getClass().getName() + "." + this.logDateFormat.format(new Date()) + ".log";
		// ログ：ログファイルpath
		this.logFilePath = LOG_FILE_PATH + getClass().getName() + "/" + this.logFileName;
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(java.util.List)
	 * @since 1.0
	 */
	@Override
	protected void run(List<String> args) {

		Date now = new Date();

		// CSVファイル取得
		String body = HttpComponents.executeGet(IMPORT_STOCKS_CSV_URL);
		if (StringUtils.isEmptyWithTrim(body)) {
			AppLogger.error("銘柄一覧CSV取り込みに失敗しました。リクエストURLを確認してください。：URL=" + IMPORT_STOCKS_CSV_URL);
			return;
		}

		// 取り込み済みのデータを全件削除
		int deleteCount = StocksComponent.deleteAll();
		AppLogger.info("取り込み済みデータを削除しました。：deleteCount=" + deleteCount);

		// CSV解析
		CSVReader reader = null;
		try {
			reader = new CSVReader(new BufferedReader(new StringReader(body)));
			// ヘッダー行のため処理を飛ばす。
			reader.readNext();

			String [] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				try {
					Stocks stocks = new Stocks();
					stocks.date               = now;
					stocks.stockCode          = Integer.parseInt(nextLine[0]);
					stocks.stockName          = nextLine[1];
					stocks.market             = nextLine[2];
					stocks.topixSector        = nextLine[3];
					stocks.shareUnit          = Integer.parseInt(nextLine[4]);
					stocks.nikkei225Flg       = BooleanUtils.toBooleanObject(nextLine[5]);
					stocks.created            = now;
					stocks.modified           = now;
					stocks.deleteFlg          = false;

					// 時間がかかるが、バルクインサートは使わず1件ずつ処理する。
					stocks.save();
				} catch (Exception e) {
					AppLogger.error("銘柄一覧CSV解析処理に失敗しました。：nextLine=" + ListUtils.toString(Arrays.asList(nextLine)), e);
				}
			}
		} catch (Exception e) {
			AppLogger.error("銘柄一覧CSV解析処理に失敗しました。", e);
			return;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					AppLogger.error("銘柄一覧CSV解析処理に失敗しました。", e);
					return;
				}
			}
		}
	}
}
