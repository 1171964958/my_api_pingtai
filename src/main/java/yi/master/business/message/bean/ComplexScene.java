package yi.master.business.message.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldRealSearch;
import yi.master.business.message.service.ComplexSceneService;
import yi.master.business.message.service.MessageSceneService;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;

/**
 * 测试集中的组合场景
 * @author xuwangcheng
 * @version 2017.11.23,1.0.0.0
 *
 */
public class ComplexScene  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(ComplexScene.class);
	
	private Integer id;
	
	/**
	 * 组合场景组成的新场景名称
	 */
	private String complexSceneName;
	
	private String configInfo = "{}";
	
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 备注
	 */
	private String mark;
	
	/**
	 * 组合场景的成功条件标记<br>
	 * 0 - 要求所有场景必须测试成功
	 * 2 - 单独统计结果(即在测试报告中，所有测试详情结果不会出现组合场景中)
	 */
	private String successFlag;
	
	/**
	 * 是否采用新的 httpclient客户端来测试此组合场景<br>
	 * 0 - 采用独立的httpclient客户端
	 * 1 - 使用共享客户端来测试
	 */
	@FieldRealSearch(names = {"是", "否"}, values = {"0", "1"})
	private String newClient;
	
	/**
	 * 对应测试集
	 */
	private Set<TestSet> testSets = new HashSet<TestSet>();
	
	private Integer sceneNum;
	
	/**
	 * 该MAP保存着该组合场景包含的测试场景和相关配置<br>
	 * map的key值为测试场景的执行顺序
	 */
	private Map<String, ComplexSceneConfig> complexSceneConfigs;
	
	/**
	 * 包含的测试场景
	 */
	private List<MessageScene> scenes = new ArrayList<MessageScene>();
	
	public ComplexScene() {
		super();	
	}

	public Integer getSceneNum() {
		setComplexSceneConfigs();
		if (complexSceneConfigs != null) {
			return complexSceneConfigs.size();
		}
		return 0;
	}
	
	/**
	 * 将configInfo配置JSON串转换为实体对象MAP
	 */
	public void setComplexSceneConfigs() {		
		checkConfigInfoValidity();
		if (StringUtils.isNotBlank(configInfo)) {			
			try {
				complexSceneConfigs = PracticalUtils.getComplexSceneConfigs(configInfo);
			} catch (Exception e) {
				
				LOGGER.error("无法转换该组合场景的配置信息:" + configInfo, e);
			}			
		} else {
			complexSceneConfigs = new HashMap<String, ComplexSceneConfig>();
		}
	}
	
	/**
	 * 添加一个测试场景到该组合场景
	 * @param messageSceneId
	 * @return 返回更新后的configJson配置
	 */
	public String addScene (Integer messageSceneId) {
		int key = complexSceneConfigs.size() + 1; 
		complexSceneConfigs.put(String.valueOf(key), new ComplexSceneConfig(messageSceneId));
		setConfigInfo(JSONObject.fromObject(complexSceneConfigs).toString());
		return this.configInfo;
	}
	
	/**
	 * 删除一个测试场景
	 * @param sequenceNum
	 * @return 返回更新后的configJson配置
	 */
	public String delScene (Integer sequenceNum) {		
		if (complexSceneConfigs.containsKey(String.valueOf(sequenceNum))) {
			complexSceneConfigs.remove(String.valueOf(sequenceNum));
			String removeKey = null;
			
			List<String> keys = new ArrayList<String>(complexSceneConfigs.keySet());
			Collections.sort(keys);
			//重新排序
			for (String key:keys) {
				if (Integer.parseInt(key) > sequenceNum) {
					complexSceneConfigs.put(String.valueOf((Integer.parseInt(key) - 1)), complexSceneConfigs.get(key));
					removeKey = key;
				}
			}
			if (removeKey != null) {
				complexSceneConfigs.remove(removeKey);
			}
			setConfigInfo(JSONObject.fromObject(complexSceneConfigs).toString());
		}
		
		return this.configInfo;
	}
	
	/**
	 * 检查组合场景配置的有效性,防止引用了已被删除的测试场景
	 */
	private void checkConfigInfoValidity () {
		boolean updateFlag = false;
		Map<String, ComplexSceneConfig> configs = PracticalUtils.getComplexSceneConfigs(this.configInfo);
		List<String> keys = new ArrayList<String>(configs.keySet());
		Collections.sort(keys);
		
		MessageSceneService messageSceneService = (MessageSceneService) FrameworkUtil.getSpringBean("messageSceneService");
		for (String key:configs.keySet()) {
			if (messageSceneService.get(configs.get(key).getMessageSceneId()) == null) {//该测试场景已被删除
				keys.remove(key);
				updateFlag = true;
			}
		}
		
		if (updateFlag) {//重新排序并更新配置
			Map<String, ComplexSceneConfig> updateComplexSceneConfigs = new HashMap<String, ComplexSceneConfig>();
			int count = 1;
			for (String key:keys) {
				updateComplexSceneConfigs.put(String.valueOf(count++), configs.get(key));
			}
			ComplexSceneService complexSceneService = (ComplexSceneService)FrameworkUtil.getSpringBean("complexSceneService");
			this.setConfigInfo(JSONObject.fromObject(updateComplexSceneConfigs).toString());
			complexSceneService.updateConfigInfo(this.id, this.configInfo);
		}
		
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MessageScene> setScenes(MessageSceneService service) {
		
		Map<String, ComplexSceneConfig> configs = PracticalUtils.getComplexSceneConfigs(this.configInfo);

		if (configs != null && configs.size() != 0) {
			for (Iterator<String> t = configs.keySet().iterator();t.hasNext();) {
				String key = t.next();
				MessageScene sceneC = service.get(configs.get(key).getMessageSceneId());
				MessageScene scene = (MessageScene) sceneC.clone();
				
				scene.setSequenceNum(Integer.valueOf(key));
				scene.setConfig(configs.get(key));
				scenes.add(scene);
			}
		}
		//按照执行顺序排序
		Collections.sort(scenes, new Comparator() {
			public int compare(Object a, Object b) {
				Integer aId = ((MessageScene) a).getSequenceNum();
				Integer bId = ((MessageScene) b).getSequenceNum();
				return aId - bId;
			}
		});
		return scenes;
	}
	
	public void setNewClient(String newClient) {
		this.newClient = newClient;
	}
	
	public String getNewClient() {
		return newClient;
	}
	
	@JSON(serialize=false)
	public List<MessageScene> getScenes() {
		return scenes;
	}
	
	public void setSuccessFlag(String successFlag) {
		this.successFlag = successFlag;
	}
	
	public String getSuccessFlag() {
		return successFlag;
	}
	
	@JSON(serialize=false)
	public Map<String, ComplexSceneConfig> getComplexSceneConfigs() {
		return complexSceneConfigs;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setConfigInfo(String configInfo) {
		this.configInfo = configInfo;
	}
	
	public String getConfigInfo() {
		return configInfo;
	}	
	
	public String getComplexSceneName() {
		return complexSceneName;
	}

	public void setComplexSceneName(String complexSceneName) {
		this.complexSceneName = complexSceneName;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
	@JSON(serialize=false)
	public Set<TestSet> getTestSets() {
		return testSets;
	}
	
	public void setTestSets(Set<TestSet> testSets) {
		this.testSets = testSets;
	}

	@Override
	public String toString() {
		return "ComplexScene [id=" + id + ", complexSceneName="
				+ complexSceneName + ", configInfo=" + configInfo
				+ ", createTime=" + createTime + ", mark=" + mark
				+ ", successFlag=" + successFlag + ", testSets=" + testSets
				+ ", sceneNum=" + sceneNum + ", complexSceneConfigs="
				+ complexSceneConfigs + ", scenes=" + scenes + "]";
	}
	
	
	
}
