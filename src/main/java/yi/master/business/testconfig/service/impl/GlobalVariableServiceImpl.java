package yi.master.business.testconfig.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.dao.GlobalVariableDao;
import yi.master.business.testconfig.service.GlobalVariableService;
/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20171129
 *
 */
@Service("globalVariableService")
public class GlobalVariableServiceImpl extends BaseServiceImpl<GlobalVariable> implements GlobalVariableService {
	
	private GlobalVariableDao globalVariableDao;
	
	@Autowired
	public void setGlobalVariableDao(GlobalVariableDao globalVariableDao) {
		super.setBaseDao(globalVariableDao);
		this.globalVariableDao = globalVariableDao;
	}

	@Override
	public GlobalVariable findByKey(String key) {
		
		return globalVariableDao.findByKey(key);
	}

	@Override
	public void updateValue(Integer variableId, String value) {
		
		globalVariableDao.updateValue(variableId, value);
		
	}

	@Override
	public List<GlobalVariable> findByVariableType(String variableType) {
		
		return globalVariableDao.findByVariableType(variableType);
	}
}
