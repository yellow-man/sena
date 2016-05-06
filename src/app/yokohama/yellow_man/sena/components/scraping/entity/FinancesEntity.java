package yokohama.yellow_man.sena.components.scraping.entity;

/**
 * 企業財務エンティティ。
 * <p>スクレイピング結果の企業財務情報を保持する。
 *
 * @author yellow-man
 * @since 1.0
 */
public class FinancesEntity extends EntityBase {

	/** 決算年 */
	public Integer year;

	/** 決算期（1..第1四、2..第2四、3..第3四、4..通期） */
	public Integer settlementTypesId;

	/** 銘柄コード */
	public Integer stockCode;

	/** 売上高 */
	public Integer sales;

	/** 営業益 */
	public Integer operatingProfit;

	/** 純利益 */
	public Integer netProfit;

	/** 売上高(前年比) */
	public Double salesRate;

	/** 営業益(前年比) */
	public Double operatingProfitRate;

	/** 純利益(前年比) */
	public Double netProfitRate;

}
