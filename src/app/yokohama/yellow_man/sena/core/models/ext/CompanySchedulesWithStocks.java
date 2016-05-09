package yokohama.yellow_man.sena.core.models.ext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import yokohama.yellow_man.sena.core.models.CompanySchedules;
import yokohama.yellow_man.sena.core.models.Stocks;

/**
 * 企業スケジュール（company_schedules）モデル拡張。
 * 銘柄情報を保持する。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity @Table(name="company_schedules")
public class CompanySchedulesWithStocks extends CompanySchedules {

	/** 銘柄（stocks）モデル */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "stock_code", referencedColumnName="stock_code")
	public Stocks stocks;
}
