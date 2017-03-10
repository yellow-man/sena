package yokohama.yellow_man.sena.jobs;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import play.Play;
import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.components.db.FinancesComponent;
import yokohama.yellow_man.sena.components.db.StocksComponent;
import yokohama.yellow_man.sena.core.components.AppLogger;
import yokohama.yellow_man.sena.core.definitions.AppConsts;
import yokohama.yellow_man.sena.core.models.Finances;
import yokohama.yellow_man.sena.core.models.Stocks;
import yokohama.yellow_man.sena.jobs.JobExecutor.JobArgument;

/**
 * 企業財務情報前年比更新バッチクラス。
 * <p>財務（finances）テーブルの下記情報を更新する。
 * <ul>
 * <li>売上高（前年比）sales_rate
 * <li>営業益（前年比）operating_profit_rate
 * <li>純利益（前年比）net_profit_rate
 * </ul>
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.0-1.2
 */
public class UpdateFinancesSetRate extends AppLoggerMailJob {

	/**
	 * メールタイトル
	 * {@code application.conf}ファイル{@code update_finances_set_rate.mail_title}キーにて値の変更可。
	 */
	private static final String UPDATE_FINANCES_SET_RATE_MAIL_TITLE    = Play.application().configuration().getString("update_finances_set_rate.mail_title", "[sena]企業財務情報前年比更新バッチ実行結果");

	/**
	 * 企業財務情報前年比更新バッチクラスコンストラクタ。
	 * @since 1.0.0-1.0
	 */
	public UpdateFinancesSetRate() {
		// メールタイトル
		this.emailTitle  = UPDATE_FINANCES_SET_RATE_MAIL_TITLE;
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
		AppLogger.info("企業財務情報前年比更新バッチ　開始");

		// 成功件数
		int success = 0;
		// エラー件数
		int error = 0;

		// 銘柄情報取得
		List<Stocks> stocksList = StocksComponent.getStocksList();
		if (CheckUtils.isEmpty(stocksList)) {
			AppLogger.warn("銘柄情報が取得できませんでした。");

		} else {
			for (Stocks stocks : stocksList) {
				Integer stockCode = stocks.stockCode;
				String stockName = stocks.stockName;

				boolean isError = false;
				for (Integer settlementTypesId : AppConsts.SETTLEMENT_TYPES_ID_LIST) {
					try {
						// 企業財務を取得
						List<Finances> financesList = FinancesComponent.getFinancesByStockCodeSettlementTypesIdList(stockCode, settlementTypesId);

						if (CheckUtils.isEmpty(financesList)) {
							AppLogger.warn(new StringBuffer("財務リストが取得できませんでした。：")
									.append(stockCode).append(":").append(stockName).append(":").append(AppConsts.SETTLEMENT_TYPES_ID_MAP_STR.get(settlementTypesId))
									.toString());

						} else {
							int size = financesList.size();
							if(size > 1){
								for(int j = 0; j + 1 < size; j++){
									Finances finance = financesList.get(j);
									Finances nextYearFinance = financesList.get(j + 1);

									if (finance.salesRate == null) {
										BigDecimal financeSales = new BigDecimal(finance.sales);
										BigDecimal nextYearFinanceSales = new BigDecimal(nextYearFinance.sales);
										finance.salesRate = financeSales.subtract(nextYearFinanceSales).divide(nextYearFinanceSales, 25, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));;
									}

									if (finance.operatingProfitRate == null) {
										BigDecimal financeOperatingProfit = new BigDecimal(finance.operatingProfit);
										BigDecimal nextYearFinanceOperatingProfit = new BigDecimal(nextYearFinance.operatingProfit);
										finance.operatingProfitRate = financeOperatingProfit.subtract(nextYearFinanceOperatingProfit).divide(nextYearFinanceOperatingProfit, 25, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));;
									}

									if (finance.netProfitRate == null) {
										BigDecimal financeNetProfit = new BigDecimal(finance.netProfit);
										BigDecimal nextYearFinanceNetProfit = new BigDecimal(nextYearFinance.netProfit);
										finance.netProfitRate = financeNetProfit.subtract(nextYearFinanceNetProfit).divide(nextYearFinanceNetProfit, 25, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));;
									}

									finance.save();
								}
							}
						}
					} catch (Exception e) {
						AppLogger.error(new StringBuffer("財務DB保存時にエラーが発生しました。：")
								.append(stockCode).append(":").append(stockName).append(":").append(AppConsts.SETTLEMENT_TYPES_ID_MAP_STR.get(settlementTypesId))
								.toString(), e);

						isError = true;
						continue;
					}
				}

				if (isError) {
					error++;
				} else {
					success++;
				}
			}
		}
		AppLogger.info("企業財務情報前年比更新バッチ　終了：処理件数=" + String.valueOf(success + error) + ", 成功件数=" + success + ", 失敗件数=" + error);
	}
}
