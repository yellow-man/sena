package yokohama.yellow_man.sena.components;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * HTTP操作を行うクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class HttpComponents {

	/**
	 * パラメータ{@code url}に対するHTTP GETリクエストを行い、body部を取得する。
	 * @param url URL
	 * @return 本文（リクエストエラーが発生した場合、{@code null}を返却）
	 * @since 1.0
	 */
	public static String executeGet(String url) {
		AppLogger.info("===== HTTP GET Start =====：url=" + url);
		String ret = null;

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet getMethod = new HttpGet(url);

			try (CloseableHttpResponse response = httpClient.execute(getMethod)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ret = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				}
			}
		} catch (IOException e) {
			AppLogger.error("HTTP GET リクエスト時にエラーが発生しました。：url=" + url, e);
		}
		AppLogger.info("===== HTTP GET  End  =====：url=" + url);
		return ret;
	}

	/**
	 * [TODO yellow-man]未実装 パラメータ{@code url}に対するHTTP POSTリクエストを行い、body部を取得する。
	 * @param url URL
	 * @return 本文（リクエストエラーが発生した場合、{@code null}を返却）
	 * @since 1.0
	 */
	public static String executePost(String url) {
		AppLogger.info("===== HTTP POST Start =====");
		String ret = null;

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost postMethod = new HttpPost(url);

			StringBuilder builder = new StringBuilder();
			builder.append("POST Body");
			builder.append("\r\n");
			builder.append("Hello Http Server!!");
			builder.append("\r\n");

			postMethod.setEntity(new StringEntity(builder.toString(),
												  StandardCharsets.UTF_8));

			try (CloseableHttpResponse response = httpClient.execute(postMethod)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					ret = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				}
			}
		} catch (IOException e) {
			AppLogger.error("HTTP POST リクエスト時にエラーが発生しました。：url=" + url, e);
		}
		AppLogger.info("===== HTTP POST End =====");
		return ret;
	}
}
