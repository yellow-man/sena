package yokohama.yellow_man.sena.jobs;

import java.util.List;

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
		System.out.println("test");
	}
}
