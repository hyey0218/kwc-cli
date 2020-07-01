package konantech.kwc.cli.proc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ThreadGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import konantech.kwc.cli.common.Constants;
import konantech.kwc.cli.entity.Crawl;
import konantech.kwc.cli.service.CrawlService;

@Service
public class WebCrawler {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	JSONObject object;
	int start;
	int end;
	
	@Value("${kwc.thread.pool}")
	String kwcThread;
	
	@Value("${kwc.queue.size}")
	String queueSize;
	@Autowired
	CrawlService crawlService;
	
	public void setPage(int start, int end) {
		this.start = start;
		this.end = end;
	}
	public void setObject(JSONObject object) {
		this.object = object;
	}
	
	public void work() throws Exception {
		int thread = Integer.parseInt(kwcThread);
		int queue = Integer.parseInt(queueSize);
		
		BlockingQueue<String> links = new ArrayBlockingQueue<String>(queue);
		ConcurrentHashMap<String,Short> errorLink = new ConcurrentHashMap<String,Short>();
		
		
		ExecutorService main = Executors.newSingleThreadExecutor();
		main.execute(new LinkCrawlerThread(links));

		ExecutorService service = Executors.newFixedThreadPool(thread);
		
		DefaultProc[] selen = new DefaultProc[thread];
		Class[] constructorTypes = {WebCrawler.class, ExecutorService.class, BlockingQueue.class, ConcurrentHashMap.class};
		Object[] constructorParams = {this,service, links, errorLink};
		String collectorClass = StringUtils.defaultString((String) object.get("collector"), "");
		collectorClass = collectorClass.equals("")?Constants.DEFAULT_PACKAGE+"DefaultProc":Constants.PROC_PACKAGE+collectorClass;
		logger.info("CollectorClass -> " + collectorClass);
		for(int i=0;i<selen.length;i++) {
			
//			DefaultProc sm = new DefaultProc(links); 
			DefaultProc sm = (DefaultProc) Class.forName(collectorClass).getConstructor(constructorTypes).newInstance(constructorParams);
			sm.setChannel((String)object.get("channel"));
			sm.setSite((String)object.get("site"));
			sm.setBoard((String)object.get("board"));
			sm.setTitle((String)object.get("title"));
			sm.setContId((String)object.get("contId"));
			sm.setContent((String) object.get("content"));
			sm.setWriter((String) object.get("writer"));
			sm.setWriteDate((String) object.get("writeDate"));
			sm.setWdatePattern((String) object.get("wdatePattern"));
			selen[i] = sm;
			service.execute(sm);
		}
		System.out.println("MAIN QUIT!!");
		main.shutdown();
		service.shutdown();
	}
	
	class LinkCrawlerThread implements Runnable{
		WebDriver webDriver;
		BlockingQueue<String> links;
		public LinkCrawlerThread(BlockingQueue<String> links) {
			this.links = links;
		}
		
		public WebDriver openBrowser() throws Exception{
			System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); 
			ChromeOptions options = new ChromeOptions();
			options.setPageLoadStrategy(PageLoadStrategy.EAGER); // only HTML document loading, (discards loding of css/image...)
//	        options.setHeadless(true); // 특정 엘리먼트를 못찾음...; don't use
	        options.setProxy(null);
	        
	        ChromeDriver webDriver = new ChromeDriver(options);
			webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); // implicit wait
//			WebDriver wd = (ChromeDriver)ThreadGuard.protect(webDriver);
			return ThreadGuard.protect(webDriver);
		}

		@Override
		public void run() {
			try {
				webDriver = openBrowser();
				
				for(int i = start; i<= end ; i++) {
					webDriver.get(object.get("pageUrl")+String.valueOf(i));
					Thread.sleep(3000);
					By titleLink = By.xpath((String) object.get("titleLink"));
					List<WebElement> titleLinks = webDriver.findElements(titleLink);
					
					for(WebElement e : titleLinks) {
						links.put(e.getAttribute("href"));
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally {
				webDriver.close();
				webDriver.quit();
			}
		}
	}
	
	public void saveData(List<Crawl> dataList) {
		crawlService.saveCrawl(dataList, "");
	}
}
