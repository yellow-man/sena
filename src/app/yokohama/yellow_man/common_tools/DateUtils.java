package yokohama.yellow_man.common_tools;

import java.util.Calendar;
import java.util.Date;

/**
 * 日付操作に関する機能を提供します。
 *
 * @author yellow-man
 * @since 1.0
 */
public class DateUtils {

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
}
