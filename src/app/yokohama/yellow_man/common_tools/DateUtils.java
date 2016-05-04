package yokohama.yellow_man.common_tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日付操作に関する機能を提供します。
 *
 * @author yellow-man
 * @since 1.0
 */
public class DateUtils {

	/** 日付フォーマット：yyyy/MM/dd HH:mm */
	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS  = "yyyy/MM/dd HH:mm:ss";
	/** 日付フォーマット：yyyy/MM/dd HH:mm */
	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM     = "yyyy/MM/dd HH:mm";
	/** 日付フォーマット：yyyy/M/d */
	public static final String DATE_FORMAT_YYYY_M_D           = "yyyy/M/d";
	/** 日付フォーマット：yyyy/MM/dd */
	public static final String DATE_FORMAT_YYYY_MM_DD           = "yyyy/MM/dd";
	/** 日付フォーマット：yyyy-MM-dd */
	public static final String DATE_FORMAT_YYYY_MM_DD_2         = "yyyy-MM-dd";
	/** 日付フォーマット：yyyy/MM */
	public static final String DATE_FORMAT_YYYY_MM              = "yyyy/MM";
	/** 日付フォーマット：yyyyMMddHHmmss */
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS       = "yyyyMMddHHmmss";
	/** 日付フォーマット：yyyyMMddHHmm */
	public static final String DATE_FORMAT_YYYYMMDDHHMM         = "yyyyMMddHHmm";
	/** 日付フォーマット：yyyyMMdd */
	public static final String DATE_FORMAT_YYYYMMDD             = "yyyyMMdd";
	/** 日付フォーマット：dd */
	public static final String DATE_FORMAT_DD                   = "dd";
	/** 日付フォーマット：HHmm */
	public static final String DATE_FORMAT_HHMM                 = "HHmm";

	/**
	 * 引数{@code date}に対して日数{@code day}を加算します。
	 * 日数{@code day}の値が負数の場合、減算します。
	 *
	 * @param date 加算（減算）対象の日付
	 * @param day 加算（減算）する日数
	 * @return 加算（減算）結果の日付
	 * @since 1.0
	 */
	public static Date addDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 日数を加算
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}


	/**
	 * 引数{@code dateStr}を引数指定フォーマット{@code formatStr}で{@link Date}に変換します。
	 * 変換に失敗した場合は、{@code null}を返します。
	 *
	 * @param dateStr 日付文字列
	 * @param formatStr 日付フォーマット
	 * @return 変換後の{@link Date}オブジェクト（変換に失敗した場合は、{@code null}を返します。）
	 * @since 1.0
	 */
	public static Date toDate(String dateStr, String formatStr) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
			date = sdf.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return date;
	}
}
