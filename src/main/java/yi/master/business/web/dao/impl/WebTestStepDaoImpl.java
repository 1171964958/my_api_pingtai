package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestStep;
import yi.master.business.web.dao.WebTestStepDao;

@Repository("webTestStepDao")
public class WebTestStepDaoImpl extends BaseDaoImpl<WebTestStep> implements WebTestStepDao {

}
