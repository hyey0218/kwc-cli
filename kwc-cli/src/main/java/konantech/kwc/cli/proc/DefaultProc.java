package konantech.kwc.cli.proc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import konantech.kwc.cli.common.CommonUtils;
import konantech.kwc.cli.entity.Crawl;
import lombok.Setter;


@Setter
public class DefaultProc implements Runnable{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	WebCrawler wc;
	ExecutorService threadService;
	protected WebDriver driver;
	protected String channel;
	protected String site;
	protected String board;
	
	protected String title;
	protected String contId;
	protected String content;
	protected String writer;
	protected String writeDate;
	protected String wdatePattern;
	protected String param1;
	protected String param2;
	protected String extParam;
	
	BlockingQueue<String> links;
	
	List<Crawl> crawlList = new ArrayList<Crawl>();
	ConcurrentHashMap<String,Short> errorLink;
	
	protected Crawl obj;
	
	public DefaultProc(WebCrawler wc,ExecutorService threadService, BlockingQueue<String> links, ConcurrentHashMap<String,Short> errorLink) {
		this.wc = wc;
		this.threadService = threadService;
		this.links = links;
		this.errorLink = errorLink;
		try {
			this.driver=openBrowser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setSiteName(String channel, String site, String board) {
		this.channel = channel;
		this.site = site;
		this.board = board;
	}
	public WebDriver openBrowser() throws Exception{
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); 
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.EAGER); // only HTML document loading, (discards loding of css/image...)
        options.setHeadless(true); // 특정 엘리먼트를 못찾음...; don't use
        options.setProxy(null);
        
        ChromeDriver webDriver = new ChromeDriver(options);
		webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); // implicit wait
		return webDriver;
	}
	@Override
	public void run() {
		proc();
	}
	public void proc() {
		String url = null;
		try {
			while( ( url = links.poll( 15, TimeUnit.SECONDS) ) != null ) {
				logger.info(url);
				obj = new Crawl();
				try {
					this.driver.get(url);
					Thread.sleep(3000);
					obj.setCrawledTime(LocalDateTime.now());
					obj.setChannel(channel);
					obj.setSiteName(site);
					obj.setBoardName(board);
					obj.setUrl(getCurrentUrl(driver.getCurrentUrl()));
					obj.setHashed(getCurruentHashed(obj.getUrl()));
					obj.setUniqkey(getCommonUniqkey(obj.getUrl()));
					// 제목>작성자>내용>게시일자 순서 변경X !!
					obj.setTitle(getCurrentTitle());
					obj.setWriteId(getCurrentWriteId());
					obj.setDoc(getCurrentContent());
					obj.setWriteTime(getWdTime());
					
					crawlList.add(obj);
				}catch(org.openqa.selenium.UnhandledAlertException ua){
					logger.error("-----------------UnhandledAlertException SKIP---------------------");
					continue;
				}catch(org.openqa.selenium.NoSuchElementException ne) {
					logger.error("-------------[NoSuchElementException-"+errorLink.size()+"] -----------");
					logger.error(ne.getMessage());
					if(errorLink.size() < 3) {
						if(errorLink.get(url) != null) {
							Short dd = (short) errorLink.get(url);
							if(dd >= 3) {
								logger.error("SKIP : " + driver.getCurrentUrl());
								errorLink.remove(url);
								continue;
							}
							errorLink.put(url, ++dd);
						}else {
							errorLink.put(url, (short) 1);
						}
						logger.error("RETRY : " + driver.getCurrentUrl());
						links.put(url);
						continue;
					}else if(errorLink.size() >= 3) {
						//에러 3번 이상일때는 더이상 수집 안해
						logger.error("NoSuchElementException is more than 3 times");
						throw ne;
					}
				}
			}
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EXCEPTION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error(CommonUtils.getStackTrace(e));
		}
		finally {
			quit();
		}
		wc.saveData(crawlList);
		System.out.println("+++++++++++++++ cnt : " + crawlList.size() + " +++++++++++++++++++++");
		logger.info("["+ Thread.currentThread().getName()+"] END " );
	}
	
	public void quit() {
		this.driver.close();
		this.driver.quit();
	}
	
	
	/**
	 * 공통 UniqKey 세팅 ( _path 사용시 Crawl 객체 setUrl() 이후 불러야됨 )
	 * @param obj
	 * @param contId
	 */
	public String getCommonUniqkey(String url) {
		String tmp = "";
		if(StringUtils.isEmpty(contId) )
			return tmp;
		
		boolean idIsXpath = StringUtils.startsWith(contId, "//");
		if(idIsXpath)
			tmp = driver.findElement(By.xpath(contId)).getAttribute("value") ;
		else {
			if(contId.equals("_path"))
				tmp = CommonUtils.getUriLastPath(url);
			else
				tmp = CommonUtils.getUriParamValue(url, contId);
		}
		return tmp;
	}
	
	public String getCurrentUrl(String url) {
		String tmp = url;
		if(!StringUtils.isEmpty(extParam)) {
			tmp = CommonUtils.getUriParamRemove(url, extParam);
		}
		return tmp;
	}
	
	
	/**
	 * title
	 * xpath 로 정확히 안잡히는 애들 param2 pattern remove
	 * @param Obj
	 */
	public String getCurrentTitle() throws Exception {
		String tmp = driver.findElement(By.xpath(title)).getText();
		if(!StringUtils.isEmpty(param2)) {
			tmp = CommonUtils.removeRegexPattern(tmp, param2);
		}
		return tmp.trim();
	}
	
	/**
	 * write time
	 * @return
	 */
	public LocalDateTime getWdTime() {
		String wdStr = driver.findElement(By.xpath(writeDate)).getText();
		boolean isTimePattern = StringUtils.containsAny(wdatePattern, "Hms");
		if(!StringUtils.isEmpty(param1)) 
			wdStr = CommonUtils.removeRegexPattern(wdStr,param1);
		
		return CommonUtils.stringToLocalDateTime(wdStr, wdatePattern, isTimePattern);
			
	}
	public String getCurrentWriteId() {
		return driver.findElement(By.xpath(writer)).getText();
	}
	public String getCurrentContent() {
		return driver.findElement(By.xpath(content)).getText();
	}
	
	public String getCurruentHashed(String url) {
		return CommonUtils.getEncMd5(url);
	}
}
