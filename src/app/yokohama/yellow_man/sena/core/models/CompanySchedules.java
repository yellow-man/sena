package yokohama.yellow_man.sena.core.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 企業スケジュール（company_schedules）モデル。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
public class CompanySchedules extends AppModel {

	/** 決算発表日 */
	@Column(name = "settlement_date")
	public Date settlementDate;

	/** 銘柄コード */
	@Column(name = "stock_code")
	public Integer stockCode;

	/** 決算期（サンプル：4月期、12月期） */
	@Column(name = "settlement")
	public String settlement;

	/** 決算種別（サンプル：1..第１、2..第２、3..第３、4..本） */
	@Column(name = "settlement_types_id")
	public Integer settlementTypesId;

	/** カレンダー登録済みフラグ（1：登録済み、0：未登録） */
	@Column(name = "reg_calendar_flg")
	public Boolean regCalendarFlg;

}
