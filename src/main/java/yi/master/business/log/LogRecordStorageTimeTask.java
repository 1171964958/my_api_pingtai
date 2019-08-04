package yi.master.business.log;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import yi.master.util.cache.CacheUtil;

public class LogRecordStorageTimeTask extends TimerTask {
	private static final Logger logger = Logger.getLogger(LogRecordStorageTimeTask.class);
	
	@Override
	public void run() {
		if (CacheUtil.getRecordCount() > 0) {
			logger.info("操作日志信息异步入库开始...");
			while (CacheUtil.saveRecord()) {				
				logger.info("当前操作日志对象缓存剩余数量为" + CacheUtil.getRecordCount());				
			}
			logger.info("操作日志信息异步入库完成,开始休眠中...");
		}
		
	}

}
