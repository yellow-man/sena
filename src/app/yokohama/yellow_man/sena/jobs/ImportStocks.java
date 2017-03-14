package yokohama.yellow_man.sena.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import au.com.bytecode.opencsv.CSVReader;
import play.Play;
import yokohama.yellow_man.common_tools.ListUtils;
import yokohama.yellow_man.common_tools.StringUtils;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.components.HttpComponent;
import yokohama.yellow_man.sena.core.models.Stocks;
import yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument;

/**
 * 銘柄一覧インポートバッチクラス。
 * <p>銘柄一覧csvファイルを取得し、銘柄（stocks）テーブルにインポートする。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.0-1.2
 */
public class ImportStocks extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code import_stocks.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCKS_MAIL_TITLE    = Play.application().configuration().getString("import_stocks.mail_title", "[sena]銘柄一覧インポートバッチ実行結果");

	/**
	 * 銘柄一覧インポートURL
	 * {@code application.conf}ファイル{@code import_stocks.csv_url}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCKS_CSV_URL       = Play.application().configuration().getString("import_stocks.csv_url", "http://kabusapo.com/dl-file/dl-stocklist.php");

	/**
	 * CSVファイル出力path
	 * {@code application.conf}ファイル{@code import_stocks.csv_file_path}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCKS_CSV_FILE_PATH = Play.application().configuration().getString("import_stocks.csv_file_path", "files/");
	/**
	 * CSVファイル名
	 * {@code application.conf}ファイル{@code import_stocks.csv_file_name}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCKS_CSV_FILE_NAME = Play.application().configuration().getString("import_stocks.csv_file_name", ImportStocks.class.getSimpleName() + "_%tF.csv");

	/**
	 * 銘柄一覧インポートバッチクラスコンストラクタ。
	 * @since 1.0.0-1.0
	 */
	public ImportStocks() {
		// メールタイトル
		this.emailTitle  = IMPORT_STOCKS_MAIL_TITLE;
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
		AppLogger.info("銘柄一覧インポートバッチ　開始");

		// 現在日時
		Date now = new Date();
		// 成功件数
		int success = 0;
		// エラー件数
		int error = 0;

		// CSVファイル取得。
		String body = HttpComponent.executeGet(IMPORT_STOCKS_CSV_URL);
		if (StringUtils.isEmptyWithTrim(body)) {
			AppLogger.error("銘柄一覧CSV取り込みに失敗しました。リクエストURLを確認してください。：URL=" + IMPORT_STOCKS_CSV_URL);
			return;
		}

		// 取り込み済みのデータを全件削除。
		int deleteCount = StocksComponent.deleteAll();
		AppLogger.info("取り込み済みデータを削除しました。：deleteCount=" + deleteCount);

		// ファイル書き込みも同時に行う。
		File file = null;
		PrintWriter printWriter = null;

		// CSV解析。
		CSVReader reader = null;
		try {
			String filePath = IMPORT_STOCKS_CSV_FILE_PATH + getClass().getName();
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String fileName = "/" + String.format(IMPORT_STOCKS_CSV_FILE_NAME, new Date());
			file = new File(filePath + fileName);
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			// ヘッダー行のため処理を飛ばす。
			String [] nextLine;
			reader = new CSVReader(new BufferedReader(new StringReader(body)));
			nextLine = reader.readNext();

			// ヘッダー行書き込み。
			printWriter.println(ListUtils.toString(Arrays.asList(nextLine)));

			while ((nextLine = reader.readNext()) != null) {
				// ボディ部書き込み。
				printWriter.println(ListUtils.toString(Arrays.asList(nextLine)));

				try {
					Stocks stocks = new Stocks();
					stocks.date               = now;
					stocks.stockCode          = Integer.parseInt(nextLine[0]);
					stocks.stockName          = nextLine[1];
					stocks.market             = nextLine[2];
					stocks.topixSector        = nextLine[3];
					stocks.shareUnit          = NumberUtils.toInt(nextLine[4], -1);
					stocks.nikkei225Flg       = BooleanUtils.toBooleanObject(nextLine[5]);
					stocks.created            = now;
					stocks.modified           = now;
					stocks.deleteFlg          = false;

					// 時間がかかるが、バルクインサートは使わず1件ずつ処理する。
					stocks.save();
					success++;

				} catch (Exception e) {
					AppLogger.error("銘柄一覧CSV解析処理に失敗しました。：nextLine=[" + ListUtils.toString(Arrays.asList(nextLine)) + "]", e);
					error++;
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
			if (printWriter != null) {
				printWriter.flush();
				printWriter.close();
			}
		}

		AppLogger.info("銘柄一覧インポートバッチ　終了：処理件数=" + String.valueOf(success + error) + ", 成功件数=" + success + ", 失敗件数=" + error);
	}
}
