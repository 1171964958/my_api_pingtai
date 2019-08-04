package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.SceneValidateRule;
import yi.master.business.message.dao.SceneValidateRuleDao;
import yi.master.business.message.service.SceneValidateRuleService;

@Service("sceneValidateRuleService")
public class SceneValidateRuleServiceImpl extends BaseServiceImpl<SceneValidateRule> implements SceneValidateRuleService {
	
	private SceneValidateRuleDao sceneValidateRuleDao;
	
	@Autowired
	public void setSceneValidateRuleDao(
			SceneValidateRuleDao sceneValidateRuleDao) {
		super.setBaseDao(sceneValidateRuleDao);
		this.sceneValidateRuleDao = sceneValidateRuleDao;
	}

	@Override
	public SceneValidateRule getValidate(Integer messageSceneId, String type) {
		
		return sceneValidateRuleDao.getValidate(messageSceneId, type);
	}

	@Override
	public void updateValidate(Integer validateId, String validateValue, String parameterName) {
		
		sceneValidateRuleDao.updateValidate(validateId, validateValue, parameterName);
	}

	@Override
	public List<SceneValidateRule> getParameterValidate(Integer messageSceneId) {
		
		return sceneValidateRuleDao.getParameterValidate(messageSceneId);
	}

	@Override
	public void updateStatus(Integer validateId, String status) {
		
		sceneValidateRuleDao.updateStatus(validateId, status);
	}

}
