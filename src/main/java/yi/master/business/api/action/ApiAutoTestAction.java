package yi.master.business.api.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.api.bean.ApiReturnInfo;
import yi.master.business.api.service.task.ApiTaskService;
import yi.master.business.api.service.task.InterfaceApiTaskService;
import yi.master.business.api.service.task.WebApiTaskService;


/**
 * 外部api调用自动化测试接口
 * @author xuwangcheng
 * @version 20180407,1.0.0
 *
 */
@Controller("apiAutoTestAction")
@Scope("prototype")
public class ApiAutoTestAction extends BaseApiAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * LOGGER
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ApiAutoTestAction.class.getName());
	/**
	 * 传入执行模块ID,对于ui自动化来说是moduleId,对于接口自动化来说是setId
	 */
	private String moduleId;
	/**
	 * 执行类型，0 - UI自动化  1 - 接口自动化
	 */
	private String testType;
	/**
	 * 任务Id, 对于ui自动化来说是webScriptTask的taskId, 对于接口自动化来说是TestReport的reportId<br>
	 * 检查finishFlag 为Y时表示完成测试 ,为N时表示没有完成
	 */
	private Integer taskId;
	
	@Autowired
	private InterfaceApiTaskService interfaceApiTaskService;
	@Autowired
	private WebApiTaskService webApiTaskService;
	
	private ApiTaskService service;
	
	private String guid;
	
	/**
	 * 执行自动化测试
	 * @return
	 */
	public String autoTest() {
		if (!validateTestType()) {
			return SUCCESS;
		}		
		returnInfo = service.startTest(moduleId, guid);
		return SUCCESS;
	}
	
	/**
	 * 检查测试任务状态
	 * @return
	 */
	public String checkTask () {
		if (!validateTestType()) {
			return SUCCESS;
		}
		if (taskId != null) {
			returnInfo = service.checkTask(taskId);				
		} else {
			returnInfo = service.checkTaskByGuid(guid);
		}
			
		return SUCCESS;
	}
	
	/**
	 * 停止测试任务
	 * @return
	 */
	public String stopTest() {
		if (!validateTestType()) {
			return SUCCESS;
		}
		returnInfo = service.stopTest(taskId);		
		return SUCCESS;
	}
	
	/**
	 * 获取可执行模块
	 * @return
	 */
	public String listModule() {
		if (!validateTestType()) {
			return SUCCESS;
		}
		returnInfo = service.listModule();		
		return SUCCESS;
	}
	
	/**
	 * 验证testType参数的有效性
	 * @return
	 */
	public boolean validateTestType() {
		service = getTaskService();
		if (service == null) {
			returnInfo = new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, testType + "类型自动化测试暂时不存在!", null);
			return false;
		}
		
		return true;
		
	}
	/***********************************************************************************/
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setTestType(String testType) {
		this.testType = testType;
	}
	
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	
	private ApiTaskService getTaskService() {
		
		if ("1".equals(testType)) return interfaceApiTaskService;
		if ("0".equals(testType)) return webApiTaskService;
		return null;
	}
}
