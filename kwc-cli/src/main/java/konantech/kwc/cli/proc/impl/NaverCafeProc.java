package konantech.kwc.cli.proc.impl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import konantech.kwc.cli.proc.WebCrawler;
import konantech.kwc.cli.proc.DefaultProc;

public class NaverCafeProc extends DefaultProc {

	

	public NaverCafeProc(WebCrawler wc, ExecutorService threadService, BlockingQueue<Map<String, String>> links,
			ConcurrentHashMap<String, Short> errorLink) {
		super(wc, threadService, links, errorLink);
		// TODO Auto-generated constructor stub
	}

	public void proc() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
	}
}
