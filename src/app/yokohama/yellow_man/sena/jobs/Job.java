package yokohama.yellow_man.sena.jobs;

import java.util.List;

public abstract class Job {

	public final void call(List<String> args) {
		try {
			if(init()) {
				before();
				run(args);
				after();
			}
		} catch (Exception e) {
		} finally {
			_finally();
		}
	}

	protected boolean init() {
		return true;
	}

	protected void before() {
	}

	protected abstract void run(List<String> args) throws Exception;

	protected void after() {
	}

	protected void _finally() {
	}
}
