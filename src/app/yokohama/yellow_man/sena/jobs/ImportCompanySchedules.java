package yokohama.yellow_man.sena.jobs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.common_tools.DateUtils;
import yokohama.yellow_man.sena.components.db.CompanySchedulesComponent;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingComponent;
import yokohama.yellow_man.sena.components.scraping.ScrapingException;
import yokohama.yellow_man.sena.components.scraping.entity.CompanySchedulesEntity;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.definitions.AppConsts;
import yokohama.yellow_man.sena.core.models.CompanySchedules;
import yokohama.yellow_man.sena.core.models.Stocks;
import yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument;

/**
 * 企業スケジュールインポートバッチクラス。
 * <p>銘柄情報（stocks）を元に企業スケジュールを取得し、
 * 企業スケジュール（company_schedules）テーブルにインポートする。
 * 相手方にアクセス負荷とならぬよう、1件取得ごとにランダムでインターバル(2秒～5秒)を設けている。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.0-1.2
 */
public class ImportCompanySchedules extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code import_company_schedules.mail_title}キーにて値の変更可。
	 */
	private static final String IMPORT_STOCKS_MAIL_TITLE    = Play.application().configuration().getString("import_company_schedules.mail_title", "[sena]企業スケジュールインポートバッチ実行結果");

	/**
	 * 企業スケジュールインポートバッチクラスコンストラクタ。
	 * @since 1.0.0-1.0
	 */
	public ImportCompanySchedules() {
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
		AppLogger.info("企業スケジュールインポートバッチ　開始");

		// 現在日時
		Date now = new Date();
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

				// DBに保存されている企業スケジュールを取得する。
				CompanySchedules sompanySchedules = CompanySchedulesComponent.getCompanySchedulesByStockCode(stockCode);

				// 決算発表日から２ヶ月経過経過していたら処理を継続する
				Date settlementDate = null;
				if (sompanySchedules != null) {
					settlementDate = DateUtils.addDay(sompanySchedules.settlementDate, 60);
				}
				if (settlementDate == null || settlementDate.getTime() < now.getTime()) {

					AppLogger.info(new StringBuffer("外部サイトから企業スケジュール取得を開始します。：")
							.append(stockCode).append("：").append(stockName)
							.toString());
					try {
						// 外部サイトから企業スケジュールを取得
						CompanySchedulesEntity companySchedulesEntity = ScrapingComponent.getSchedule(stockCode);
						// 取得できない場合は、処理を飛ばす。
						if (companySchedulesEntity == null) {

							AppLogger.warn(new StringBuffer("外部サイトから企業スケジュールが取得できませんでした。インポート処理をスキップします。：")
									.append(stockCode).append("：").append(stockName)
									.toString());
							skip++;
							continue;

						}
						AppLogger.info(new StringBuffer("外部サイトから企業スケジュールが取得できました。：")
								.append(companySchedulesEntity.stockCode).append("：").append(companySchedulesEntity.stockName)
								.toString());

						// モデルに詰め替えDBに保存
						boolean ret = _saveCompanySchedules(companySchedulesEntity);
						if (ret) {
							success++;
						} else {
							error++;
						}

					} catch (ScrapingException e) {

						// 取得できない場合は、処理を飛ばす。
						AppLogger.error(new StringBuffer("外部サイトから企業スケジュールが取得できませんでした。：")
								.append(stockCode).append("：").append(stockName)
								.toString(), e);

						error++;
						continue;
					}

				} else {
					AppLogger.info(new StringBuffer("決算発表日から２ヶ月（６０日）経過していませんでした。取得処理をスキップします。：stockCode=").append(stockCode)
							.append(", stockName=").append(stockName)
							.toString());
					skip++;
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
		AppLogger.info("企業スケジュールインポートバッチ　終了：処理件数=" + String.valueOf(success + error + skip) + ", 成功件数=" + success + ", 失敗件数=" + error + ", スキップ件数=" + skip);
	}

	/**
	 * エンティティから、モデルにデータを詰め替えDBに保存する。
	 * @param companySchedulesEntity 企業スケジュールエンティティ
	 * @return インポートが成功したら{@code true}、失敗したら{@code false}
	 * @since 1.0.0-1.0
	 */
	private boolean _saveCompanySchedules(CompanySchedulesEntity companySchedulesEntity) {
		boolean ret = false;
		Integer stockCode = companySchedulesEntity.stockCode;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_YYYY_M_D);
		CompanySchedules companySchedules = new CompanySchedules();
		try {
			companySchedules.settlementDate    = simpleDateFormat.parse(companySchedulesEntity.settlementDateStr);
			companySchedules.stockCode         = stockCode;
			companySchedules.settlement        = companySchedulesEntity.settlement;
			companySchedules.settlementTypesId = AppConsts.SETTLEMENT_TYPES_ID_MAP.get(companySchedulesEntity.settlementType);
			companySchedules.regCalendarFlg    = false;
			companySchedules.created           = new Date();
			companySchedules.modified          = new Date();
			companySchedules.deleteFlg         = false;
			companySchedules.save();
			ret = true;
		} catch (ParseException e) {
			AppLogger.error("エンティティから、モデルにデータを詰め替え時にエラーが発生しました。：companySchedulesEntity.settlementDateStr=" + companySchedulesEntity.settlementDateStr, e);
		} catch (Exception e) {
			AppLogger.error("企業スケジュールDB保存時にエラーが発生しました。：companySchedulesEntity=" + companySchedulesEntity, e);
		}
		return ret;
	}
}
