package yi.master.business.api.service.task;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.api.bean.ApiReturnInfo;
import yi.master.business.localize.shanxi.bean.WebScriptModule;
import yi.master.business.localize.shanxi.bean.WebScriptTask;
import yi.master.business.localize.shanxi.service.WebScriptModuleService;
import yi.master.business.localize.shanxi.service.WebScriptTaskService;
import yi.master.constant.SystemConsts;
import yi.master.coretest.web.framework.BaseFrameworkExecutor;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;


@Service
public class WebApiTaskService implements ApiTaskService{
	
	@Autowired
	private WebScriptModuleService moduleService;
	@Autowired
	private WebScriptTaskService taskService;

	@Override
	public ApiReturnInfo startTest(String moduleId, String guid) {
		
		JSONObject obj = new JSONObject();
		
		if (BaseFrameworkExecutor.getRunningWebScriptId() != null) {
			obj.put("taskId", BaseFrameworkExecutor.getRunningWebScriptId());
			return new ApiReturnInfo(ApiReturnInfo.DISABLED_CODE, "当前有正在执行的WEB测试任务,请稍等!", obj);
		}
				
		WebScriptModule module = moduleService.findByModuleCode(moduleId);		
		if (module == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该模块不存在", null);
		}
		
		if (StringUtils.isNotBlank(guid) && taskService.findByGuid(guid) != null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该guid标识已经存在,请更换", null);
		}
		
		WebScriptTask task = BaseFrameworkExecutor.getExecutorInstance(module.getFrameworkType()).startTest(module, guid);
		task.setTaskType("1");
		task.setTaskId(taskService.save(task));
		
		if ("Y".equalsIgnoreCase(task.getFinishFlag())) {
			return checkTask(task);
		} else {
			BaseFrameworkExecutor.setRunningWebScriptId(task.getTaskId());
			
			obj.put("taskId", task.getTaskId());
			obj.put("finishFlag", task.getFinishFlag());
			obj.put("moduleName", task.getModule().getModuleName());
			obj.put("startTime", PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, task.getStartTime()));
					
			return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "执行测试成功!", obj);
		}
		
	}

	@Override
	public ApiReturnInfo checkTask(Integer taskId) {
		
		WebScriptTask task = taskService.get(taskId);
		if (task == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "测试任务不存在", null);
		}
		
		return checkTask(task);
	}

	@Override
	public ApiReturnInfo stopTest(Integer taskId) {
		
		WebScriptTask task = taskService.get(taskId);
		if (task == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "测试任务不存在", null);
		}
		
		if ("Y".equals(task.getFinishFlag())) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该测试任务已完成,不需要停止", null);
		}
		
		if (taskId != BaseFrameworkExecutor.getRunningWebScriptId()) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该测试任务已经停止过啦!", null);
		}
		
		task.setMark("已被强制停止!");
		taskService.edit(task);
		
		BaseFrameworkExecutor.setRunningWebScriptId(null);
		BaseFrameworkExecutor.getRuningWebScriptProcess().destroy();
		BaseFrameworkExecutor.setRuningWebScriptProcess(null);
				
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "测试任务成功停止", null);
	}

	@Override
	public ApiReturnInfo listModule() {
		
		JSONArray arr = new JSONArray();
		
		List<WebScriptModule> modules = moduleService.findAll();
		for (WebScriptModule m:modules) {
			JSONObject obj = new JSONObject();
			obj.put("moduleId", m.getModuleCode());
			obj.put("moduleName", m.getModuleName());
			obj.put("mark", m.getMark());
			arr.add(obj);
		}
		
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "查询信息成功", arr);
	}

	@Override
	public ApiReturnInfo checkTaskByGuid(String guid) {
		
		
		WebScriptTask task = taskService.findByGuid(guid);
		if (task == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "测试任务不存在", null);
		}
		
		return checkTask(task);
	}

	
	private ApiReturnInfo checkTask(WebScriptTask task) {
		JSONObject obj = new JSONObject();
		obj.put("reportUrl", CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME) + "/" + task.getReportPath());
		obj.put("taskId", task.getTaskId());
		obj.put("finishFlag", task.getFinishFlag());
		obj.put("guid", task.getGuid());
		obj.put("desc", JSONObject.fromObject(StringUtils.isNotBlank(task.getDetailJson()) ? task.getDetailJson() : "{}"));
		if (obj.getJSONObject("desc").isEmpty()) {
			obj.put("status", "FAIL");
		} else {
			obj.put("status", obj.getJSONObject("desc").getDouble("successPercent") == 100.00 ? "SUCCESS" : "FAIL");
		}

		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "查询任务信息成功", obj);
	}
}
