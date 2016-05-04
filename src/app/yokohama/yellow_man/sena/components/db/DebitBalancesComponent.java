package yokohama.yellow_man.sena.components.db;

import java.util.ArrayList;
import java.util.List;

import yokohama.yellow_man.common_tools.ListUtils;
import yokohama.yellow_man.sena.core.models.DebitBalances;

/**
 * 信用残（debit_balances）モデルの操作を行うコンポーネントクラス。
 * <p>共通コンポーネント{@link yokohama.yellow_man.sena.core.components.db.DebitBalancesComponent}を拡張する。
 *
 * @author yellow-man
 * @since 1.0
 * @see yokohama.yellow_man.sena.core.components.db.DebitBalancesComponent
 */
public class DebitBalancesComponent extends yokohama.yellow_man.sena.core.components.db.DebitBalancesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 信用残テーブルより直近50件の公表日（ミリ秒）降順のリストを返す。
	 *
	 * @param stockCode 銘柄コード
	 * @return 直近50件の公表日（ミリ秒）降順のリストを返す。
	 * @since 1.0
	 */
	public static List<Long> getReleaseDateTimeByStockCode(Integer stockCode) {

		List<DebitBalances> debitBalancesList = getDebitBalancesListByStockCode(stockCode, 50, 1);

		List<Long> retList = null;
		if (!ListUtils.isEmpty(debitBalancesList)) {
			retList = new ArrayList<>();
			for (DebitBalances debitBalances : debitBalancesList) {
				retList.add(Long.valueOf(debitBalances.releaseDate.getTime()));
			}
		}

		return retList;
	}
}
