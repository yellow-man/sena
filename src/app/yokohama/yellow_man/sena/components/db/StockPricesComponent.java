package yokohama.yellow_man.sena.components.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yokohama.yellow_man.common_tools.util.CheckUtils;
import yokohama.yellow_man.sena.core.models.StockPrices;

/**
 * 株価（stock_prices）モデルの操作を行うコンポーネントクラス。
 * <p>共通コンポーネント{@link yokohama.yellow_man.sena.core.components.db.StockPricesComponent}を拡張する。
 *
 * @author yellow-man
 * @since 1.1.0-1.2
 * @see yokohama.yellow_man.sena.core.components.db.StockPricesComponent
 */
public class StockPricesComponent extends yokohama.yellow_man.sena.core.components.db.StockPricesComponent {

	/**
	 * 検索条件に銘柄コード（{@code stock_code}）を指定し、
	 * 株価テーブルより指定日付範囲内{@code startDate} 〜 {@code endDate}の取得日（ミリ秒）降順のリストを返す。
	 *
	 * @param stockCode 銘柄コード
	 * @param startDate 取得開始日（以上）
	 * @param endDate 取得終了日（以下）
	 * @return 指定日付範囲内{@code startDate} 〜 {@code endDate}の取得日（ミリ秒）降順のリストを返す。
	 * @since 1.1.0-1.2
	 */
	public static List<Long> getDateTimeListByStockCode(Integer stockCode, Date startDate, Date endDate) {

		List<StockPrices> stockPricesList = getStockPricesListByStockCode(stockCode, startDate, endDate);

		List<Long> retList = null;
		if (!CheckUtils.isEmpty(stockPricesList)) {
			retList = new ArrayList<>();
			for (StockPrices stockPrices : stockPricesList) {
				retList.add(Long.valueOf(stockPrices.date.getTime()));
			}
		}

		return retList;
	}
}
