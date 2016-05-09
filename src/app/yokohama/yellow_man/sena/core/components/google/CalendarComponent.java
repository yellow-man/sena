package yokohama.yellow_man.sena.core.components.google;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import play.Play;
import yokohama.yellow_man.common_tools.DateUtils;

/**
 * google カレンダー操作を行うクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class CalendarComponent {

	/**
	 * google アプリケーション名
	 * {@code application.conf}ファイル{@code google_calendar.application.name}キーにて値の変更可。
	 */
	private static final String GOOGLE_CALENDAR_APPLICATION_NAME      = Play.application().configuration().getString("google_calendar.application.name", "");
	/**
	 * google キーファイルのpath
	 * {@code application.conf}ファイル{@code google_calendar.privatekey.file.path}キーにて値の変更可。
	 */
	private static final String GOOGLE_CALENDAR_PRIVATEKEY_FILE_PATH  = Play.application().configuration().getString("google_calendar.privatekey.file.path", "");
	/**
	 * google サービスアカウントID
	 * {@code application.conf}ファイル{@code google_calendar.service.account.id}キーにて値の変更可。
	 */
	private static final String GOOGLE_CALENDAR_SERVICE_ACCOUNT_ID    = Play.application().configuration().getString("google_calendar.service.account.id", "");
	/**
	 * google カレンダーID
	 * {@code application.conf}ファイル{@code google_calendar.calendar.id}キーにて値の変更可。
	 */
	private static final String GOOGLE_CALENDAR_CALENDAR_ID           = Play.application().configuration().getString("google_calendar.calendar.id", "");

	/** カレンダーサービスオブジェクト */
	private Calendar service = null;

	/**
	 * コンストラクタ
	 * @throws GoogleApiException Google API操作に関する例外クラス
	 * @since 1.0
	 */
	public CalendarComponent() throws GoogleApiException {
		// 設定値の確認
		StringBuffer errorMessage = new StringBuffer();
		if ("".equals(GOOGLE_CALENDAR_APPLICATION_NAME)) {
			errorMessage.append("アプリケーション名（google_calendar.application.name）が設定されていません。");
		}
		if ("".equals(GOOGLE_CALENDAR_PRIVATEKEY_FILE_PATH)) {
			errorMessage.append("キーファイルのpath（google_calendar.privatekey.file.path）が設定されていません。");
		}
		if ("".equals(GOOGLE_CALENDAR_SERVICE_ACCOUNT_ID)) {
			errorMessage.append("サービスアカウントID（google_calendar.service.account.id）が設定されていません。");
		}
		if ("".equals(GOOGLE_CALENDAR_CALENDAR_ID)) {
			errorMessage.append("カレンダーID（google_calendar.calendar.id）が設定されていません。");
		}
		if (errorMessage.length() > 0) {
			throw new GoogleApiException(errorMessage.toString());
		}

		GoogleCredential credential;
		try {
			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				// クライアント ID
				.setServiceAccountId(GOOGLE_CALENDAR_SERVICE_ACCOUNT_ID)
				.setServiceAccountScopes(Arrays.asList(CalendarScopes.CALENDAR))
				.setServiceAccountPrivateKeyFromP12File(new File(GOOGLE_CALENDAR_PRIVATEKEY_FILE_PATH))
				.build();

			this.service = new Calendar.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName(GOOGLE_CALENDAR_APPLICATION_NAME)
				.build();

			System.out.println(this.service.calendarList().list().execute().toString());

		} catch (Exception e) {
			throw new GoogleApiException(e);
		}
	}

	/**
	 * イベント開始日（終了日）{@code eventDate}を指定し、
	 * プレフィックス銘柄コード{@code stockCode}＋サマリ文字列{@code summaryStr}のイベントを追加する。
	 * <p>また、プレフィックス銘柄コード{@code stockCode}は説明{@code description}に登録する。
	 * イベント登録が完了した場合{@code true}、失敗した場合{@code false}を返却する。
	 *
	 * @param eventDate イベント開始日（終了日）
	 * @param stockCode 銘柄コード
	 * @param summaryStr サマリ文字列
	 * @return イベント登録が完了した場合{@code true}、失敗した場合{@code false}を返す。
	 * @throws GoogleApiException Google API操作に関する例外クラス
	 * @since 1.0
	 */
	public boolean insertEvent(Date eventDate, Integer stockCode, String summaryStr) throws GoogleApiException{
		boolean ret = false;
		try {
			// カレンダー更新
			Event event = new Event();
			event.setSummary(new StringBuffer(stockCode.toString()).append(" ").append(summaryStr).toString());
			event.setDescription(stockCode.toString());

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String startDateStr = dateFormat.format(eventDate);
			DateTime start = new DateTime(startDateStr);

			event.setStart(new EventDateTime().setDate(start));
			event.setEnd(new EventDateTime().setDate(start));

			Event createdEvent = this.service.events().insert(GOOGLE_CALENDAR_CALENDAR_ID, event).execute();

			System.out.println(createdEvent.getId());
			ret = true;
		} catch (Exception e) {
			throw new GoogleApiException(e);
		}
		return ret;
	}

	/**
	 * イベント開始日（終了日）{@code eventDate}を指定し、
	 * イベント一覧を取得する。
	 * <p>取得フォーマットは{@code Map}形式で取得し、説明{@code description}に登録された銘柄コードをキーに、
	 * サマリ文字列を値とし返却する。
	 *
	 * @param eventDate イベント開始日（終了日）
	 * @return 説明{@code description}に登録された銘柄コードをキーとし、サマリ文字列を値とし返却する。
	 * @throws GoogleApiException Google API操作に関する例外クラス
	 * @since 1.0
	 */
	public Map<Integer, String> listEvent(Date eventDate) throws GoogleApiException {
		Map<Integer, String> retMap = null;
		try {
			DateTime start = new DateTime(DateUtils.getJustDate(eventDate));
			DateTime end = new DateTime(DateUtils.getDate(eventDate, 23, 59, 59));

			Events events = this.service.events().list(GOOGLE_CALENDAR_CALENDAR_ID)
					.setTimeMin(start)
					.setTimeMax(end)
					.execute();

			if (events != null) {
				retMap = new LinkedHashMap<>();
				for (Event event : events.getItems()) {
					String summary = event.getSummary();
					String description = event.getDescription();
					System.out.println(new StringBuffer(summary).append(":").append(description).toString());

					Integer stockCode = NumberUtils.toInt(description);
					if (stockCode <= 0) {
						continue;
					}
					retMap.put(stockCode, summary);
				}
			}
		} catch (Exception e) {
			throw new GoogleApiException(e);
		}
		return retMap;
	}
}
