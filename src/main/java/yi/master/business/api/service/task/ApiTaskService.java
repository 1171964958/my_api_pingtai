package yi.master.business.api.service.task;

import yi.master.business.api.bean.ApiReturnInfo;

public interface ApiTaskService {
	/**
	 * 外部通过接口执行测试
	 * @param moduleId  模块ID 根据测试类型不同,含义不同
	 * @param guid 外部传入的guid,如果传入了则需要同步执行，不立即返回，如果没有传入则异步执行并立即返回一些信息
	 * @return
	 */
	ApiReturnInfo startTest(String moduleId, String guid);
	/**
	 * 检查指定测试任务的执行状态
	 * @param taskId 异步执行时返回的任务id
	 * @return
	 */
	ApiReturnInfo checkTask(Integer taskId);
	
	/**
	 * 根据外部传入的guid标识来查询指定任务的执行情况
	 * @param guid
	 * @return
	 */
	ApiReturnInfo checkTaskByGuid(String guid);
	/**
	 * 停止指定测试任务
	 * @param taskId
	 * @return
	 */
	ApiReturnInfo stopTest(Integer taskId);
	/**
	 * 获取所有可执行模块
	 * @return
	 */
	ApiReturnInfo listModule();
}
