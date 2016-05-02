package yokohama.yellow_man.module.definitions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 参照するアプリケーション全体で利用する定数定義クラス。
 * @author yellow-man
 * @since 1.0
 */
public class AppConsts {

	/** 決算種別：1..第１(int) */
	public static final int INT_SETTLEMENT_TYPES_ID_1 = 1;
	/** 決算種別：2..第２(int) */
	public static final int INT_SETTLEMENT_TYPES_ID_2 = 2;
	/** 決算種別：3..第３(int) */
	public static final int INT_SETTLEMENT_TYPES_ID_3 = 3;
	/** 決算種別：4..本(int) */
	public static final int INT_SETTLEMENT_TYPES_ID_4 = 4;

	/** 決算種別：1..第１ */
	public static final Integer SETTLEMENT_TYPES_ID_1 = INT_SETTLEMENT_TYPES_ID_1;
	/** 決算種別：2..第２ */
	public static final Integer SETTLEMENT_TYPES_ID_2 = INT_SETTLEMENT_TYPES_ID_2;
	/** 決算種別：3..第３ */
	public static final Integer SETTLEMENT_TYPES_ID_3 = INT_SETTLEMENT_TYPES_ID_3;
	/** 決算種別：4..本 */
	public static final Integer SETTLEMENT_TYPES_ID_4 = INT_SETTLEMENT_TYPES_ID_4;

	/** 決算種別：マッピング */
	public static final Map<String, Integer> SETTLEMENT_TYPES_ID_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>() {
		{
			put("第１", SETTLEMENT_TYPES_ID_1);
			put("第２", SETTLEMENT_TYPES_ID_2);
			put("第３", SETTLEMENT_TYPES_ID_3);
			put("本"  , SETTLEMENT_TYPES_ID_4);
		}
	});

}
