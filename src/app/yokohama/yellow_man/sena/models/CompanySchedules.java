package yokohama.yellow_man.sena.models;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	/** 決算種別：1..第１ */
	public static final Integer SETTLEMENT_TYPES_ID_1 = 1;
	/** 決算種別：2..第２ */
	public static final Integer SETTLEMENT_TYPES_ID_2 = 2;
	/** 決算種別：3..第３ */
	public static final Integer SETTLEMENT_TYPES_ID_3 = 3;
	/** 決算種別：4..本 */
	public static final Integer SETTLEMENT_TYPES_ID_4 = 4;

	/** 決算種別：マッピング */
	public static final Map<String, Integer> SETTLEMENT_TYPES_ID_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>() {
		{
			put("第１", SETTLEMENT_TYPES_ID_1);
			put("第２", SETTLEMENT_TYPES_ID_2);
			put("第３", SETTLEMENT_TYPES_ID_3);
			put("本"  , SETTLEMENT_TYPES_ID_4);
		}
	});
}
