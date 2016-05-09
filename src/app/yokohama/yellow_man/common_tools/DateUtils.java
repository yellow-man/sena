package yokohama.yellow_man.common_tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
	public static final String DATE_FORMAT_YYYY_M_D             = "yyyy/M/d";
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


	/**
	 * 引数{@code date}に対して年を取得します。
	 *
	 * @param date 取得対象の日付
	 * @return 年を取得
	 * @since 1.0
	 */
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 引数{@code date}に対して月を取得します。
	 *
	 * @param date 取得対象の日付
	 * @return 月を取得
	 * @since 1.0
	 */
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 引数{@code date}に対して日を取得します。
	 *
	 * @param date 取得対象の日付
	 * @return 日を取得
	 * @since 1.0
	 */
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DATE);
	}


	/**
	 * 引数年{@code year}、月{@code month}、日{@code day}を指定し、日付を取得します。
	 * @param year 年 1900～
	 * @param month 月 1～12
	 * @param day 日 1～31
	 * @return 日付を取得
	 * @since 1.0
	 */
	public static Date getDate(int year, int month, int day) {
		// Dateを生成
		Calendar cal = new GregorianCalendar(year, month-1, day, 0, 0, 0);
		return cal.getTime();
	}

	/**
	 * 引数年{@code year}、月{@code month}、日{@code day}、時{@code hour}、分{@code min}を指定し、日付を取得します。
	 * @param year 年 1900～
	 * @param month 月 1～12
	 * @param day 日 1～31
	 * @param hour 時 0〜23
	 * @param min 分 0〜59
	 * @return 日付を取得
	 * @since 1.0
	 */
	public static Date getDate(int year, int month, int day, int hour, int min) {
		// Dateを生成
		Calendar cal = new GregorianCalendar(year, month-1, day, hour, min);
		return cal.getTime();
	}

	/**
	 * 引数年{@code year}、月{@code month}、日{@code day}、時{@code hour}、分{@code min}、秒{@code sec}を指定し、日付を取得します。
	 * @param year 年 1900～
	 * @param month 月 1～12
	 * @param day 日 1～31
	 * @param hour 時 0〜23
	 * @param min 分 0〜59
	 * @param sec 秒 0〜59
	 * @return 日付を取得
	 * @since 1.0
	 */
	public static Date getDate(int year, int month, int day, int hour, int min, int sec) {
		// Dateを生成
		Calendar cal = new GregorianCalendar(year, month-1, day, hour, min, sec);
		return cal.getTime();
	}


	/**
	 * 引数{@code date}に対して、時{@code hour}、分{@code min}、秒{@code sec}を指定し、日付を取得します。
	 * @param date 日付
	 * @param hour 時 0〜23
	 * @param min 分 0〜59
	 * @param sec 秒 0〜59
	 * @return 日付を取得
	 * @since 1.0
	 */
	public static Date getDate(Date date, int hour, int min, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Dateを生成
		cal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour, min, sec);
		return cal.getTime();
	}

	/**
	 * 引数{@code date}に対して、時{@code hour}、分{@code min}、秒{@code sec}が00:00:00の日付を取得します。
	 * @param date 日付
	 * @return 日付を取得
	 * @since 1.0
	 */
	public static Date getJustDate(Date date) {
		return getDate(date, 0, 0, 0);
	}
}
