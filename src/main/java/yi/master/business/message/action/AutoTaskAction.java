package yi.master.business.message.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.AutoTask;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.service.AutoTaskService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.SystemConsts;
import yi.master.coretest.task.JobManager;
import yi.master.util.PracticalUtils;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class AutoTaskAction extends BaseAction<AutoTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AutoTaskService autoTaskService;
	@Autowired
	private TestSetService testSetService;
	@Autowired
	private JobManager jobManager;
	
	@Autowired
	public void setAutoTaskService(AutoTaskService autoTaskService) {
		super.setBaseService(autoTaskService);
		this.autoTaskService = autoTaskService;
	}

	
	
	
	@Override
	public String edit() {
		
		checkObjectName();
		if (!checkNameFlag.equals("true")) {
			jsonMap.put("returnCode", ReturnCodeConsts.NAME_EXIST_CODE);
			jsonMap.put("msg", "重复的任务名称!");
			
			return SUCCESS;
		}
		if (model.getTaskId() == null) {
			model.setUser((User)FrameworkUtil.getSessionMap().get("user"));
		}
		return super.edit();
	}




	/**
	 * 判断标任务名重复性
	 * 新增或者修改状态下均可用
	 */
	@Override
	public void checkObjectName() {
		AutoTask task = autoTaskService.findByName(model.getTaskName());
		
		checkNameFlag = (task != null && !task.getTaskId().equals(model.getTaskId())) ? "重复的任务名称" : "true";
		
		if (model.getTaskId() == null) {
			checkNameFlag = (task == null) ? "true" : "重复的任务名称";
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object processListData(Object o) {
		
		List<AutoTask> tasks = (List<AutoTask>) o;
		
		for (AutoTask task:tasks) {
			setTestSetName(task);
		}
		return tasks;
	}	
		
	@Override
	public String get() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		model = autoTaskService.get(id);
		
		setTestSetName(model);
		
		jsonMap.put("object", model);		
		return SUCCESS;
	}

	/**
	 * 启动quartz
	 * @return
	 */
	public String startQuartz() {
		String quartzFlag = (String) FrameworkUtil.getApplicationMap().get(SystemConsts.QUARTZ_SCHEDULER_START_FLAG);
		
		if (SystemConsts.QUARTZ_SCHEDULER_IS_START.equalsIgnoreCase(quartzFlag)) {
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_HAS_BEEN_START);
			jsonMap.put("msg", "定时任务调度器已经是启动状态了!");
			return SUCCESS;
		}
		
		jobManager.startTasks();
		FrameworkUtil.getApplicationMap().put(SystemConsts.QUARTZ_SCHEDULER_START_FLAG, SystemConsts.QUARTZ_SCHEDULER_IS_START);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("status", SystemConsts.QUARTZ_SCHEDULER_IS_START);
		return SUCCESS;
	}
	
	/**
	 * 停止quartz
	 * @return
	 */
	public String stopQuartz() {
		String quartzFlag = (String) FrameworkUtil.getApplicationMap().get(SystemConsts.QUARTZ_SCHEDULER_START_FLAG);
		
		if (!SystemConsts.QUARTZ_SCHEDULER_IS_START.equalsIgnoreCase(quartzFlag)) {
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_HAS_BEEN_STOP);
			jsonMap.put("msg", "定时任务调度器已经被暂停了!");
			return SUCCESS;
		}
		
		jobManager.stopTasks();
		FrameworkUtil.getApplicationMap().put(SystemConsts.QUARTZ_SCHEDULER_START_FLAG, SystemConsts.QUARTZ_SCHEDULER_IS_STOP);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("status", SystemConsts.QUARTZ_SCHEDULER_IS_STOP);
		return SUCCESS;
	}
	
	/**
	 * 添加可运行的任务到quartz
	 * @return
	 */
	public String startRunableTask() {
		String quartzFlag = (String) FrameworkUtil.getApplicationMap().get(SystemConsts.QUARTZ_SCHEDULER_START_FLAG);
			
		if (!SystemConsts.QUARTZ_SCHEDULER_IS_START.equalsIgnoreCase(quartzFlag)) {
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_HAS_BEEN_STOP);
			jsonMap.put("msg", "请先恢复定时任务调度器!");
			return SUCCESS;
		}
		model = autoTaskService.get(model.getTaskId());
		
		if (!PracticalUtils.isNormalString(model.getTaskCronExpression())) {
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_NEED_CRON_EXPRESSION);
			jsonMap.put("msg", "请先设置定时任务规则/cron表达式!");
			return SUCCESS;
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);		
		try {
			jobManager.addTimeTask(model);
		} catch (Exception e) {
			
			LOGGER.error("定时任务规则设置有误!", e);
			jsonMap.put("msg", "启动定时任务出错，详情：" + e.getMessage());
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_CRON_EXPRESSION_SETTING_ERROR);	
		}		
		return SUCCESS;
	}
	
	/**
	 * 停止运行中的任务
	 * @return
	 */
	public String stopRunningTask() {
		
		String quartzFlag = (String) FrameworkUtil.getApplicationMap().get(SystemConsts.QUARTZ_SCHEDULER_START_FLAG);
		
		if (!SystemConsts.QUARTZ_SCHEDULER_IS_START.equalsIgnoreCase(quartzFlag)) {
			jsonMap.put("returnCode", ReturnCodeConsts.QUARTZ_HAS_BEEN_STOP);
			jsonMap.put("msg", "定时任务调度器不在运行状态!");
			return SUCCESS;
		}
		model = autoTaskService.get(model.getTaskId());
		jobManager.stopTimeTask(model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	public String getQuartzStatus() {
		jsonMap.put("status", FrameworkUtil.getApplicationMap().get(SystemConsts.QUARTZ_SCHEDULER_START_FLAG));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	public String updateCronExpression() {
		autoTaskService.updateExpression(model.getTaskId(), model.getTaskCronExpression());
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	
	private void setTestSetName(AutoTask task) {
		if ("0".equals(task.getRelatedId())) {
			task.setSetName("全量测试");
		} else {
			switch (task.getTaskType()) {
			case "0":
				TestSet set = testSetService.get(task.getRelatedId());
				task.setSetName(set == null ? "测试集已删除" : set.getSetName());
				break;
			case "1":
				task.setSetName("");
				break;
			default:
				break;
			}
		}
	}
	
}
