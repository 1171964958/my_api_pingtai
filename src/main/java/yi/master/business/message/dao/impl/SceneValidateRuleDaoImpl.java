package yi.master.business.message.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.SceneValidateRule;
import yi.master.business.message.dao.SceneValidateRuleDao;

@Repository("sceneValidateRuleDao")
public class SceneValidateRuleDaoImpl extends BaseDaoImpl<SceneValidateRule> implements SceneValidateRuleDao{

	@Override
	public SceneValidateRule getValidate(Integer messageSceneId, String type) {
		
		String hql = "From SceneValidateRule s where s.messageScene.messageSceneId=:messageSceneId and s.validateMethodFlag=:type";
		return (SceneValidateRule) getSession().createQuery(hql).setInteger("messageSceneId", messageSceneId).setString("type", type).uniqueResult();
	}

	@Override
	public void updateValidate(Integer validateId, String validateValue, String parameterName) {
		
		String hql = "update SceneValidateRule s set s.validateValue=:validateValue,s.parameterName=:parameterName where s.validateId=:validateId";
		getSession().createQuery(hql).setString("validateValue", validateValue).setInteger("validateId", validateId).setString("parameterName", parameterName).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SceneValidateRule> getParameterValidate(Integer messageSceneId) {
		
		String hql = "From SceneValidateRule s where s.messageScene.messageSceneId=:messageSceneId";
		return  getSession().createQuery(hql).setInteger("messageSceneId", messageSceneId).setCacheable(true).list();
	}

	@Override
	public void updateStatus(Integer validateId, String status) {
		
		String hql = "update SceneValidateRule s set s.status=:status where s.validateId=:validateId";
		getSession().createQuery(hql).setString("status", status).setInteger("validateId", validateId).executeUpdate();
	}

}
