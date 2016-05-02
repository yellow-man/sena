package yokohama.yellow_man.sena.components.db.dao;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

import yokohama.yellow_man.module.models.Stocks;
import yokohama.yellow_man.sena.components.db.StocksComponent;

/**
 * 銘柄（stocks）モデルのネイティブクエリによる操作クラス。
 * <p>アプリケーションとは祖結合としたい為、直接の参照は行わない。
 * <br>利用する場合、{@link StocksComponent}を経由しアクセスする。
 *
 * @author yellow-man
 * @since 1.0
 */
public class StocksDao {

	/**
	 * 銘柄（stocks）モデルのバルクインサートを行う。
	 * @param list バルクインサート対象リスト
	 * @return INSERT結果件数を返す。
	 * @since 1.0
	 */
	protected static int executeBulkInsert(List<Stocks> list) {

		// SQLテンプレート作成
		StringBuffer sqlStringBuffer =
				new StringBuffer("INSERT INTO stocks (date, stock_code, stock_name, market, topix_sector, share_unit, nikkei225_flg, created, modified, delete_flg) VALUES ");

		for (int i = 0; i < list.size(); i++) {
			if (0 == i) {
				sqlStringBuffer.append("(");
			} else {
				sqlStringBuffer.append(", (");
			}
			sqlStringBuffer
				.append(":date_").append(i).append(", ")
				.append(":stock_code_").append(i).append(", ")
				.append(":stock_name_").append(i).append(", ")
				.append(":market_").append(i).append(", ")
				.append(":topix_sector_").append(i).append(", ")
				.append(":share_unit_").append(i).append(", ")
				.append(":nikkei225_flg_").append(i).append(", ")
				.append(":created_").append(i).append(", ")
				.append(":modified_").append(i).append(", ")
				.append(":delete_flg_").append(i)
			.append(")");
		}

		// バルクインサート用SQL作成
		SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sqlStringBuffer.toString());
		int j = 0;
		for (Stocks items : list) {
			sqlUpdate.setParameter("date_" + j, items.date);
			sqlUpdate.setParameter("stock_code_" + j, items.stockCode);
			sqlUpdate.setParameter("stock_name_" + j, items.stockName);
			sqlUpdate.setParameter("market_" + j, items.market);
			sqlUpdate.setParameter("topix_sector_" + j, items.topixSector);
			sqlUpdate.setParameter("share_unit_" + j, items.shareUnit);
			sqlUpdate.setParameter("nikkei225_flg_" + j, items.nikkei225Flg);
			sqlUpdate.setParameter("created_" + j, items.created);
			sqlUpdate.setParameter("modified_" + j, items.modified);
			sqlUpdate.setParameter("delete_flg_" + j, items.deleteFlg);
			j++;
		}

		// インサート実行
		return Ebean.execute(sqlUpdate);
	}

	/**
	 * 銘柄（stocks）の削除フラグ無効（{@code false}）のレコードを全件削除（{@code true}）する。
	 * @return UPDATE結果件数を返す。
	 * @since 1.0
	 */
	protected static int deleteAll() {
		String sql = "UPDATE stocks SET delete_flg = true, modified = :modified WHERE delete_flg = false";
		SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sql);
		sqlUpdate.setParameter("modified", new Date());
		return sqlUpdate.execute();
	}
}
