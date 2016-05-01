package yokohama.yellow_man.sena.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.mail.MultiPartEmail;

import play.Play;
import yokohama.yellow_man.common_tools.StringUtils;
import yokohama.yellow_man.sena.components.AppLogger;

/**
 * バッチ処理ごとの個別ログを管理者にメール配信する基底クラス。
 * <p>バッチ処理固有ログ出力基底クラスの拡張。
 *
 * @author yellow-man
 * @since 1.0
 * @see AppJob
 */
public abstract class AppLoggerMailJob extends AppLoggerJob {

	/** メール：メールサーバ */
	public static final String EMAIL_SMTP_HOST_NAME = Play.application().configuration().getString("email.smtp.host.name", "localhost");
	/** メール：SMTPポート */
	public static final int EMAIL_SMTP_HOST_PORT    = Play.application().configuration().getInt("email.smtp.host.port", 25);
	/** メール：送信先メールアドレス（TO） */
	public static final String EMAIL_SMTP_TO        = Play.application().configuration().getString("email.smtp.to", "");
	/** メール：送信元メールアドレス（FROM） */
	public static final String EMAIL_SMTP_FROM      = Play.application().configuration().getString("email.smtp.from", "");


	/** メール：バッチログのメール配信をするしない（true：する、false：しない） */
	protected boolean isJobMail  = true;

	/** メール：メールタイトル */
	protected String emailTitle  = "";

	/** メール：送信先メールアドレス（TO）（※バッチ個々に設定が必要な場合、継承先で上書き） */
	protected String emailSmtpTo     = EMAIL_SMTP_TO;
	/** メール：送信元メールアドレス（FROM）（※バッチ個々に設定が必要な場合、継承先で上書き） */
	protected String emailSmtpFrom   = EMAIL_SMTP_FROM;

	/**
	 * バッチ終了時にメール配信処理を行う。
	 * <p>スーパークラスのメソッドを先に呼び出し、ログを切り替えている。
	 * メール出力に関するログ等、以降のログはapplication.logとして出力される。
	 *
	 * @see yokohama.yellow_man.sena.jobs.AppJob#_finally()
	 * @since 1.0
	 */
	@Override
	protected void _finally() {
		// スーパークラスのメソッドを呼び出し、先にログを切り替える。
		// 以降のログはapplication.logとして出力される。
		super._finally();

		BufferedReader bufferedReader = null;
		try {
			// メール送信フラグがONの場合送信
			if (this.isJobMail) {
				// メール送信処理
				AppLogger.info("バッチ メール送信処理 start");

				AppLogger.info(new StringBuffer("----- email settings -----").toString());
				AppLogger.info(new StringBuffer("email.smtp.host.name = ").append(EMAIL_SMTP_HOST_NAME).toString());
				AppLogger.info(new StringBuffer("email.smtp.host.port = ").append(EMAIL_SMTP_HOST_PORT).toString());
				AppLogger.info(new StringBuffer("email.smtp.to        = ").append(this.emailSmtpTo).toString());
				AppLogger.info(new StringBuffer("email.smtp.from      = ").append(this.emailSmtpFrom).toString());
				AppLogger.info(new StringBuffer("title                = ").append(this.emailTitle).toString());
				AppLogger.info(new StringBuffer("body(logFilePath)    = ").append(this.logFilePath).toString());

				// ログファイルから本文を取得
				File file = new File(this.logFilePath);
				bufferedReader = new BufferedReader(new FileReader(file));
				StringBuffer buff = new StringBuffer();
				String str = bufferedReader.readLine();
				while(str != null){
					str = bufferedReader.readLine();
					buff.append(str);
				}
				bufferedReader.close();

				MultiPartEmail email = new MultiPartEmail();

				//メールサーバを設定する
				email.setHostName(EMAIL_SMTP_HOST_NAME);
				email.setSmtpPort(EMAIL_SMTP_HOST_PORT);

				//送信先メールアドレスを設定する
				if (StringUtils.isEmptyWithTrim(this.emailSmtpTo)) {
					AppLogger.error("メール送信失敗：TOメールアドレスが設定されていません。");
					return;
				} else {
					String[] mailaddresses = this.emailSmtpTo.split(",");
					if (mailaddresses != null && mailaddresses.length > 0) {
						for (String mailaddress : mailaddresses) {
							email.addTo(mailaddress);
						}
					}
				}

				if (StringUtils.isEmptyWithTrim(this.emailSmtpFrom)) {
					AppLogger.error("メール送信失敗：FROMメールアドレスが設定されていません。");
					return;
				} else {
					//送信元メールアドレスを設定する
					email.setFrom(this.emailSmtpFrom);
				}

				//メールのタイトルを設定する
				email.setSubject(this.emailTitle);
				//メールの本文を設定する
				email.setMsg(buff.toString());

				//メールを送信する
				email.send();

			}
		} catch (Exception e) {
			AppLogger.error("メール送信処理に失敗しました。", e);

		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					AppLogger.error("BufferedReaderクローズ処理に失敗しました。", e);
				}
			}

			AppLogger.info("バッチ メール送信処理  end ");
		}
	}
}
