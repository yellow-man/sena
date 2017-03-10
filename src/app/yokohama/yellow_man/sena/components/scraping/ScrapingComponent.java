package yokohama.yellow_man.sena.components.scraping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import yokohama.yellow_man.common_tools.DateUtils;
import yokohama.yellow_man.sena.components.scraping.entity.CompanySchedulesEntity;
import yokohama.yellow_man.sena.components.scraping.entity.DebitBalancesEntity;
import yokohama.yellow_man.sena.components.scraping.entity.FinancesEntity;
import yokohama.yellow_man.sena.components.scraping.entity.IndicatorsEntity;
import yokohama.yellow_man.sena.components.scraping.entity.StockPricesEntity;
import yokohama.yellow_man.sena.core.definitions.AppConsts;

/**
 * ウェブスクレイピングを行うクラス。
 *
 * @author yellow-man
 * @since 1.0.0-1.0
 * @version 1.1.0-1.2
 */
public class ScrapingComponent {

	/** 企業スケジュールを取得：アクセス先のURL */
	private static final String GET_SCHEDULE_ACCESS_URL         = "http://www.nikkei.com/markets/kigyo/money-schedule/kessan/";
	/** 企業スケジュールを取得：ベースとなるスタイルシートのpath（元のXPath：『 //*[@id="newpresSchedule"]/div[3]/div/div[2]/table/tbody/tr 』） */
	private static final String GET_SCHEDULE_CSS_BASE_PATH      = "#newpresSchedule > div > div > div > table > tbody > tr";

	/** 信用残取得：アクセス先のURL */
	private static final String GET_DEBIT_BALANCES_ACCESS_URL   = "http://www.nikkei.com/markets/company/history/trust.aspx?scode=";

	/** 企業指標取得：アクセス先のURL(yahoo) */
	private static final String GET_INDICATORS_ACCESS_URL_YAHOO = "http://stocks.finance.yahoo.co.jp/stocks/detail/?code=";
	/** 企業指標取得：アクセス先のURL(日経) */
	private static final String GET_INDICATORS_ACCESS_URL_NIKKEI= "http://www.nikkei.com/markets/company/kessan/shihyo.aspx?scode=";

	/** 企業財務取得：アクセス先のURL */
	private static final String GET_FINANCES_ACCESS_URL = "http://ke.kabupro.jp/xbrl/";

	/**
	 * 企業スケジュールを取得する。
	 * @param stockCode 銘柄コード
	 * @return 企業スケジュールエンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0.0-1.0
	 * @see CompanySchedulesEntity
	 * @see ScrapingException
	 */
	public static CompanySchedulesEntity getSchedule(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI postUrl = null;
		CompanySchedulesEntity retEntity = null;

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
				throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTr = document.select(GET_SCHEDULE_CSS_BASE_PATH);
			if(tableTr != null && tableTr.size() > 0){
				retEntity = new CompanySchedulesEntity();
				retEntity.stockCode         = Integer.parseInt(tableTr.select("td").eq(0).text().replaceAll(" |　| ", ""));
				retEntity.stockName         = tableTr.select("td").eq(1).text().replaceAll(" |　| ", "");
				retEntity.settlement        = tableTr.select("td").eq(3).text().replaceAll(" |　| ", "");
				retEntity.settlementDateStr = tableTr.select("th").eq(0).text().replaceAll(" |　| ", "");
				retEntity.settlementType    = tableTr.select("td").eq(4).text().replaceAll(" |　| ", "");
				retEntity.market            = tableTr.select("td").eq(6).text().replaceAll(" |　| ", "");
			}else{
				throw new ScrapingException("データ解析失敗しました。：" + messagePrefix);
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP POST リクエスト時にエラーが発生しました。：" + messagePrefix + ", postUrl=" + postUrl, e);
		}

		return retEntity;
	}

	/**
	 * 信用残を取得する
	 * @param stockCode 銘柄コード
	 * @return 信用残エンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0.0-1.0
	 * @see DebitBalancesEntity
	 * @see ScrapingException
	 */
	public static List<DebitBalancesEntity> getDebitBalancesList(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI getUrl = null;
		List<DebitBalancesEntity> retEntityList = null;

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
				throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTrList = document.getElementsByClass("cmn-table_style1").select("tr");

			if (tableTrList != null && tableTrList.size() > 0) {
				retEntityList = new ArrayList<DebitBalancesEntity>();
				int tableTrSize = tableTrList.size();
				for (int i = 1; i < tableTrSize; i++) {
					Element trElement = tableTrList.get(i);
					DebitBalancesEntity debitBalance = new DebitBalancesEntity();
					debitBalance.stockCode 				= stockCode;
					debitBalance.releaseDateStr 		= trElement.select("th").eq(0).text().replaceAll(" |　| ", "");
					debitBalance.releaseDate 			= DateUtils.toDate(debitBalance.releaseDateStr, DateUtils.DATE_FORMAT_YYYY_M_D);
					debitBalance.marginSellingBalance 	= trElement.select("td").eq(0).text().replaceAll(" |　| |,", "");
					debitBalance.marginDebtBalance 		= trElement.select("td").eq(1).text().replaceAll(" |　| |,", "");
					debitBalance.ratioMarginBalance 	= trElement.select("td").eq(2).text().replaceAll(" |　| ", "");
					retEntityList.add(debitBalance);
				}
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP GET リクエスト時にエラーが発生しました。：getUrl=" + getUrl, e);
		}

		return retEntityList;
	}

	/**
	 * 企業指標を取得する。
	 * @param stockCode 銘柄コード
	 * @return 企業指標エンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0.0-1.0
	 * @see IndicatorsEntity
	 * @see ScrapingException
	 */
	public static IndicatorsEntity getIndicators(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI getUrl = null;
		IndicatorsEntity retEntity = null;

		try {
			getUrl = new URI(GET_INDICATORS_ACCESS_URL_YAHOO + stockCode);
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
				throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTrList = document.getElementsByClass("chartFinance").select("dl");

			if (tableTrList != null && tableTrList.size() > 0) {
				retEntity = new IndicatorsEntity();

				String regex = "((0|[-+]?[1-9][0-9]*)(\\.\\d+)?)";
				Pattern pattern = Pattern.compile(regex);

				int tableTrSize = tableTrList.size();
				for (int i = 1; i < tableTrSize; i++) {
					Element trElement = tableTrList.get(i);

					String text = null;
					if (trElement.toString().indexOf("配当利回り") > -1) {
						text = trElement.select("strong").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.dividendYield = matchStr;
						}

					} else if (trElement.toString().indexOf("PER") > -1) {
						text = trElement.select("strong").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.priceEarningsRatio = matchStr;
						}

					} else if (trElement.toString().indexOf("PBR") > -1) {
						text = trElement.select("strong").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.priceBookValueRatio = matchStr;
						}

					} else if (trElement.toString().indexOf("EPS") > -1) {
						text = trElement.select("strong").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.earningsPerShare = matchStr;
						}

					} else if (trElement.toString().indexOf("BPS") > -1) {
						text = trElement.select("strong").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.bookValuePerShare = matchStr;
						}

					}
				}

				retEntity.stockCode = stockCode;

				// PBR / PER = ROE
				if (retEntity.priceBookValueRatio != null
						&& retEntity.priceEarningsRatio != null
						&& Double.parseDouble(retEntity.priceEarningsRatio) != 0) {

					BigDecimal priceBookValueRatio = new BigDecimal(retEntity.priceBookValueRatio);
					BigDecimal priceEarningsRatio = new BigDecimal(retEntity.priceEarningsRatio);

					BigDecimal divide = priceBookValueRatio.divide(priceEarningsRatio, 25, BigDecimal.ROUND_HALF_UP);
					BigDecimal answer = divide.multiply(new BigDecimal("100"));

					retEntity.returnOnEquity = answer.toString();
				}
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP GET リクエスト時にエラーが発生しました。：getUrl=" + getUrl, e);
		}

		if (retEntity == null) {
			throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
		}

		try {
			getUrl = new URI(GET_INDICATORS_ACCESS_URL_NIKKEI + stockCode);
		} catch (URISyntaxException e) {
			throw new ScrapingException("URL作成に失敗しました。：" + messagePrefix, e);
		}

		httpGet = new HttpGet(getUrl);

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
				throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTrList = document.getElementsByClass("cmn-table_style1").select("tr");
			if (tableTrList != null && tableTrList.size() > 0) {

				String regex = "((0|[-+]?[1-9][0-9]*)(\\.\\d+)?)";
				Pattern pattern = Pattern.compile(regex);

				int tableTrSize = tableTrList.size();
				for (int i = 1; i < tableTrSize; i++) {
					Element trElement = tableTrList.get(i);

					String text = null;
					if (trElement.toString().indexOf("自己資本比率") > -1) {
						text = trElement.select("td").text().trim().replaceAll(",", "");
						Matcher matcher = pattern.matcher(text);
						if (matcher.find()) {
							String matchStr = matcher.group(1);
							System.out.println(text+" = "+matchStr);

							retEntity.capitalRatio = matchStr;
						}
					}
				}
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP GET リクエスト時にエラーが発生しました。：getUrl=" + getUrl, e);
		}
		return retEntity;
	}

	/**
	 * 企業財務を取得する。
	 * @param stockCode 銘柄コード
	 * @return 企業財務エンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.0.0-1.0
	 * @see FinancesEntity
	 * @see ScrapingException
	 */
	public static List<FinancesEntity> getFinancesList(Integer stockCode) throws ScrapingException {
		String messagePrefix = "stockCode=" + stockCode;

		URI getUrl = null;
		List<FinancesEntity> retList = null;

		try {
			getUrl = new URI(GET_FINANCES_ACCESS_URL + stockCode + ".htm");
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
						html = EntityUtils.toString(response.getEntity(), "Shift-jis");
						break;

					case HttpStatus.SC_NOT_FOUND:
						throw new ScrapingException("データが存在しません。：" + messagePrefix + ", statusCode=" + statusCode);

					default:
						throw new ScrapingException("通信エラーが発生しました。：" + messagePrefix + ", statusCode=" + statusCode);
				}
			}

			if(html == null || html.length() < 0){
				throw new ScrapingException("データ取得に失敗しました。：" + messagePrefix);
			}

			Document document = Jsoup.parse(html);
			// tableを読み込む
			Elements tableTrList = document.getElementsByClass("Quote").select("tr");

			if (tableTrList != null && tableTrList.size() > 4) {
				int tableTrSize = tableTrList.size();
				retList = new ArrayList<FinancesEntity>();
				for (int i = 4; i < tableTrSize; i++) {
					Element trElement = tableTrList.get(i);
					Elements thElements = trElement.select("th");
					Elements tdElements = trElement.select("td");
					if (tdElements != null && thElements.size() == 1 && tdElements.size() == 7) {
						FinancesEntity finance = new FinancesEntity();

						String[] str = thElements.get(0).text().split(" ");
						Integer sales = null;
						Integer operatingProfit = null;
						Integer netProfit = null;
						try{
							sales = Integer.parseInt(tdElements.get(0).text().replaceAll(" |　|,", ""));
						}catch(Exception e){}
						try{
							operatingProfit = Integer.parseInt(tdElements.get(1).text().replaceAll(" |　|,", ""));
						}catch(Exception e){}
						try{
							netProfit = Integer.parseInt(tdElements.get(2).text().replaceAll(" |　|,", ""));
						}catch(Exception e){}

						try{
							finance.year = Integer.parseInt(str[0]);
							finance.settlementTypesId = AppConsts.getSettlementType(str[1]);
							finance.stockCode = stockCode;
							finance.sales = sales;
							finance.operatingProfit = operatingProfit;
							finance.netProfit = netProfit;
						}catch(Exception e){
							continue;
						}
						retList.add(finance);
					}
				}
			}

		} catch (IOException e) {
			throw new ScrapingException("HTTP GET リクエスト時にエラーが発生しました。：getUrl=" + getUrl, e);
		}

		return retList;
	}

	/**
	 * 株価を取得する。
	 * @param stockCode 銘柄コード
	 * @return 株価エンティティを返却する。
	 * @throws ScrapingException スクレイピング時に発生する例外。
	 * @since 1.1.0-1.2
	 * @see StockPricesEntity
	 * @see ScrapingException
	 */
	public static List<StockPricesEntity> getStockPricesList(Integer stockCode) throws ScrapingException {
		return null;
	}

}
