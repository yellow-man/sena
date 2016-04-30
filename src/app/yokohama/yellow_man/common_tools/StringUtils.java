package yokohama.yellow_man.common_tools;

/**
 * 文字列操作に関する機能を提供します。
 *
 * @author yellow-man
 * @since 1.0
 */
public class StringUtils {

	/**
	 * 引数{@code value}の値が{@code null}または、
	 * {@link String#trim()}した結果が{@link String#isEmpty()}の場合、
	 * {@code true}を返します。
	 *
	 * @param value チェック対象の文字列
	 * @return 引数{@code value}の値が{@code null}または、
	 * 			{@link String#trim()}した結果が{@link String#isEmpty()}の場合、{@code true}。
	 * 			それ以外{@code false}。
	 * @since 1.0
	 */
	public static boolean isEmptyWithTrim(String value) {
		if (value == null || value.trim().isEmpty()) {
			return true;
		}
		return false;
	}

}
