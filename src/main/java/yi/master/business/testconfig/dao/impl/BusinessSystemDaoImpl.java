package yi.master.business.testconfig.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.dao.BusinessSystemDao;

@Repository("businessSystemDao")
public class BusinessSystemDaoImpl extends BaseDaoImpl<BusinessSystem> implements BusinessSystemDao {

	@SuppressWarnings("unchecked")
	@Override
	public PageModel<InterfaceInfo> listSystemInterface(Integer systemId,
			int dataNo, int pageSize, String orderDataName, String orderType,
			String searchValue, List<List<String>> dataParams, int mode, String procotolType,
			String... filterCondition) {
		
		PageModel<InterfaceInfo> pm = new PageModel<InterfaceInfo>(orderDataName, orderType, searchValue, dataParams, dataNo, pageSize);
		StringBuilder hql = new StringBuilder("from InterfaceInfo m1 where");
		//mode=0 查询存在的 mode=1查询不存在的
		hql.append(mode == 1 ? " not" : "");
		
		hql.append(" exists (select 1 from InterfaceInfo m2 join m2.systems s "
				+ "where s.systemId=" + systemId + " and m1.interfaceId=m2.interfaceId) and m1.interfaceProtocol='" + procotolType + "'");
		LOGGER.info("The query HQL String: \n" + hql.toString());		
		pm.setRecordCount(getHqlCount("select count(m1) " + hql.toString()));

		
		//增加搜索条件
		if (StringUtils.isNotBlank(searchValue)) {
			hql.append(" and (");
			int i = 1;
			for (List<String> ss : dataParams) {
				i++;
				String columnName = ss.get(0);
				
				if (ss.size() == 1) {
					hql.append(columnName + " like '%" + searchValue + "%'");
				}
				
				if (ss.size() > 1) {
					for (int m = 1;m < ss.size();m ++) {
						hql.append(columnName + " like '%" + ss.get(m) + "%'");
						if (m + 1 < ss.size()) {
							hql.append(" or ");
						}
					}
				}
												
				if (i <= dataParams.size()) {
					hql.append(" or ");
				}
			}
			hql.append(")");
		}
		
		//增加自定义的条件
		if (filterCondition != null && filterCondition.length > 0) {
			hql.append(" and ");
						
			int i = 1;
			for (String s : filterCondition) {
				hql.append(s);
				i++;
				if (i <= filterCondition.length) {
					hql.append(" and ");
				}
			}
		}
		
		LOGGER.info("The query HQL String: \n" + hql.toString());
		pm.setFilteredCount(getHqlCount("select count(m1) " + hql.toString()));
		
		//增加排序
		if (!orderDataName.isEmpty()) {
			hql.append(" order by " + orderDataName + " " + orderType);
		}
		
		LOGGER.info("The query HQL String: \n" + hql.toString());
		
		pm.setDatas(getSession().createQuery(hql.toString())
				.setFirstResult(dataNo)
				.setMaxResults(pageSize)
				.setCacheable(true).list());
		
		return pm;
	}

	@Override
	public void addInterfaceToSystem(Integer systemId, Integer interfaceId) {
		
		String sql = "insert into at_interface_info_business_system (system_id, interface_id) VALUES (:systemId, :interfaceId)";
		getSession().createSQLQuery(sql).setInteger("systemId", systemId).setInteger("interfaceId", interfaceId).executeUpdate();
		
	}

	@Override
	public void delInterfaceFromSystem(Integer systemId, Integer interfaceId) {
		
		String sql = "delete from at_interface_info_business_system where system_id=:systemId and interface_id=:interfaceId";
		getSession().createSQLQuery(sql).setInteger("systemId", systemId).setInteger("interfaceId", interfaceId).executeUpdate();
	}
}
