package yi.master.business.api.service.task;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.api.bean.ApiReturnInfo;
import yi.master.business.message.bean.TestReport;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.service.TestReportService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.user.service.UserService;
import yi.master.constant.SystemConsts;
import yi.master.coretest.message.test.MessageAutoTest;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

@Service
public class InterfaceApiTaskService implements ApiTaskService {
	
	@Autowired
	private TestReportService reportService;
	@Autowired
	private TestSetService setService;
	@Autowired
	private UserService userService;
	@Autowired
	private MessageAutoTest autoTest;

	@Override
	public ApiReturnInfo startTest(String moduleId, String guid) {
		
		Integer setId = Integer.valueOf(moduleId);
		TestSet set = setService.get(setId);
		if (set == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该模块不存在", null);
		}
		if (StringUtils.isNotBlank(guid) && reportService.findByGuid(guid) != null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该guid已存在,请更换", null);
		}
		
		int[] result = autoTest.batchTest(userService.get(SystemConsts.ADMIN_USER_ID), setId, "外部API调用", guid);
		
		if (result == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "没有足够的测试场景可供测试", null);
		}
		
		TestReport report = reportService.get(result[0]);
		if ("Y".equalsIgnoreCase(report.getFinishFlag())) {
			return checkTask(report);		
			
		} else {
			JSONObject obj = new JSONObject();			
			obj.put("taskId", report.getReportId());
			obj.put("finishFlag", report.getFinishFlag());
			obj.put("moduleName", set.getSetName());
			obj.put("startTime", PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, report.getCreateTime()));
			return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "执行测试成功!", obj);
		}
	}

	@Override
	public ApiReturnInfo checkTask(Integer reportId) {
		
		TestReport report = reportService.get(reportId);
		if (report == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "测试任务不存在", null);
		}
		return checkTask(report);
	}

	@Override
	public ApiReturnInfo stopTest(Integer reportId) {
		
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "停止测试任务成功", null);
	}

	@Override
	public ApiReturnInfo listModule() {
		
		JSONArray arr = new JSONArray();
		
		List<TestSet> sets = setService.findAll("parented='1'");
		for (TestSet set:sets) {
			JSONObject obj = new JSONObject();
			obj.put("moduleId", set.getSetId());
			obj.put("moduleName", set.getSetName());
			obj.put("mark", set.getMark());
			arr.add(obj);
		}
		
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "查询信息成功", arr);
	}

	@Override
	public ApiReturnInfo checkTaskByGuid(String guid) {
		
		TestReport report = reportService.findByGuid(guid);
		if (report == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "该测试任务不存在", null);
		}
		return checkTask(report);
	}
	
	private ApiReturnInfo checkTask(TestReport report) {
		JSONObject obj = new JSONObject();
		obj.put("reportUrl", CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME) + "/" + report.getReportHtmlPath());
		obj.put("taskId", report.getReportId());
		obj.put("finishFlag", report.getFinishFlag());
		obj.put("guid", report.getGuid());
		
		//成功则返回详细内容
		if ("Y".equals(report.getFinishFlag())) {			
			JSONObject reportInfo = JSONObject.fromObject(reportService.getDetailsJson(report.getReportId()));
			JSONObject desc = new JSONObject();
			JSONObject desc1 = reportInfo.getJSONObject("desc");
			desc.put("successCount", desc1.get("successNum"));
			desc.put("failCount", (int) desc1.get("failNum") + (int) desc1.getInt("stopNum"));
			desc.put("totalCount", desc1.get("sceneNum"));
			desc.put("successPercent", desc1.get("successRate"));
			
			obj.put("status", desc1.getDouble("successRate") == 100.00 ? "SUCCESS" : "FAIL");
			
			JSONArray details = new JSONArray();
			for (Object o:reportInfo.getJSONArray("data")) {
				JSONObject detail = new JSONObject();
				detail.put("moduleName", JSONObject.fromObject(o).get("messageInfo"));
				detail.put("moduleCode", "");
				detail.put("status", "0".equals(JSONObject.fromObject(o).get("runStatus").toString()) ? "SUCCESS" : "FAIL");
				details.add(detail);
			}
			
			desc.put("details", details);
			obj.put("desc", desc);	
		}
		
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "查询任务信息成功", obj);
	}

}
