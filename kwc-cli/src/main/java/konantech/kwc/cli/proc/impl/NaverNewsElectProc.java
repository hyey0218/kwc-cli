package konantech.kwc.cli.proc.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;

import konantech.kwc.cli.common.CommonUtils;
import konantech.kwc.cli.proc.DefaultProc;
import konantech.kwc.cli.proc.WebCrawler;

public class NaverNewsElectProc extends DefaultProc {

	
	public NaverNewsElectProc(WebCrawler wc, ExecutorService threadService, BlockingQueue<Map<String, String>> links,
			ConcurrentHashMap<String, Short> errorLink) {
		super(wc, threadService, links, errorLink);
	}


	@Override
	public void proc() {
		String url = null;
		Map<String,String> map = null;
		try {
			while( ( map = links.poll( 15, TimeUnit.SECONDS) ) != null ) {
				url = map.get("href");
				this.driver.get(url);
//				logger.info(url);
				String curUrl = driver.getCurrentUrl();
				String contents = driver.getPageSource();
				wc.saveDataAsFile(map.get("news"), map.get("text"), contents);
				Thread.sleep(1000);
			}
		}catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EXCEPTION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			logger.error(CommonUtils.getStackTrace(e));
		}
		finally {
			quit();
		}
		logger.info("["+ Thread.currentThread().getName()+"] END " );
	}
	
}
