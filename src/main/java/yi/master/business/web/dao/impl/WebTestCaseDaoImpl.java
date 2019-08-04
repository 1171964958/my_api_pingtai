package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestCase;
import yi.master.business.web.dao.WebTestCaseDao;

@Repository("webTestCaseDao")
public class WebTestCaseDaoImpl extends BaseDaoImpl<WebTestCase> implements WebTestCaseDao {

}
