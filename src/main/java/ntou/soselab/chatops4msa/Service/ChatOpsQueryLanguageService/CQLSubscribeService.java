package ntou.soselab.chatops4msa.Service.ChatOpsQueryLanguageService;

import ntou.soselab.chatops4msa.Service.CapabilityOrchestrator.CapabilityOrchestrator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;

@Service
public class CQLSubscribeService {

    public CQLSubscribeService() {
//        try {
//
//            // TODO
//
//            // 创建调度器
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//            // 定义定时任务
//            JobDetail job = JobBuilder.newJob(CapabilityOrchestrator.class)
//                    .withIdentity("myJob", "group1")
//                    .build();
//
//            // 定义触发器，使用 cron 表达式
//            Trigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("myTrigger", "group1")
//                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 ? * MON"))  // 每周一早上9点执行
//                    .build();
//
//            // 将定时任务和触发器关联到调度器
//            scheduler.scheduleJob(job, trigger);
//
//            // 启动调度器
//            scheduler.start();
//
//            // 让程序运行一段时间后停止调度器
//            Thread.sleep(5000);
//
//            // 停止调度器
//            scheduler.shutdown();
//
//        } catch (SchedulerException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private String getCronExpress() {
        return "0 9 * * *";
    }
}
