package konantech.kwc.cli.proc.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import konantech.kwc.cli.proc.WebCrawler;
import konantech.kwc.cli.common.CommonUtils;
import konantech.kwc.cli.proc.DefaultProc;

public class DaumNewsProc extends DefaultProc {

	

	public DaumNewsProc(WebCrawler wc, ExecutorService threadService, BlockingQueue<Map<String, String>> links,
			ConcurrentHashMap<String, Short> errorLink) {
		super(wc, threadService, links, errorLink);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LocalDateTime getWdTime() {
		String wdStr = "";
		try {
			wdStr = driver.findElement(By.xpath(writeDate)).getText();
		}catch ( org.openqa.selenium.NoSuchElementException e){
			wdStr = driver.findElement(By.xpath("//*[@id=\"cSub\"]/div/span/span/span")).getText();
			obj.setWriteId("");
		}
		boolean isTimePattern = StringUtils.containsAny(wdatePattern, "Hms");
		return CommonUtils.stringToLocalDateTime(wdStr, wdatePattern, isTimePattern);
	}
	

}
