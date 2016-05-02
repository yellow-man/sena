package yokohama.yellow_man.module.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 銘柄（stocks）モデル。
 *
 * @author yellow-man
 * @since 1.0
 */
@SuppressWarnings("serial")
@Entity
public class Stocks extends AppModel {

	/** 取得日 */
	@Column(name = "date")
	public Date date;

	/** 銘柄コード */
	@Column(name = "stock_code")
	public Integer stockCode;

	/** 銘柄名 */
	@Column(name = "stock_name")
	public String stockName;

	/** 市場名 */
	@Column(name = "market")
	public String market;

	/** 業種分類 */
	@Column(name = "topix_sector")
	public String topixSector;

	/** 単元株数 */
	@Column(name = "share_unit")
	public Integer shareUnit;

	/** 日経225採用銘柄フラグ（1：採用銘柄、0：未採用銘柄） */
	@Column(name = "nikkei225_flg")
	public Boolean nikkei225Flg;

}
