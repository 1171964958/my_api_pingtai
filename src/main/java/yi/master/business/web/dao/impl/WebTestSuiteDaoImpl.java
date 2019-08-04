package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestSuite;
import yi.master.business.web.dao.WebTestSuiteDao;

@Repository("webTestSuiteDao")
public class WebTestSuiteDaoImpl extends BaseDaoImpl<WebTestSuite> implements WebTestSuiteDao {

}
