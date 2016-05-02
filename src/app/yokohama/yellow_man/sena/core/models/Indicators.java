package yokohama.yellow_man.sena.core.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 指標（indicators）モデル。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
public class Indicators extends AppModel {

	/** 取得日 */
	@Column(name = "date")
	public Date date;

	/** 銘柄コード */
	@Column(name = "stock_code")
	public Integer stockCode;

	/** 配当利回り（整数部：8桁、小数部：2桁） */
	@Column(name = "dividend_yield")
	public BigDecimal dividendYield;

	/** 株価収益率（PER、整数部：8桁、小数部：2桁） */
	@Column(name = "price_earnings_ratio")
	public BigDecimal priceEarningsRatio;

	/** 株価純資産倍率（PBR、整数部：8桁、小数部：2桁） */
	@Column(name = "price_book_value_ratio")
	public BigDecimal priceBookValueRatio;

	/** 1株利益（EPS、整数部：8桁、小数部：2桁） */
	@Column(name = "earnings_per_share")
	public BigDecimal earningsPerShare;

	/** 1株当たり純資産（BPS、整数部：8桁、小数部：2桁） */
	@Column(name = "book_value_per_share")
	public BigDecimal bookValuePerShare;

	/** 株主資本利益率（ROE、整数部：5桁、小数部：20桁） */
	@Column(name = "return_on_equity")
	public BigDecimal returnOnEquity;

	/** 自己資本比率（整数部：8桁、小数部：2桁） */
	@Column(name = "capital_ratio")
	public BigDecimal capitalRatio;

}
