package yokohama.yellow_man.sena.jobs;

import java.text.SimpleDateFormat;

import play.Play;
import yokohama.yellow_man.common_tools.DateUtils;
import yokohama.yellow_man.sena.components.AppLogger;

/**
 * バッチ処理固有のログ出力を行う基底クラス。
 * <p>バッチ処理ごとの個別ログを出力する場合、このクラスを継承する。
 *
 * @author yellow-man
 * @since 1.0
 * @see AppJob
 */
public abstract class AppLoggerJob extends AppJob {

	/** ログ：ログ出力先 */
	public static final String LOG_FILE_PATH = Play.application().configuration().getString("log.file.path", "logs/");

	/** ログ：日付フォーマット（dd）1ヶ月分保持 */
	protected SimpleDateFormat logDateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DD);
	/** ログ：ログファイル名 */
	protected String logFileName = "";
	/** ログ：ログファイルpath */
	protected String logFilePath = "";

	/**
	 * ログ出力先の切り替え処理を追加。
	 * @see yokohama.yellow_man.sena.jobs.AppJob#init()
	 * @since 1.0
	 */
	@Override
	protected boolean init() {
		// ログ出力先の切替処理
		AppLogger.addLoggerFileAppender(AppLogger.APP_LOGGER_NAME + "." + getClass().getName(), this.logFilePath);
		return super.init();
	}

	/**
	 * ログ出力先の切り替え処理を追加。
	 * @see yokohama.yellow_man.sena.jobs.AppJob#_finally()
	 * @since 1.0
	 */
	@Override
	protected void _finally() {
		// ログ出力先の切替処理
		AppLogger.resetLogger();
		super._finally();
	}
}
