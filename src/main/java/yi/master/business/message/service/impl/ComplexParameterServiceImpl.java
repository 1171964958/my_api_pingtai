package yi.master.business.message.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.dao.ComplexParameterDao;
import yi.master.business.message.service.ComplexParameterService;

@Service("complexParameterService")
public class ComplexParameterServiceImpl extends BaseServiceImpl<ComplexParameter> implements ComplexParameterService {
	
	@SuppressWarnings("unused")
	private ComplexParameterDao complexParameterDao;
	
	@Autowired
	public void setComplexParameterDao(ComplexParameterDao complexParameterDao) {
		super.setBaseDao(complexParameterDao);
		this.complexParameterDao = complexParameterDao;
	}
}
