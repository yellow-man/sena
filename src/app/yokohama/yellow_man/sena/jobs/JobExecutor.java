package yokohama.yellow_man.sena.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import play.Play;

public class JobExecutor {

	@Argument(index=0, metaVar="jobClass", required=true)
	private String jobClass;

	@Argument(index=1, metaVar="arguments...", handler=StringArrayOptionHandler.class)
	private String[] arguments;

	public static void main(String[] args) {

		JobExecutor jobExecutor = new JobExecutor();

		CmdLineParser parser = new CmdLineParser(jobExecutor);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			return;
		}

		jobExecutor.execute();
	}

	private void execute() {

		try {
			// playアプリケーション取得
			String applicationPath = new File("").getAbsolutePath();
			File applicationDir = new File(applicationPath);
			ClassLoader cl = this.getClass().getClassLoader();
			play.api.Application app = new play.api.DefaultApplication(applicationDir, cl, null, play.api.Mode.Prod());

			// playアプリケーション起動
			play.api.Play.start(app);

			// jobクラス取得
			@SuppressWarnings("unchecked")
			Class<? extends Job> clazz = (Class<? extends Job>) Play.application().classloader().loadClass(this.jobClass);

			// Jobインスタンス生成
			Job job = clazz.newInstance();

			// job呼び出し
			List<String> argList = null;
			if (this.arguments != null) {
				argList = new ArrayList<String>();
				argList.addAll(Arrays.asList(this.arguments));
			}
			job.call(argList);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			play.api.Play.stop();
		}
	}
}
