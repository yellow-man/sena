package yokohama.yellow_man.sena.core.components.db;

import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.sena.core.models.DebitBalances;

/**
 * 信用残（debit_balances）モデルの操作を行うコンポーネントクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class DebitBalancesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 公表日（{@code release_date}）降順の信用残情報を{@code limit}件数分返す。
	 *
	 * @param stockCode 銘柄コード
	 * @param limit 取得リミット件数
	 * @param page 取得ページ番号
	 * @return 公表日（{@code release_date}）降順の信用残情報を{@code limit}件数分返す。
	 * @since 1.0
	 */
	public static List<DebitBalances> getDebitBalancesListByStockCode(Integer stockCode, int limit, int page) {
		List<DebitBalances> retList =
				Ebean.find(DebitBalances.class)
					.where()
					.eq("delete_flg", false)
					.eq("stock_code", stockCode)
					.orderBy("release_date DESC, id DESC")
					.findPagingList(limit)
					.setFetchAhead(false)
					.getPage(page - 1)
					.getList();

		return retList;
	}

}
