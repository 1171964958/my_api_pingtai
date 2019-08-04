package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.dao.ParameterDao;
import yi.master.business.message.service.ParameterService;

/**
 * 接口参数Service接口实现
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 * 
 */

@Service("parameterService")
public class ParameterServiceImpl extends BaseServiceImpl<Parameter> implements ParameterService {

	private ParameterDao parameterDao;
	
	@Autowired
	public void setParameterDao(ParameterDao parameterDao) {
		super.setBaseDao(parameterDao);
		this.parameterDao = parameterDao;
	}
	
	
	@Override
	public List<Parameter> findByInterfaceId(int interfaceId) {
		
		return parameterDao.findByInterfaceId(interfaceId);
	}

	@Override
	public void editProperty(int parameterId, String attrName, String attrValue) {
		
		parameterDao.editProperty(parameterId, attrName, attrValue);
	}


	@Override
	public void delByInterfaceId(int interfaceId) {
		
		List<Parameter> params = findByInterfaceId(interfaceId);
		
		for (Parameter p:params) {
			delete(p.getParameterId());
		}
	}


	@Override
	public Parameter checkRepeatParameter(Integer parameterId,
			String parameterIdentify, String path, String type,
			Integer interfaceId) {
		
		return parameterDao.checkRepeatParameter(parameterId, parameterIdentify, path, type, interfaceId);
	}

}
