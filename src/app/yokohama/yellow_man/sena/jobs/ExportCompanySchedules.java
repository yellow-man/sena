package yokohama.yellow_man.sena.jobs;

import java.util.Date;
import java.util.List;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.components.db.CompanySchedulesComponent;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.components.google.CalendarComponent;
import yokohama.yellow_man.sena.core.components.google.GoogleApiException;
import yokohama.yellow_man.sena.core.models.CompanySchedules;
import yokohama.yellow_man.sena.core.models.ext.CompanySchedulesWithStocks;

/**
 * 企業スケジュールエクスポートバッチクラス。
 * 企業スケジュール（company_schedules）テーブルの情報を、googleカレンダーにエクスポートする。
 * <p>googleカレンダーAPIコールに対するレイトリミット：
 * <ul>
 * <li>requests/day：1,000,000
 * <li>requests/100seconds/user：500
 * </ul>
 * レイトリミットに掛からないよう、1秒1件のペースで処理を行う。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ExportCompanySchedules extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code export_company_schedules.mail_title}キーにて値の変更可。
	 */
	private static final String EXPORT_COMPANY_SCHEDULES_MAIL_TITLE    = Play.application().configuration().getString("export_company_schedules.mail_title", "[sena]企業スケジュールエクスポートバッチ実行結果");

	/**
	 * 企業スケジュールエクスポートバッチクラスコンストラクタ。
	 * @since 1.0
	 */
	public ExportCompanySchedules() {
		// メールタイトル
		this.emailTitle  = EXPORT_COMPANY_SCHEDULES_MAIL_TITLE;
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
		AppLogger.info("企業スケジュールエクスポートバッチ　開始");

		// 成功件数
		int success = 0;
		// エラー件数
		int error = 0;

		// 企業スケジュール情報取得
		List<CompanySchedulesWithStocks> companySchedulesList = CompanySchedulesComponent.getCompanySchedulesUnregistList();
		if (CheckUtils.isEmpty(companySchedulesList)) {
			AppLogger.warn("企業スケジュール情報が取得できませんでした。");

		} else {
			CalendarComponent calendarComponent = null;
			try {
				calendarComponent = new CalendarComponent();
			} catch (GoogleApiException e) {
				AppLogger.error("カレンダーコンポーネント初期化処理に失敗しました。", e);
				// 処理の継続は不可能
				return;
			}

			for (CompanySchedulesWithStocks companySchedules : companySchedulesList) {
				Integer stockCode = companySchedules.stockCode;
				Date settlementDate = companySchedules.settlementDate;

				String stockName = "";
				if (companySchedules.stocks != null) {
					stockName = companySchedules.stocks.stockName;
				} else {
					AppLogger.warn(new StringBuffer("銘柄情報が取得できませんでした。：")
							.append(stockCode).append(":").append(stockName)
							.toString());
				}

				try {
					// カレンダー登録を行う。
					calendarComponent.insertEvent(settlementDate, stockCode, stockName);

					// 登録が完了したらDB更新処理を行う。
					CompanySchedules updCompanySchedules = companySchedules;
					updCompanySchedules.regCalendarFlg = true;
					updCompanySchedules.save();

					success++;
				} catch (Exception e) {

					AppLogger.error(new StringBuffer("カレンダー登録処理に失敗しました。：")
							.append(stockCode).append(":").append(stockName)
							.toString(), e);

					error++;
				}

				// インターバル(1秒)
				try {
					Thread.sleep(1000 * 1);
				} catch (InterruptedException e) {
					AppLogger.error("インターバル取得時にエラーが発生しました。", e);
				}
			}
		}
		AppLogger.info("企業スケジュールエクスポートバッチ　終了：処理件数=" + String.valueOf(success + error) + ", 成功件数=" + success + ", 失敗件数=" + error);
	}
}
