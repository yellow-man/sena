package yokohama.yellow_man.common_tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * フィールド操作に関する機能を提供します。
 * @author yellow-man
 * @since 1.0
 */
public class FieldUtils {

	/**
	 * オブジェクトのプロパティ変数名をキーとし、変数の値を値とした新たな{@code Map}を返します。
	 * <br>また、staticフィールドは除外設定の有無にかかわらず除外します。
	 * @param obj Map化するオブジェクト
	 * @param excludeList 除外設定（ここで指定された文字列のプロパティはMapから除外します。）
	 * @return キー{@code Object}の変数名, 値{@code Object}の変数の値
	 * @since 1.0
	 */
	public static Map<String, Object> toMapField(Object obj, List<String> excludeList) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		Field[] fs = obj.getClass().getFields();
		for (Field f : fs) {
			// フィールドの属性（修飾子）を取得
			int mod = f.getModifiers();

			// 静的要素は除外
			if (Modifier.isStatic(mod)) {
				continue;
			}

			try {
				String name = f.getName();

				// 除外リストに指定されたものはマップに含めない
				if (excludeList != null && excludeList.contains(name)) {
					continue;
				}

				retMap.put(name, (f.get(obj) == null) ? null : f.get(obj));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return retMap;
	}

	/**
	 * このメソッドは、{@link FieldUtils#toMapField(Object, List)}のラップメソッドです。
	 * 除外設定に{@code null}を指定した{@code FieldUtils#toMapField(obj, null)}と同じ結果が得られます。
	 * @param obj Map化するオブジェクト
	 * @return キー{@code Object}の変数名, 値{@code Object}の変数の値
	 * @since 1.0
	 * @see FieldUtils#toMapField(Object, List)
	 */
	public static Map<String, Object> toMapField(Object obj) {
		return toMapField(obj, null);
	}
}
