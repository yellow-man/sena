package yokohama.yellow_man.sena.components.scraping;

/**
 * ウェブスクレイピングに関する例外クラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ScrapingException extends Exception {

	/**
	 * ウェブスクレイピング例外クラスコンストラクタ。
	 * @since 1.0
	 */
	public ScrapingException() {
		super();
	}

	/**
	 * ウェブスクレイピング例外クラスコンストラクタ。
	 * @param message エラーメッセージ
	 * @param cause 例外
	 * @since 1.0
	 */
	public ScrapingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * ウェブスクレイピング例外クラスコンストラクタ。
	 * @param message エラーメッセージ
	 * @since 1.0
	 */
	public ScrapingException(String message) {
		super(message);
	}

	/**
	 * ウェブスクレイピング例外クラスコンストラクタ。
	 * @param cause 例外
	 * @since 1.0
	 */
	public ScrapingException(Throwable cause) {
		super(cause);
	}

}
