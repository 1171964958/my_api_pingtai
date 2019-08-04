package yi.master.coretest.task.probe;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import yi.master.constant.SystemConsts;

/**
 * 探测任务执行监听器
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.26
 *
 */
public class ProbeTaskJobListener implements JobListener {

	public static final String LISTENER_NAME = SystemConsts.QUARTZ_PROBE_TASK_NAME_PREFIX_KEY + "-autoTest";
	
	@Override
	public String getName() {
		
		return LISTENER_NAME;
	}

	/**
	 * 将要被执行的时候
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		
		
	}

	
	/**
	 * 被触发监听器中断执行的时候
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		
		
	}

	/**
	 * 执行完成的时候
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		
		
	}

}
