package yokohama.yellow_man.sena.components.db;

import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.sena.core.models.ext.CompanySchedulesWithStocks;

/**
 * 企業スケジュール（company_schedules）モデルの操作を行うコンポーネントクラス。
 * <p>共通コンポーネント{@link yokohama.yellow_man.sena.core.components.db.CompanySchedulesComponent}を拡張する。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @see yokohama.yellow_man.sena.core.components.db.CompanySchedulesComponent
 */
public class CompanySchedulesComponent extends yokohama.yellow_man.sena.core.components.db.CompanySchedulesComponent {

	/**
	 * 企業スケジュールカレンダー未登録一覧を取得する。
	 *
	 * @return 企業スケジュールカレンダー未登録一覧を返す。
	 * @since 1.0.0-1.0
	 */
	public static List<CompanySchedulesWithStocks> getCompanySchedulesUnregistList() {
		List<CompanySchedulesWithStocks> retList =
				Ebean.find(CompanySchedulesWithStocks.class)
					.fetch("stocks")
					.where()
					.eq("deleteFlg", false)
					.eq("regCalendarFlg", false)
					.eq("stocks.deleteFlg", false)
					.orderBy("settlementDate DESC, id DESC")
					.findList();

		return retList;
	}
}
