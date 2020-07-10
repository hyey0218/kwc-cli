package konantech.kwc.cli.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import konantech.kwc.cli.proc.WebCrawler;

@Component
public class AppStartListener implements ApplicationListener<ApplicationStartedEvent> {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ResourceLoader resourceLoader;
	@Autowired
	ThreadPoolTaskScheduler tpts;
	@Autowired
	WebCrawler webCrawler;
	
	@Value("${json.path}")
	String jsonPath;
	
	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		appStart();
	}
	
	
	@SuppressWarnings("unchecked")
	public void appStart() {
		try {
			
			File path = ResourceUtils.getFile(jsonPath);
			JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(new File(path,"list.json")));
			System.out.println(path.getAbsolutePath());
			JSONArray jArray = (JSONArray) json.get("list");
			JSONObject detail = (JSONObject) json.get("detail");
			
			
			jArray.forEach((j)->{
				System.out.println(j);
				JSONArray colArray = (JSONArray) detail.get(j);
				
				for(int i=0;i <colArray.size() ; i++) {
					try {
						JSONObject obj = (JSONObject) colArray.get(i);
						String cron = (String) obj.get("cron");
						String fileName = (String) obj.get("name");
						if(!cron.equals("")) {
							int start = Integer.valueOf( (String) obj.get("start") );
							int end = Integer.valueOf( (String) obj.get("end") );
							String filePath = j+"/"+fileName+".json";
							System.out.println(filePath);
							File file = new File(path,"collectors/"+filePath);
							task(cron,file.getAbsolutePath(),start,end);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
					
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void task(String cron, String filePath, int start, int end) {
		ScheduledFuture<?> future = this.tpts.schedule(()->{
			logger.info("scheduled task start!!!");
			try {
				JSONObject object = (JSONObject) new JSONParser().parse(new FileReader(filePath));
				webCrawler.setObject(object);
				webCrawler.setPage(start, end);
				try {
					webCrawler.work();
				} catch (Exception e) {
					e.printStackTrace();
				}	
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
					
		},new CronTrigger(cron));
	}

}
