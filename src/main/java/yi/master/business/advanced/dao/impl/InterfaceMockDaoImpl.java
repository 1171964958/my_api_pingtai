package yi.master.business.advanced.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.advanced.bean.InterfaceMock;
import yi.master.business.advanced.dao.InterfaceMockDao;
import yi.master.business.base.dao.impl.BaseDaoImpl;

@Repository("interfaceMockDao")
public class InterfaceMockDaoImpl extends BaseDaoImpl<InterfaceMock> implements InterfaceMockDao {

	@Override
	public InterfaceMock findByMockUrl(String mockUrl) {
		
		String hql = "From InterfaceMock m where m.mockUrl=:mockUrl";		
		return (InterfaceMock) getSession().createQuery(hql).setString("mockUrl", mockUrl).uniqueResult();
	}

	@Override
	public void updateStatus(Integer mockId, String status) {
		
		String hql = "update InterfaceMock m set m.status=:status where m.mockId=:mockId";
		getSession().createQuery(hql).setInteger("mockId", mockId).setString("status", status).executeUpdate();
	}

	@Override
	public void updateSetting(Integer mockId, String settingType,
			String configJson) {
		
		String hql = "update InterfaceMock m set m." + settingType + "=:configJson where m.mockId=:mockId";
		getSession().createQuery(hql).setInteger("mockId", mockId).setString("configJson", configJson).executeUpdate();
	}

	@Override
	public List<InterfaceMock> getEnableSocketMock() {
		
		String hql = "From InterfaceMock m where m.protocolType='Socket' and m.status='0'";
		return getSession().createQuery(hql).list();
	}

}
