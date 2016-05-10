package yokohama.yellow_man.sena.components.db;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.common_tools.ListUtils;
import yokohama.yellow_man.sena.core.models.Finances;

/**
 * 財務（finances）モデルの操作を行うコンポーネントクラス。
 * <p>共通コンポーネント{@link yokohama.yellow_man.sena.core.components.db.FinancesComponent}を拡張する。
 *
 * @author yellow-man
 * @since 1.0
 * @see yokohama.yellow_man.sena.core.components.db.FinancesComponent
 */
public class FinancesComponent extends yokohama.yellow_man.sena.core.components.db.FinancesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 財務テーブルより直近50件の公表日（ミリ秒）降順のリストを返す。
	 *
	 * @param stockCode 銘柄コード
	 * @return 決算年（{@code year}）降順、決算種別（{@code settlement_types_id}）降順のリストを返す。
	 * @since 1.0
	 */
	public static List<String> getYearSettlementTypesIdByStockCode(Integer stockCode) {

		List<Finances> financesList = getFinancesListByStockCode(stockCode, 50, 1);

		List<String> retList = null;
		if (!ListUtils.isEmpty(financesList)) {
			retList = new ArrayList<>();
			for (Finances debitBalances : financesList) {
				retList.add(new StringBuffer(debitBalances.year).append("_").append(debitBalances.settlementTypesId).toString());
			}
		}

		return retList;
	}

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）、決算種別（{@code settlementTypesId}）を指定し、
	 * 財務テーブルより決算年（降順）で一覧を取得する。
	 *
	 * @param stockCode 銘柄コード
	 * @param settlementTypesId 決算種別（1..第１、2..第２、3..第３、4..本）
	 * @return 決算年（降順）のリストを返す。
	 * @since 1.0
	 */
	public static List<Finances> getFinancesByStockCodeSettlementTypesIdList(Integer stockCode, Integer settlementTypesId) {
		List<Finances> retList =
				Ebean.find(Finances.class)
					.where()
					.eq("delete_flg", false)
					.eq("stock_code", stockCode)
					.eq("settlement_types_id", settlementTypesId)
					.orderBy("year DESC")
					.findList();

		return retList;
	}
}
