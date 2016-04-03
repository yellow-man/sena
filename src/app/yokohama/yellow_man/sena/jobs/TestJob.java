package yokohama.yellow_man.sena.jobs;

import java.util.List;

public class TestJob extends Job {

	public TestJob() {
	}

	@Override
	protected void run(List<String> args) {
		System.out.println("test");
	}
}
