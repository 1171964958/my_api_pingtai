package yi.master.business.log.service;

import java.sql.Timestamp;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.log.bean.LogRecord;
import yi.master.business.log.dao.LogRecordDao;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.user.bean.User;
import yi.master.constant.SystemConsts;
import yi.master.util.cache.CacheUtil;

@Service("logRecordService")
public class LogRecordServiceImpl extends BaseServiceImpl<LogRecord> implements LogRecordService {

	@SuppressWarnings("unused")
	private LogRecordDao logRecordDao;
	
	@Autowired
	public void setLogRecordDao(LogRecordDao logRecordDao) {
		super.setBaseDao(logRecordDao);
		this.logRecordDao = logRecordDao;
	}

	@Override
	public void saveRecord(User user, OperationInterface opInterface,
			String callUrl, String interceptStatus, String callType,
			String userHost, String browserAgent, int validateTime,
			int executeTime, String requestParams, String responseParams,
			String mark) {
		
		//检查日志开关并排除某些url
		if (!"0".equals(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_LOGSWITCH)) 
				|| Pattern.matches("log-.*|mail-getNoReadMailNum|.*[Ll]ist.*|.*-get.*|ptc-viewTest", callUrl)) {
			return;
		}		
		
		boolean flag = false;
		//优先判断白名单
		if (Pattern.matches(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_LOG_RECORD_WHITELIST), callUrl)) {
			flag = true;
		}		
		//判断黑名单
		if (!flag && Pattern.matches(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_LOG_RECORD_BLACKLIST), callUrl)) {
			return;
		}
		
		LogRecord record = new LogRecord();
		record.setUser(user);
		record.setOpInterface(opInterface);
		record.setCallUrl(callUrl);
		record.setCallType(callType);
		record.setInterceptStatus(interceptStatus);
		record.setUserHost(userHost);
		record.setBrowserAgent(browserAgent);
		record.setValidateTime(validateTime);
		record.setExecuteTime(executeTime);
		record.setRequestParams(requestParams);
		record.setResponseParams(responseParams);
		record.setOpTime(new Timestamp(System.currentTimeMillis()));
		record.setMark(mark);
		
		//先存入缓存，异步入库
		//logRecordDao.save(record);		
		CacheUtil.addRecord(record);
	}
}
