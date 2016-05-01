package yokohama.yellow_man.sena.components.db;

import java.util.List;

import com.avaje.ebean.Ebean;

import yokohama.yellow_man.sena.components.db.dao.StocksDao;
import yokohama.yellow_man.sena.models.Stocks;

/**
 * 銘柄（stocks）モデルの操作を行うコンポーネントクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class StocksComponent extends StocksDao {

	/**
	 * 銘柄（stocks）モデルのバルクインサートを行う。
	 *
	 * @param list バルクインサート対象リスト
	 * @return INSERT結果件数を返す。
	 * @since 1.0
	 * @see StocksDao#executeBulkInsert(List)
	 */
	public static int executeBulkInsert(List<Stocks> list) {
		return StocksDao.executeBulkInsert(list);
	}

	/**
	 * 銘柄（stocks）の削除フラグ無効（{@code false}）のレコードを全件削除（{@code true}）する。
	 * @return UPDATE結果件数を返す。
	 * @since 1.0
	 * @see StocksDao#deleteAll()
	 */
	public static int deleteAll() {
		return StocksDao.deleteAll();
	}

	/**
	 * 未削除の銘柄（stocks）情報一覧を返す。
	 * @return 未削除の銘柄（stocks）情報一覧
	 * @since 1.0
	 */
	public static List<Stocks> getStocksList() {
		List<Stocks> stocksList =
				Ebean.find(Stocks.class)
					.where()
					.eq("delete_flg", false)
					.orderBy("id ASC")
					.findList();

		return stocksList;
	}
}
