package yokohama.yellow_man.sena.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 信用残（debit_balances）モデル。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
public class DebitBalances extends AppModel {

	/** 公開日 */
	@Column(name = "release_date")
	public Date releaseDate;

	/** 銘柄コード */
	@Column(name = "stock_code")
	public Integer stockCode;

	/** 信用売残（ハイフン等、数値に変換できない場合：-1） */
	@Column(name = "margin_selling_balance")
	public Integer marginSellingBalance;

	/** 信用買残（ハイフン等、数値に変換できない場合：-1） */
	@Column(name = "margin_debt_balance")
	public Integer marginDebtBalance;

	/** 信用倍率（整数部：8桁、小数部：2桁、ハイフン等、数値に変換できない場合：-1） */
	@Column(name = "ratio_margin_balance")
	public BigDecimal ratioMarginBalance;

}
