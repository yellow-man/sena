package yokohama.yellow_man.sena.components.scraping.entity;

import java.util.Date;

/**
 * 株価エンティティ。
 * <p>スクレイピング結果の株価情報を保持する。
 *
 * @author yellow-man
 * @since 1.1.0-1.2
 */
public class StockPricesEntity extends EntityBase {

	/** 日付（文字列） */
	public String dateStr;

	/** 日付（日付） */
	public Date date;

	/** 銘柄コード */
	public Integer stockCode;

	/** 始値（整数部：15桁、小数部：5桁） */
	public String openingPrice;

	/** 高値（整数部：15桁、小数部：5桁） */
	public String highPrice;

	/** 安値（整数部：15桁、小数部：5桁） */
	public String lowPrice;

	/** 終値（整数部：15桁、小数部：5桁） */
	public String closingPrice;

	/** 出来高（整数部：15桁、小数部：5桁） */
	public String turnover;

	/** 調整後終値	（分割実施前の終値を分割後の値に調整したもの、整数部：15桁、小数部：5桁） */
	public String adjustedClosingPrice;

	/** 分割フラグ（1：分割あり、0：分割無し） */
	public Boolean splitFlg = false;

	/** 分割テキスト */
	public String splitText;
}
