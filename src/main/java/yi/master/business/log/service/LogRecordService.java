package yi.master.business.log.service;

import yi.master.business.base.service.BaseService;
import yi.master.business.log.bean.LogRecord;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.user.bean.User;

public interface LogRecordService extends BaseService<LogRecord> {
	
	/**
	 * 保存记录信息
	 * @param user
	 * @param opInterface
	 * @param callUrl
	 * @param interceptStatus
	 * @param callType
	 * @param userHost
	 * @param browserAgent
	 * @param validateTime
	 * @param executeTime
	 * @param requestParams
	 * @param responseParams
	 * @param mark
	 */
	void saveRecord(User user, OperationInterface opInterface, String callUrl, String interceptStatus, String callType,
			String userHost, String browserAgent, int validateTime, int executeTime, String requestParams, String responseParams,
			String mark);

}
