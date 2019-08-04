package yi.master.business.log.dao;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.log.bean.LogRecord;

@Repository("logRecordDao")
public class LogRecordDaoImpl extends BaseDaoImpl<LogRecord> implements LogRecordDao {
}
