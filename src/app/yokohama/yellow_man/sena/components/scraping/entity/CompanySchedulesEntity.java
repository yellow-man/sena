package yokohama.yellow_man.sena.components.scraping.entity;

/**
 * 企業スケジュールエンティティ。
 * <p>スクレイピング結果の企業スケジュール情報を保持する。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 */
public class CompanySchedulesEntity extends EntityBase {

	/** 決算発表日 */
	public String settlementDateStr;

	/** 銘柄コード */
	public Integer stockCode;

	/** 銘柄名 */
	public String stockName;

	/** 決算期（サンプル：4月期、12月期） */
	public String settlement;

	/** 決算種別（サンプル：1..第１、2..第２、3..第３、4..本） */
	public String settlementType;

	/** 上場市場 */
	public String market;

}
