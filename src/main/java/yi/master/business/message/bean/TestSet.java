package yi.master.business.message.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.user.bean.User;

/**
 * 测试集实体类
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170518
 *
 */

public class TestSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer setId;
	/**
	 * 测试集名称
	 */
	private String setName;
	/**
	 * 创建用户
	 */
	private User user;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 可用状态<br>
	 * 0-可用   1-不可用<br>
	 * 在执行定时任务测试时,将会忽略状态为1的测试集
	 */
	@FieldRealSearch(names = {"正常", "禁用"}, values = {"0", "1"})
	private String status;
	/**
	 * 备注
	 */
	private String mark;
	
	/**
	 * 该测试集的测试配置
	 */
	private TestConfig config;
	
	/**
	 * 是否为父节点<br>父节点就是测试集菜单而不是测试集
	 * <br>1 - 表示测试集  0 - 表示文件夹目录
	 */
	private String parented;
	
	/**
	 * 父节点
	 */
	private TestSet parentSet;;
	
	/**
	 * 子节点或者下属测试集
	 */
	private Set<TestSet> childrenSets = new HashSet<TestSet>();
	/**
	 * 包含场景数量
	 */
	@FieldNameMapper(fieldPath="size(ms)",ifSearch=false)
	private Integer sceneNum;
	
	private Set<ComplexScene> complexMs = new HashSet<ComplexScene>();
	
	private Set<MessageScene> ms = new HashSet<MessageScene>();
	
	/**
	 * 组合场景数量
	 */
	@FieldNameMapper(fieldPath="size(complexMs)",ifSearch=false)
	private Integer complexSceneNum;
	
	private Integer parentId;

	public TestSet(Integer setId, String setName, User user,
			Timestamp createTime, String status, String mark, TestConfig config) {
		super();
		this.setId = setId;
		this.setName = setName;
		this.user = user;
		this.createTime = createTime;
		this.status = status;
		this.mark = mark;
		this.config = config;
	}

	public TestSet (Integer setId) {
		this.setId = setId;
	}
	
	public TestSet() {
	}
	
	/**
	 * 获取节点数据
	 * @return
	 */
	public Map<String, Object> getNodesMap (TestSetService service) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", setId);
		map.put("name", setName);
		map.put("spread", false);
		map.put("parented", "0".equals(parented) ? true : false);
		map.put("invalid", "1".equals(status) ? true : false);
		if (service.totalCount("parentSet.setId=" + setId) > 0) {
			map.put("spread", true);
			List<Map<String, Object>> childrens = new ArrayList<Map<String, Object>>();
			for (TestSet set:service.findAll("parentSet.setId=" + setId)) {
				childrens.add(set.getNodesMap(service));
			}
			map.put("children", childrens);
		}
		return map;
		
	}
	
	public Integer getParentId() {
		if (this.getParentSet() != null) {
			return this.parentSet.getSetId();
		}
		return 0;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public void setParentSet(TestSet parentSet) {
		this.parentSet = parentSet;
	}
	
	@JSON(serialize=false)
	public TestSet getParentSet() {
		return parentSet;
	}
	
	public void setChildrenSets(Set<TestSet> childrenSets) {
		this.childrenSets = childrenSets;
	}
	
	@JSON(serialize=false)
	public Set<TestSet> getChildrenSets() {
		return childrenSets;
	}
	
	public String getParented() {
		return parented;
	}
	
	public void setParented(String parented) {
		this.parented = parented;
	}

	public TestConfig getConfig() {
		return config;
	}
	
	public void setConfig(TestConfig config) {
		this.config = config;
	}
	
	public Integer getSceneNum() {
		return this.ms.size();
	}

	public void setSceneNum(Integer sceneNum) {
		this.sceneNum = sceneNum;
	}
	
	public Integer getComplexSceneNum() {
		return this.complexMs.size();
	}
	
	public void setComplexSceneNum(Integer complexSceneNum) {
		this.complexSceneNum = complexSceneNum;
	}
	
	public Integer getSetId() {
		return setId;
	}

	public void setSetId(Integer setId) {
		this.setId = setId;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@JSON(serialize=false)
	public Set<MessageScene> getMs() {
		return ms;
	}

	public void setMs(Set<MessageScene> ms) {
		this.ms = ms;
	}
	
	public void setComplexMs(Set<ComplexScene> complexMs) {
		this.complexMs = complexMs;
	}
	
	@JSON(serialize=false)
	public Set<ComplexScene> getComplexMs() {
		return complexMs;
	}

	@Override
	public String toString() {
		return "TestSet [setId=" + setId + ", setName=" + setName
				+ ", createTime=" + createTime + ", status=" + status
				+ ", mark=" + mark + ", config=" + config + ", parented="
				+ parented + ", sceneNum=" + sceneNum + ", complexSceneNum="
				+ complexSceneNum + ", parentId=" + parentId + "]";
	}
	
	
}
