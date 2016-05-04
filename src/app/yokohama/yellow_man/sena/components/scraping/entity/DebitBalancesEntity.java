package yokohama.yellow_man.sena.components.scraping.entity;

import java.util.Date;

/**
 * 信用残エンティティ。
 * <p>スクレイピング結果の信用残情報を保持する。
 *
 * @author yellow-man
 * @since 1.0
 */
public class DebitBalancesEntity extends EntityBase {

	/** 公開日（文字列） */
	public String releaseDateStr;

	/** 公開日（日付） */
	public Date releaseDate;

	/** 銘柄コード */
	public Integer stockCode;

	/** 信用売残 */
	public String marginSellingBalance;

	/** 信用買残 */
	public String marginDebtBalance;

	/** 信用倍率 */
	public String ratioMarginBalance;

}
