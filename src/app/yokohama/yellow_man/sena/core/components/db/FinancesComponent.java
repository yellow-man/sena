package yokohama.yellow_man.sena.core.components.db;

import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.sena.core.models.Finances;

/**
 * 財務（finances）モデルの操作を行うコンポーネントクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class FinancesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 決算年（{@code year}）降順、決算種別（{@code settlement_types_id}）降順の財務情報を{@code limit}件数分返す。
	 *
	 * @param stockCode 銘柄コード
	 * @param limit 取得リミット件数
	 * @param page 取得ページ番号
	 * @return 決算年（{@code year}）降順、決算種別（{@code settlement_types_id}）降順の財務情報を{@code limit}件数分返す。
	 * @since 1.0
	 */
	public static List<Finances> getFinancesListByStockCode(Integer stockCode, int limit, int page) {
		List<Finances> retList =
				Ebean.find(Finances.class)
					.where()
					.eq("delete_flg", false)
					.eq("stock_code", stockCode)
					.orderBy("year DESC, settlement_types_id DESC")
					.findPagingList(limit)
					.setFetchAhead(false)
					.getPage(page - 1)
					.getList();

		return retList;
	}
}
