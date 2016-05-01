package yokohama.yellow_man.sena.components.scraping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import yokohama.yellow_man.sena.components.AppLogger;
import yokohama.yellow_man.sena.components.scraping.entity.CompanySchedulesEntity;
import yokohama.yellow_man.sena.components.scraping.entity.DebitBalancesEntity;

/**
 * ウェブスクレイピングを行うクラス。
 *
 * @author yellow-man
 * @since 1.0
 */
public class ScrapingComponent {

	/** 企業スケジュールを取得：アクセス先のURL */
	private static final String GET_SCHEDULE_ACCESS_URL       = "http://www.nikkei.com/markets/kigyo/money-schedule/kessan/";
	/** 企業スケジュールを取得：ベースとなるスタイルシートのpath（元のXPath：『 //*[@id="newpresSchedule"]/div[3]/div/div[2]/table/tbody/tr 』） */
	private static final String GET_SCHEDULE_CSS_BASE_PATH    = "#newpresSchedule > div > div > div > table > tbody > tr";

	/** 信用残取得：アクセス先のURL */
	private static final String GET_DEBIT_BALANCES_ACCESS_URL = "http://www.nikkei.com/markets/company/history/trust.aspx?scode=";

	/**
	 * 企業スケジュールを取得する。
	 * @param stockCode 銘柄コード
	 * @return 企業スケジュールエンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0
	 * @see CompanySchedulesEntity
	 * @see ScrapingException
	 */
	public static CompanySchedulesEntity getSchedule(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI postUrl = null;
		CompanySchedulesEntity schedule = null;

		try {
			postUrl = new URI(GET_SCHEDULE_ACCESS_URL);
		} catch (URISyntaxException e) {
			throw new ScrapingException("URL作成に失敗しました。：" + messagePrefix, e);
		}

		HttpPost postMethod = new HttpPost(postUrl);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		// 証券コード
		postParams.add(new  BasicNameValuePair("kwd", stockCode.toString()));
		// hidden値のセット
		postParams.add(new  BasicNameValuePair("ResultFlag", "3"));
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ScrapingException("POSTパラメータエンコーディング処理に失敗しました。：" + messagePrefix, e);
		}

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String html = null;
			try (CloseableHttpResponse response = httpClient.execute(postMethod)) {

				int statusCode = response.getStatusLine().getStatusCode();
				System.out.println("ステータスコード:" + statusCode);

				switch (response.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						System.out.println("取得に成功しました。：" + messagePrefix + ", statusCode=" + statusCode);
						html = EntityUtils.toString(response.getEntity(), "UTF-8");
						break;

					case HttpStatus.SC_NOT_FOUND:
						throw new ScrapingException("データが存在しません。：" + messagePrefix + ", statusCode=" + statusCode);

					default:
						throw new ScrapingException("通信エラーが発生しました。：" + messagePrefix + ", statusCode=" + statusCode);
				}
			}

			if(html == null || html.length() < 0){
				throw new ScrapingException("データ取得失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTr = document.select(GET_SCHEDULE_CSS_BASE_PATH);
			if(tableTr != null && tableTr.size() > 0){
				schedule = new CompanySchedulesEntity();
				schedule.stockCode         = Integer.parseInt(tableTr.select("td").eq(0).text().replaceAll(" |　| ", ""));
				schedule.stockName         = tableTr.select("td").eq(1).text().replaceAll(" |　| ", "");
				schedule.settlement        = tableTr.select("td").eq(3).text().replaceAll(" |　| ", "");
				schedule.settlementDateStr = tableTr.select("th").eq(0).text().replaceAll(" |　| ", "");
				schedule.settlementType    = tableTr.select("td").eq(4).text().replaceAll(" |　| ", "");
				schedule.market            = tableTr.select("td").eq(6).text().replaceAll(" |　| ", "");
			}else{
				throw new ScrapingException("データ解析失敗しました。：" + messagePrefix);
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP POST リクエスト時にエラーが発生しました。：" + messagePrefix + ", postUrl=" + postUrl, e);
		}

		return schedule;
	}

	/**
	 * 信用残を取得する
	 * @param stockCode 銘柄コード
	 * @return 信用残エンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0
	 * @see DebitBalancesEntity
	 * @see ScrapingException
	 */
	public static List<DebitBalancesEntity> getDebitBalances(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI getUrl = null;
		List<DebitBalancesEntity> retList = null;

		try {
			getUrl = new URI(GET_DEBIT_BALANCES_ACCESS_URL + stockCode);
		} catch (URISyntaxException e) {
			throw new ScrapingException("URL作成に失敗しました。：" + messagePrefix, e);
		}

		HttpGet httpGet = new HttpGet(getUrl);

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String html = null;
			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {

				int statusCode = response.getStatusLine().getStatusCode();
				System.out.println("ステータスコード:" + statusCode);

				switch (response.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						System.out.println("取得に成功しました。：" + messagePrefix + ", statusCode=" + statusCode);
						html = EntityUtils.toString(response.getEntity(), "UTF-8");
						break;

					case HttpStatus.SC_NOT_FOUND:
						throw new ScrapingException("データが存在しません。：" + messagePrefix + ", statusCode=" + statusCode);

					default:
						throw new ScrapingException("通信エラーが発生しました。：" + messagePrefix + ", statusCode=" + statusCode);
				}
			}

			if(html == null || html.length() < 0){
				throw new ScrapingException("データ取得失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTrList = document.getElementsByClass("cmn-table_style1").select("tr");

			if (tableTrList != null && tableTrList.size() > 0) {
				retList = new ArrayList<DebitBalancesEntity>();
				int tableTrSize = tableTrList.size();
				for (int i = 1; i < tableTrSize; i++) {
					Element trElement = tableTrList.get(i);
					DebitBalancesEntity debitBalance = new DebitBalancesEntity();
					debitBalance.stockCode 				= stockCode;
					debitBalance.releaseDate 			= trElement.select("th").eq(0).text().replaceAll(" |　| ", "");
					debitBalance.marginSellingBalance 	= trElement.select("td").eq(0).text().replaceAll(" |　| |,", "");
					debitBalance.marginDebtBalance 		= trElement.select("td").eq(1).text().replaceAll(" |　| |,", "");
					debitBalance.ratioMarginBalance 	= trElement.select("td").eq(2).text().replaceAll(" |　| ", "");
					retList.add(debitBalance);
				}
			}

		} catch (IOException e) {
			AppLogger.error("HTTP GET リクエスト時にエラーが発生しました。：getUrl=" + getUrl, e);
		}

		return retList;
	}

	public static void main(String[] args){
		List<DebitBalancesEntity> debitBalanceList;
		try {
			debitBalanceList = getDebitBalances(1789);

			for (DebitBalancesEntity debitBalance : debitBalanceList) {
				System.out.println(debitBalance.toString());
			}
		} catch (ScrapingException e) {
			e.printStackTrace();
		}
	}
}
