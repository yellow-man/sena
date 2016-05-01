package yokohama.yellow_man.sena.components.scraping.entity;

/**
 * 企業指標エンティティ。
 * <p>スクレイピング結果の企業指標情報を保持する。
 *
 * @author yellow-man
 * @since 1.0
 */
public class IndicatorsEntity extends EntityBase {

	/** 日付 */
	public String date;

	/** 銘柄コード */
	public int stockCode;

	/** 配当利回り */
	public String dividendYield;

	/** 株価収益率(PER) */
	public String priceEarningsRatio;

	/** 株価純資産倍率(PBR) */
	public String priceBookValueRatio;

	/** 1株利益(EPS) */
	public String earningsPerShare;

	/** 1株当たり純資産(BPS) */
	public String bookValuePerShare;

	/** 株主資本利益率(ROE) */
	public String returnOnEquity;

	/** 自己資本比率 */
	public String capitalRatio;

}
