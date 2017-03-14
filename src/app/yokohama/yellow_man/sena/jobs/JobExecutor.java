package yokohama.yellow_man.sena.jobs;

import java.io.File;
import java.util.Date;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.IntOptionHandler;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import play.Play;
import yokohama.yellow_man.common_tools.DateUtils;

/**
 * バッチ処理の起動クラス。
 * <p>コマンドラインより実行対象のクラスを指定し起動する。
 * <p>{@link TestJob}クラスを起動する例（windows）：
 * <ul>
 * <li>{@code src}フォルダに移動する。</li>
 * <li>下記コマンドの実行
 * <pre>
 * &gt; activator stage
 * &gt; java -cp ./target/universal/stage/lib/* ^
 * -Dconfig.file=./target/universal/stage/conf/application-local.conf ^
 * -Dlogger.file=./target/universal/stage/conf/logger-local.xml ^
 * -Dfile.encoding=utf-8 ^
 * yokohama.yellow_man.sena.jobs.JobExecutor yokohama.yellow_man.sena.jobs.TestJob
 * </pre>
 * </li>
 * </ul>
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.1-1.2
 */
public class JobExecutor {

	/**
	 * 起動引数を保持するクラス。
	 * @author yellow-man
	 * @since 1.1.0-1.2
	 * @version 1.1.1-1.2
	 */
	public static class JobArgument {
		/** インターバル定数：インターバルとして指定する最小値。デフォルト：2秒 */
		public static final Integer MIN_INTERVAL_SEC = 2;

		/** インターバル定数：インターバルとして指定する最大値。デフォルト：5秒 */
		public static final Integer MAX_INTERVAL_SEC = 5;


		/** 起動引数：【必須】実行するバッチクラス名 */
		@Argument(index=0, metaVar="jobClass", required=true)
		public String jobClass;

		/** 起動引数：【任意】実行するバッチクラスに渡すパラメータ */
		@Argument(index=1, metaVar="arguments...", handler=StringArrayOptionHandler.class)
		public String[] arguments;

		/** 起動引数：【任意】取得対象開始日（フォーマット：YYYY-MM-DD） */
		@Option(name="-sd", handler=DateOptionHandler.class, metaVar="startDate", usage="start date")
		public Date startDate;

		/** 起動引数：【任意】取得対象終了日（フォーマット：YYYY-MM-DD） */
		@Option(name="-ed", handler=DateOptionHandler.class, metaVar="endDate", usage="end date")
		public Date endDate;

		/** 起動引数：【任意】取得対象最小銘柄コード（指定された銘柄コード以降の銘柄情報を取得する。） */
		@Option(name="-min", handler=IntOptionHandler.class, metaVar="minStockCode", usage="min stock code")
		public Integer minStockCode;

		/** 起動引数：【任意】取得対象最大銘柄コード（指定された銘柄コードまでの銘柄情報を取得する。） */
		@Option(name="-max", handler=IntOptionHandler.class, metaVar="maxStockCode", usage="max stock code")
		public Integer maxStockCode;

		/** 起動引数：【任意】インターバル最小値（インターバルとして指定する最小値。デフォルト：2秒） */
		@Option(name="-minIntervalSec", handler=IntOptionHandler.class, metaVar="minIntervalSec", usage="min interval")
		public Integer minIntervalSec;

		/** 起動引数：【任意】インターバル最大値（インターバルとして指定する最大値。デフォルト：5秒） */
		@Option(name="-maxIntervalSec", handler=IntOptionHandler.class, metaVar="maxIntervalSec", usage="max interval")
		public Integer maxIntervalSec;

		/**
		 * args4j ハンドラ拡張 日付チェックハンドラクラス。
		 * @author yellow-man
		 * @since 1.1.0-1.2
		 */
		public static class DateOptionHandler extends OneArgumentOptionHandler<Date> {
			/**
			 * コンストラクタ。
			 * @param parser The owner to which this handler belongs to.
			 * @param option The annotation.
			 * @param setter Object to be used for setting value.
			 */
			public DateOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Date> setter) {
				super(parser, option, setter);
			}

			/* (非 Javadoc)
			 * @see org.kohsuke.args4j.spi.OneArgumentOptionHandler#parse(java.lang.String)
			 */
			@Override
			protected Date parse(String argument) throws NumberFormatException, CmdLineException {

				Date date = DateUtils.toDate(argument, DateUtils.DATE_FORMAT_YYYY_MM_DD_2);
				if (date == null) {
					throw new IllegalArgumentException(argument);
				}
				return date;
			}
		}
	}

	/**
	 * コマンドライン起動{@code main}関数。
	 * @param args 起動引数
	 * @since 1.0.0-1.0
	 */
	public static void main(String[] args) {

		// 起動引数保持用オブジェクト初期化
		JobArgument jobArgument = new JobArgument();
		CmdLineParser parser = new CmdLineParser(jobArgument);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			return;
		}
		// インターバルデフォルト値
		if (jobArgument.minIntervalSec == null) {
			jobArgument.minIntervalSec = JobArgument.MIN_INTERVAL_SEC;
		}
		if (jobArgument.maxIntervalSec == null) {
			jobArgument.maxIntervalSec = JobArgument.MAX_INTERVAL_SEC;
		}
		// 引数が逆転してたらエラー
		if (jobArgument.maxIntervalSec < jobArgument.minIntervalSec) {
			throw new IllegalArgumentException(
					"maxIntervalSec < minIntervalSec：maxIntervalSec=" + jobArgument.maxIntervalSec
					+ ", minIntervalSec=" + jobArgument.minIntervalSec);
		}
		JobExecutor jobExecutor = new JobExecutor();
		jobExecutor.execute(jobArgument);
	}

	/**
	 * バッチ処理起動メソッド。
	 * <p>Playアプリケーションの起動、バッチ実行クラスの呼び出しを行う。
	 * @param jobArgument 起動引数
	 * @since 1.1.0-1.2
	 */
	private void execute(JobArgument jobArgument) {

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
			Class<? extends AppJob> clazz = (Class<? extends AppJob>) Play.application().classloader().loadClass(jobArgument.jobClass);

			// Jobインスタンス生成
			AppJob job = clazz.newInstance();

			// job呼び出し
			job.call(jobArgument);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			play.api.Play.stop();
		}
	}
}
