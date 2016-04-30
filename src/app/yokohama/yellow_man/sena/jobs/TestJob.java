package yokohama.yellow_man.sena.jobs;

import java.util.List;

import yokohama.yellow_man.sena.components.AppLogger;

/**
 * バッチ処理テストクラス。
 * <p>バッチ疎通確認等に使用する。
 *
 * @author yellow-man
 * @since 1.0
 */
public class TestJob extends AppJob {

	/**
	 * バッチ処理テストクラスコンストラクタ。
	 * @since 1.0
	 */
	public TestJob() {
	}

	/**
	 * @see yokohama.yellow_man.sena.jobs.AppJob#run(java.util.List)
	 * @since 1.0
	 */
	@Override
	protected void run(List<String> args) {

		// ログ出力テスト
		AppLogger.debug("debugテスト");
		AppLogger.info("infoテスト");
		AppLogger.warn("warnテスト");
		AppLogger.error("errorテスト");
		AppLogger.error("errorテスト", new Exception());
	}
}
