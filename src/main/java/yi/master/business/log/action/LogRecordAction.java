package yi.master.business.log.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.log.bean.LogRecord;
import yi.master.business.log.service.LogRecordService;

@Controller
@Scope("prototype")
public class LogRecordAction extends BaseAction<LogRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LogRecordService logRecordService;
	
	@Autowired
	public void setLogRecordService(LogRecordService logRecordService) {
		super.setBaseService(logRecordService);
		this.logRecordService = logRecordService;
	}
	
	
}
