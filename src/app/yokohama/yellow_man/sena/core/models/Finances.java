package yokohama.yellow_man.sena.core.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 財務（finances）モデル。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
public class Finances extends AppModel {

	/** 決算年 */
	@Column(name = "year")
	public Integer year;

	/** 決算種別（サンプル：1..第１、2..第２、3..第３、4..本） */
	@Column(name = "settlement_types_id")
	public Integer settlementTypesId;

	/** 銘柄コード */
	@Column(name = "stock_code")
	public Integer stockCode;

	/** 売上高 */
	@Column(name = "sales")
	public Integer sales;

	/** 営業益 */
	@Column(name = "operating_profit")
	public Integer operatingProfit;

	/** 純利益 */
	@Column(name = "net_profit")
	public Integer netProfit;

	/** 売上高（前年比、整数部：10桁、小数部：20桁） */
	@Column(name = "sales_rate")
	public BigDecimal salesRate;

	/** 営業益（前年比、整数部：10桁、小数部：20桁） */
	@Column(name = "operating_profit_rate")
	public BigDecimal operatingProfitRate;

	/** 純利益（前年比、整数部：10桁、小数部：20桁） */
	@Column(name = "net_profit_rate")
	public BigDecimal netProfitRate;

}
