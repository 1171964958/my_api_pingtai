package yi.master.util.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;

import yi.master.business.log.bean.LogRecord;
import yi.master.business.log.service.LogRecordService;
import yi.master.business.system.bean.GlobalSetting;
import yi.master.business.testconfig.bean.DataDB;
import yi.master.coretest.message.test.mock.MockSocketServer;
import yi.master.coretest.message.test.performance.PerformanceTestObject;
import yi.master.util.FrameworkUtil;

/**
 * web相关的一些配置数据以及全局数据的缓存设置<br>
 * 目前暂时只能缓存在内存中
 * @author xuwangcheng
 * @version 2017.11.17,1.0.0
 *
 */
public class CacheUtil {
	
	/**
	 * 全局设置
	 */
	private static Map<String,GlobalSetting> settingMap;

	/**
	 * 被预占的测试数据
	 */
	private static Set<Integer> lockedTestDatas = new HashSet<Integer>();
	
	/**
	 * 数据源
	 */
	private static Map<String, DataDB> queryDBMap;
	
	/**
	 * 放入缓存中的操作日志信息<br>
	 * linkedList是线程不安全的，多线程下可能导致异常情况，参考<a href="http://yizhilong28.iteye.com/blog/717158">LinkedList多线程不安全的解决办法</a>
	 */
	private static Queue<LogRecord> records = new ConcurrentLinkedQueue<LogRecord>();
	
	/**
	 * 性能测试对象
	 */
	private static Map<Integer, Map<Integer, PerformanceTestObject>> ptObjects = new HashMap<Integer, Map<Integer, PerformanceTestObject>>();
	
	/**
	 * socket类型mock服务，key值为对应的mock对象的mockId
	 */
	private static Map<Integer, MockSocketServer> socketServers = new HashMap<Integer, MockSocketServer>();
	
	
	public static void setQueryDBMap(Map<String, DataDB> queryDBMap) {
		CacheUtil.queryDBMap = queryDBMap;
	}

	public static void addLockedTestData(Integer dataId) {
		CacheUtil.lockedTestDatas.add(dataId);
	}
	
	public static void removeLockedTestData(Integer dataId) {
		CacheUtil.lockedTestDatas.remove(dataId);
	}
	
	public static boolean checkLockedTestData(Integer dataId) {
		return CacheUtil.lockedTestDatas.contains(dataId);
	}
	
	/**
	 * 获取查询数据库信息列表
	 * @return
	 */
	public static Map<String, DataDB> getQueryDBMap() {
		return queryDBMap;
	}
	
	/**
	 * 根据dbId获取指定的查询数据库信息
	 * @param dbId
	 * @return
	 */
	public static DataDB getQueryDBById (String dbId) {
		return queryDBMap.get(dbId);
	}
	/**
	 * 有新增、删除、修改时更新此MAP
	 */
	public static void updateQueryDBMap(DataDB db, Integer id) {
		if (db == null) {
			//删除
			queryDBMap.remove(String.valueOf(id));			
			return;
		}
		
		//更新或者新增
		queryDBMap.put(String.valueOf(db.getDbId()), db);
	}
	
	/**
	 * 获取全局设置项
	 * @return
	 */
	public static Map<String,GlobalSetting> getGlobalSettingMap () {
		return settingMap;
	}
	
	public static void setSettingMap(Map<String, GlobalSetting> settingMap) {
		CacheUtil.settingMap = settingMap;
	}
	
	/**
	 * 获取指定名称的全局设置项
	 * @param settingName
	 * @return
	 */
	public static String getSettingValue (String settingName) {
		GlobalSetting setting = settingMap.get(settingName);
		if (setting != null) {
			return  StringUtils.isEmpty(setting.getSettingValue()) ? setting.getDefaultValue() : setting.getSettingValue();
		}
		return "";
	}
	
	/**
	 * 更新内存中的指定全局设置项的值
	 * @param settingName
	 * @param settingValue
	 */
	public static void updateGlobalSettingValue(String settingName, String settingValue) {
		for (GlobalSetting setting:settingMap.values()) {
			if (setting.getSettingName().equals(settingName)) {
				setting.setSettingValue(settingValue);
			}
		}
	}
	
	public static boolean saveRecord() {
		LogRecord record = 	CacheUtil.records.poll();
		if (record != null) {
			LogRecordService service = (LogRecordService) FrameworkUtil.getSpringBean("logRecordService");
			service.save(record);
			return true;
		}
		return false;
	}
	
	public static void addRecord(LogRecord record) {
		if (record != null) {
			CacheUtil.records.offer(record);
		}		
	}
	
	public static int getRecordCount() {
		return CacheUtil.records.size();
	}
	
	public static void setPtObjects(
			Map<Integer, Map<Integer, PerformanceTestObject>> ptObjects) {
		CacheUtil.ptObjects = ptObjects;
	}
	
	public static Map<Integer, Map<Integer, PerformanceTestObject>> getPtObjects() {
		return ptObjects;
	}
	
	public static Map<Integer, PerformanceTestObject> getPtObjectsByUserId (Integer userId) {
		Map<Integer, PerformanceTestObject> ptos = ptObjects.get(userId);
		if (ptos == null) {
			ptos = new HashMap<Integer, PerformanceTestObject>();
			ptObjects.put(userId, ptos);
		}
		return ptos;
	}
	
	public static void addPtObject (Integer userId, PerformanceTestObject ptObject) {
		Map<Integer, PerformanceTestObject> pts = getPtObjectsByUserId(userId);
		pts.put(ptObject.getObjectId(), ptObject);
	}
	
	public static void setSocketServers(
			Map<Integer, MockSocketServer> socketServers) {
		CacheUtil.socketServers = socketServers;
	}
	
	public static Map<Integer, MockSocketServer> getSocketServers() {
		return socketServers;
	}
}
