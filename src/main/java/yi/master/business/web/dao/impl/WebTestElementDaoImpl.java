package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestElement;
import yi.master.business.web.dao.WebTestElementDao;

@Repository("webTestElementDao")
public class WebTestElementDaoImpl extends BaseDaoImpl<WebTestElement> implements WebTestElementDao {

}
