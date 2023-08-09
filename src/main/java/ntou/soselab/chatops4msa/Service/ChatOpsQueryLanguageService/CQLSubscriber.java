package ntou.soselab.chatops4msa.Service.ChatOpsQueryLanguageService;

import ntou.soselab.chatops4msa.Exception.CapabilityRoleException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestrator.CapabilityOrchestrator;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CQLSubscriber implements Job {
    private CapabilityOrchestrator capabilityOrchestrator;
    private JDAService jdaService;
    private Scheduler scheduler;
    private List<String> subscribeList = new ArrayList<>();

    @Autowired
    public CQLSubscriber(CapabilityOrchestrator capabilityOrchestrator, JDAService jdaService) {
        this.capabilityOrchestrator = capabilityOrchestrator;
        this.jdaService = jdaService;
    }

    /**
     * for creating a job
     */
    public CQLSubscriber() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println(">>> trigger the subscribed job");

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String functionName = jobDataMap.getString("functionName");
        Map<String, String> argumentMap = (Map<String, String>) jobDataMap.get("argumentMap");
        String username = jobDataMap.getString("username");
        List<String> roleNameList = (List<String>) jobDataMap.get("roleNameList");
        CapabilityOrchestrator capabilityOrchestrator = (CapabilityOrchestrator) jobDataMap.get("capabilityOrchestrator");
        JDAService jdaService = (JDAService) jobDataMap.get("jdaService");

        System.out.println("[Time] " + new Date());
        System.out.println("[Command] /" + functionName);
        System.out.println("[Options] " + argumentMap);
        System.out.println("[User Name] " + username);
        System.out.println("[User Role] " + roleNameList);

        try {
            capabilityOrchestrator.performTheCapability(functionName, argumentMap, roleNameList);

        } catch (CapabilityRoleException e) {
            e.printStackTrace();
            String warningMessage = "[WARNING] " + e.getLocalizedMessage();
            System.out.println(warningMessage);
            jdaService.sendChatOpsChannelWarningMessage(warningMessage);

        } catch (ToolkitFunctionException e) {
            e.printStackTrace();
            String errorMessage = "[ERROR] " + e.getLocalizedMessage();
            System.out.println(errorMessage);
            jdaService.sendChatOpsChannelErrorMessage(errorMessage);
        }

        System.out.println("<<< end of current subscribed job");
        System.out.println();
    }

    public void subscribeTheCapability(String username,
                                       List<String> roleNameList,
                                       String functionName,
                                       Map<String, String> argumentMap,
                                       String cron) {

        String info = "cron: " + cron + "\n" +
                "command: " + functionName + "\n" +
                "argument: " + argumentMap + "\n" +
                "by: " + username;
        jdaService.sendChatOpsChannelInfoMessage(info);

        // record the subscription
        subscribeList.add(info);

        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();

            // create a job
            JobDetail job = JobBuilder.newJob(this.getClass())
                    .storeDurably()
                    .withIdentity(functionName, username)
                    .build();

            // put the job data
            JobDataMap jobDataMap = job.getJobDataMap();
            jobDataMap.put("capabilityOrchestrator", capabilityOrchestrator);
            jobDataMap.put("jdaService", jdaService);
            jobDataMap.put("functionName", functionName);
            jobDataMap.put("argumentMap", argumentMap);
            jobDataMap.put("username", username);
            jobDataMap.put("roleNameList", roleNameList);

            // create a trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(functionName, username)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void checkAllSubscription() {
        if (subscribeList.isEmpty()) {
            jdaService.sendChatOpsChannelWarningMessage("[WARNING] There Are Currently No Subscriptions");
        } else {
            for (String subscribeInfo : subscribeList) {
                jdaService.sendChatOpsChannelInfoMessage(subscribeInfo);
            }
        }
    }

    public void unsubscribeAllCapability() {
        try {
            scheduler.clear();
            subscribeList.clear();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}