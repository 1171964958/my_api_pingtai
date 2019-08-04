package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestConfig;
import yi.master.business.web.dao.WebTestConfigDao;

@Repository("webTestConfigDao")
public class WebTestConfigDaoImpl extends BaseDaoImpl<WebTestConfig> implements WebTestConfigDao {

}
