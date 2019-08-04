package yi.master.coretest.task;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.service.InterfaceProbeService;
import yi.master.business.message.bean.AutoTask;
import yi.master.business.message.service.AutoTaskService;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.constant.SystemConsts;
import yi.master.coretest.task.probe.ProbeTaskJobAction;
import yi.master.coretest.task.probe.ProbeTaskJobListener;
import yi.master.coretest.task.time.TimeTaskJobAction;
import yi.master.coretest.task.time.TimeTaskJobListener;
import yi.master.util.PracticalUtils;


/**
 * quartz任务管理类
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.26
 *
 */
public class JobManager {
	
	private static final Logger LOGGER = Logger.getLogger(JobManager.class);
	
	@Resource  
    private Scheduler scheduler ;
	@Resource
	private TimeTaskJobListener timeTaskJobListener;
	@Resource
	private ProbeTaskJobListener probeTaskJobListener;
	@Resource
	private AutoTaskService taskService;
	@Resource
	private GlobalVariableService globalVariableService;
	@Resource
	private InterfaceProbeService interfaceProbeService;
      
    public Scheduler getScheduler() {  
        return scheduler;  
    }  
  
    public void setScheduler(Scheduler scheduler) {  
        this.scheduler = scheduler;  
    }    
    
    /**
     * 添加运行探测任务
     * @param task
     * @return
     */
    public boolean addProbeTask (InterfaceProbe task) {
    	boolean flag = false;
    	TriggerKey triggerKey = TriggerKey.triggerKey("" + task.getProbeId(), SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY);
    	SimpleScheduleBuilder scheduleBuilder = null;
    	
    	if (task.getConfig().getMaxCallTime() == -1) {
    		scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(task.getConfig().getIntervalTime());
    	} else {
    		scheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(task.getConfig().getMaxCallTime(), task.getConfig().getIntervalTime());
    	}
    	try {
			SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
			if (trigger == null) {
				JobDetail jobDetail = JobBuilder.newJob(ProbeTaskJobAction.class)
						.withIdentity("" + task.getProbeId(), SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY)
						.build();
				jobDetail.getJobDataMap().put("" + task.getProbeId(), task);
				trigger = TriggerBuilder.newTrigger().withIdentity("" + task.getProbeId(), SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY)
						.withSchedule(scheduleBuilder)
						.build();
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();  
                scheduler.rescheduleJob(triggerKey, trigger);
			}
			flag = true;
			//更新任务状态
			task.setMark("");
			task.setStatus("1");
			task.setCallCount(0);
			task.setFirstCallTime(null);
			task.setLastCallTime(null);
			task.setCycleAnalysisData("");
			interfaceProbeService.edit(task);
		} catch (Exception e) {
			LOGGER.error("创建quartz探测任务失败:[任务ID]-" + task.getProbeId(), e);
		}
    	
    	return flag;
    }
    
    /**
     * 停止运行探测任务
     * @param task
     * @return
     */
    public boolean stopProbeTask (InterfaceProbe task) {
    	boolean flag = false;   	
    	//获取任务信息数据
    	TriggerKey triggerKey = TriggerKey.triggerKey("" + task.getProbeId(), SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY);
    	
    	try {
    		SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
        	//任务存在就删除(停止)
        	if(trigger != null){
        		scheduler.unscheduleJob(triggerKey);
        	}
        	flag = true;
        	//更新状态
        	task.setStatus("0");
			interfaceProbeService.edit(task);
		} catch (Exception e) {
			
			LOGGER.error("停止quartz探测任务失败:,[任务ID]-" + task.getProbeId(), e);
		}
    	
    	return flag;
    	
    }
    
    
    
    /**
     * 添加一个定时任务
     * @param task   任务详情
     * @param classz 执行任务的详细job
     * @throws SchedulerException 
     */
	public boolean addTimeTask(AutoTask task) {
    	boolean flag = false;
    	//获取任务信息数据
    	TriggerKey triggerKey = TriggerKey.triggerKey("" + task.getTaskId(), SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY);
    	//表达式调度构建器  
    	CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(PracticalUtils
    			.replaceGlobalVariable(task.getTaskCronExpression(), globalVariableService)); 
    	try {
    		//复杂触发器
    		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        	//判断添加的任务触发器是否已经存在？
        	if(trigger == null){
        		//不存在,创建一个
        		 JobDetail jobDetail = JobBuilder.newJob(TimeTaskJobAction.class).withIdentity("" + task.getTaskId(), SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY).build();       		 
        		 jobDetail.getJobDataMap().put("" + task.getTaskId(), task);
       		                 
                 //按cronExpression表达式构建一个新的trigger  
                 trigger = TriggerBuilder.newTrigger().withIdentity("" + task.getTaskId(), SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY).withSchedule(scheduleBuilder).build();  
                 
                 scheduler.scheduleJob(jobDetail, trigger);                 
                 
        	} else {
        		//存在就更新表达式
        		//表达式调度构建器  
                //按新的cronExpression表达式重新构建trigger  
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();  
                //按新的trigger重新设置job执行  
                scheduler.rescheduleJob(triggerKey, trigger);
        	}
        	flag = true;
        	
        	//更新task状态
        	task.setStatus("0");
        	task.setLastFinishTime(null);
        	task.setRunCount(0);
        	taskService.edit(task);
		} catch (Exception e) {
			LOGGER.error("创建quartz定时任务失败:\n[任务名称]-" + task.getTaskName() + ",[任务ID]-" + task.getTaskId(), e);
		}
    	return flag;   
    }
    
    /**
     * 停止定时任务
     * @param task
     * @throws SchedulerException 
     */
    public boolean stopTimeTask(AutoTask task) {
    	boolean flag = false;   	
    	//获取任务信息数据
    	TriggerKey triggerKey = TriggerKey.triggerKey("" + task.getTaskId(), SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY);
    	
    	try {
    		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        	//任务存在就删除(停止)
        	if(trigger!=null){
        		scheduler.unscheduleJob(triggerKey);
        	}
        	flag = true;
        	//更新task状态
        	taskService.updateStatus(task.getTaskId(), "1");
		} catch (Exception e) {
			LOGGER.error("停止quartz定时任务失败:\n[任务名称]-" + task.getTaskName() + ",[任务ID]-" + task.getTaskId(), e);
		}
    	
    	return flag;
    }
    
    
    /** 
     * 启动所有任务 
     * 
     */  
    public void startTasks() {  
        try {     
        	WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
            ServletContext servletContext = webApplicationContext.getServletContext(); 
            if (SystemConsts.QUARTZ_SCHEDULER_IS_STOP.equals(servletContext.getAttribute(SystemConsts.QUARTZ_SCHEDULER_START_FLAG))) {
            	LOGGER.info("恢复所有定时任务...");
            	scheduler.resumeAll();
            	return;
            }
            
            LOGGER.info("启动Quartz定时任务管理器...");
            scheduler.start();  
            scheduler.getListenerManager().addJobListener(timeTaskJobListener, GroupMatcher.jobGroupEquals(SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY));
            scheduler.getListenerManager().addJobListener(probeTaskJobListener, GroupMatcher.jobGroupEquals(SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY));;
            LOGGER.info("启动Quartz可运行定时任务...");
            //启动当前所有可运行的定时任务
            List<AutoTask> tasks = taskService.findRunTasks();
            LOGGER.info("当前可运行定时任务为" + tasks.size());
            for (AutoTask t:tasks) {
            	addTimeTask(t);
            }
            //启动当前所有可运行的探测任务
            List<InterfaceProbe> probes = interfaceProbeService.findRunTasks();
            LOGGER.info("当前可运行探测任务为" + probes.size());   
            for (InterfaceProbe probe:probes) {
            	addProbeTask(probe);
            }
            LOGGER.info("定时任务、探测任务启动完毕...");     
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
    
    /**
     * 停止所有任务
     */
    public void stopTasks(){
    	try {
    		//shutdown之后无法重启,所以使用pauseAll通过暂停
			//scheduler.shutdown();
    		LOGGER.info("暂停所有任务...");
			scheduler.pauseAll();			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}
