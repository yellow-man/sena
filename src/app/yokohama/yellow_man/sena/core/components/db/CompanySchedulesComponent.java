package yokohama.yellow_man.sena.core.components.db;

import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.common_tools.CheckUtils;
import yokohama.yellow_man.sena.core.models.CompanySchedules;

/**
 * 企業スケジュール（company_schedules）モデルの操作を行うコンポーネントクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class CompanySchedulesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 直近の決算発表日（{@code settlement_date}）の企業スケジュールを返す。
	 *
	 * @param stockCode 銘柄コード
	 * @return 直近の決算発表日（{@code settlement_date}）の企業スケジュールを返す。
	 * 			取得できない場合は、{@code null}を返す。
	 * @since 1.0
	 */
	public static CompanySchedules getCompanySchedulesByStockCode(Integer stockCode) {
		List<CompanySchedules> list =
				Ebean.find(CompanySchedules.class)
					.where()
					.eq("delete_flg", false)
					.eq("stock_code", stockCode)
					.orderBy("settlement_date DESC")
					.findList();

		CompanySchedules ret = null;
		if (!CheckUtils.isEmpty(list)) {
			ret = list.get(0);
		}

		return ret;
	}

}
