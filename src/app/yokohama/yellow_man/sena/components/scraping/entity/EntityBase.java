package yokohama.yellow_man.sena.components.scraping.entity;

import yokohama.yellow_man.common_tools.FieldUtils;

/**
 * エンティティ基底クラス。
 * スクレイピングエンティティは、このクラスを継承する。
 * @author yellow-man
 * @since 1.0
 */
public class EntityBase {

	/**
	 * @see java.lang.Object#toString()
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return FieldUtils.toMapField(this).toString();
	}
}
