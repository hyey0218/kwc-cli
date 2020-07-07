package konantech.kwc.cli.search;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.RequestLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import konantech.kwc.cli.common.Constants;

public class SearchRestClient {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	public RestClient getRestClient() {
		RestClientBuilder restClientBuilder = RestClient.builder(
				new HttpHost("127.0.0.1", 9200,"http")
				);
		RestClient restClient = null;
//		Header[] header = new Header[] {new BasicHeader("", "")};
//		restClientBuilder.setDefaultHeaders(header);
		restClientBuilder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);
		
		
//		final CredentialsProvider credentialsProvider =
//			    new BasicCredentialsProvider();
//			credentialsProvider.setCredentials(AuthScope.ANY,
//			    new UsernamePasswordCredentials("elastic", "elastic"));
		
		// 구성요소(설정) 세팅
		restClientBuilder.setRequestConfigCallback(
				new RestClientBuilder.RequestConfigCallback() {
					
					@Override
					public Builder customizeRequestConfig(Builder requestConfigBuilder) {
						return requestConfigBuilder
								.setSocketTimeout(10000)
								.setConnectTimeout(10000);
					}
				})
//				.setHttpClientConfigCallback(new HttpClientConfigCallback() {
//					
//					@Override
//					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//						httpClientBuilder.disableAuthCaching();
//						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//					}
//				})
		;
		
		
		restClient = restClientBuilder.build();
		return restClient;
	}
	
	
	public void getSearchData(String query) {
		RestClient restClient = null;
		
		try {
			restClient = getRestClient();
			
			Request req = new Request("GET", "/kwc/_search");
			req.setOptions(Constants.COMMON_OPTIONS);
//			req.addParameter("", "");
			
			req.setEntity(new NStringEntity(query,ContentType.APPLICATION_JSON));
			
			
			//비동기
//			Cancellable cancellable = restClient.performRequestAsync(req, 
//					new ResponseListener() {
//						
//						@Override
//						public void onSuccess(Response response) {
//							logger.info(" Rest Success ");
////							Response response = restClient.performRequest(new Request("GET", "/"));
//							RequestLine requestLine = response.getRequestLine(); 
//							HttpHost host = response.getHost(); 
//							int statusCode = response.getStatusLine().getStatusCode(); 
//							Header[] headers = response.getHeaders(); 
//							try {
//								String responseBody = EntityUtils.toString(response.getEntity());
//								System.out.println(responseBody);
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} 
//						}
//						
//						@Override
//						public void onFailure(Exception exception) {
//							logger.info(" Rest Failure ");
//						}
//					});
			
			
			Response response = restClient.performRequest(req);
			
//			System.out.println(response.getStatusLine()); //HTTP/1.1 200 OK
			RequestLine line = response.getRequestLine();
			String body = EntityUtils.toString(response.getEntity());
			JSONObject result = (JSONObject) new JSONParser().parse(body);
			JSONObject hits = (JSONObject) result.get("hits");
			
			if(hits != null) {
				
			}
			System.out.println(hits.get("hits"));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				restClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getFindHashedQuery(String param) {
		String query = "{ \"query\" : { \"match\" : {\"hashed\":\""+param+"\"} } }";
		getSearchData(query);
		
		return query;
	}

}
