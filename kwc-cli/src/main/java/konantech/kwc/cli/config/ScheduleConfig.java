package konantech.kwc.cli.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@Configuration
@EnableScheduling
public class ScheduleConfig {

	private static int TASK_CORE_POOL_SIZE = 50; //AsyncConfig 수와 같게 해주자
    private static String EXECUTOR_BEAN_NAME = "KWCSchedule-";
	
//	@Override
//	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//		// 기본 scheduled default는 스레드 1개로 동작 -> 설정 변경하여 스레딩함.
//		ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();
//		tpts.setPoolSize(1);
//		tpts.setThreadNamePrefix("KWCLinkage-");
//		tpts.initialize();
//		taskRegistrar.setTaskScheduler(tpts);
//	}
//	@Scheduled(cron = "*/10 * * * * *")  // 매 10초
//	public void fixedTask() {
//		System.out.println(">>>>>>>>>>>>>>> Scheduled Task");
//		System.out.println("Current Thread : " + Thread.currentThread().getName());
////		Thread.sleep(30000);
//	}
	
	
	@Bean
	public ThreadPoolTaskScheduler configureTasks() {
		// 기본 scheduled default는 스레드 1개로 동작 -> 설정 변경하여 스레딩함.
		ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();
		tpts.setPoolSize(TASK_CORE_POOL_SIZE);
		tpts.setThreadNamePrefix(EXECUTOR_BEAN_NAME);
		tpts.initialize();
		return tpts;
	}
	
	
}
