package yi.master.business.base.action;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import yi.master.annotation.util.AnnotationUtil;
import yi.master.business.base.bean.PageModel;
import yi.master.business.base.service.BaseService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

/**
 * 通用Action类，默认继承ActionSupport并实现ModelDriven接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 * @param <T>
 */

public class BaseAction<T> extends ActionSupport implements ModelDriven<T> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * LOGGER
	 */
	protected static final Logger LOGGER = Logger.getLogger(BaseAction.class.getName());
	
	/**
	 * 前置验证不通过的说明
	 */
	protected String leadValidationMark;
	
	/**
	 * BaseService
	 */
	private BaseService<T> baseService;
	
	/**
	 * ajax调用返回的map
	 */
	protected Map<String,Object> jsonMap = new HashMap<String,Object>();

	/**
	 * 传入的泛型类
	 */
	@SuppressWarnings("rawtypes")
	protected Class clazz;
	
	/**
	 * 实现ModelDriver的泛型T
	 */
	protected T model;
	
	/**
	 * 对应泛型实体的id,在进行get/del等请求时，前台传入的参数名必须为id
	 */
	protected Integer id;
		
	/**
	 * dataTable服务器处理参数<br>
	 * 请求计数<br>
	 * 保证不混淆前后请求展示的数据顺序
	 */
	protected Integer draw;
	
	/**
	 * dataTable服务器处理参数<br>
	 * 请求数据的起始位置
	 */
	protected Integer start;
	
	/**
	 * dataTable服务器处理参数<br>
	 * 本次请求的数据长度
	 */
	protected Integer length;
	
	/**
	 * 远程校验接口名是否重复<br>
	 * 需要返回的标记<br>
	 * 没有重复(验证通过) 必须返回 true或者字符串"true"<br>
	 * 验证不通过 返回提示信息
	 */
	protected String checkNameFlag;
	
	/**
	 * 自定义查询条件
	 */
	protected String[] filterCondition;
	
	/**
	 * 分页查询中的高级查询标记<br>
	 * queryMode="advanced"
	 */
	protected String queryMode;
	
	public void setBaseService(BaseService<T> baseService) {
		this.baseService = baseService;
	}
	
	/**
	 * 通用 list方法<br>
	 * 分页展示对应实体的集合
	 * @return 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String list() {
		Map<String,Object>  dt = FrameworkUtil.getDTParameters(clazz);
		String[] querys = null;
		//高级查询需要自动组装自定义的查询条件
		if ("advanced".equals(queryMode)) {
			//不能自动组装的需要在子类中覆盖customAdvancedQueryConitions方法做处理
			List<String> querysList = customAdvancedQueryConitions(AnnotationUtil.advancedQueryConditions(model));	
			if (querysList != null) {
				querys = (String[]) querysList.toArray(new String[0]);
			}
		}
		
		PageModel<T> pu = baseService.findByPager(start, length
				,(String)dt.get("orderDataName"),(String)dt.get("orderType")
				,(String)dt.get("searchValue"),(List<List<String>>)dt.get("dataParams")
				, ArrayUtils.addAll(prepareList(), querys));
		
		jsonMap.put("draw", draw);
		jsonMap.put("data", processListData(pu.getDatas()));
		jsonMap.put("recordsTotal", pu.getRecordCount());		
		jsonMap.put("recordsFiltered", pu.getFilteredCount());
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	public List<String> customAdvancedQueryConitions (List<String> conitions) {
		return conitions;
	}
	
	/**
	 * 准备自定义的查询条件
	 * @return
	 */
	public String[] prepareList() {
		return this.filterCondition;
	}
	
	/**
	 * 通用方法 listAll<br>
	 * 获取所有的对应实体集合
	 * @return 
	 */
	public String listAll() {
		List<T> ts = baseService.findAll(prepareList());
		jsonMap.put("data", processListData(ts));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	
	/**
	 * 通用del方法 <br>
	 * 根据传入的id删除对应实体
	 * @return
	 */
	public String del() {		
		baseService.delete(id);		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 通用get方法 <br>
	 * 根据id获取指定实体信息
	 * @return
	 */
	public String get() {
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("object", baseService.get(id));
		
		return SUCCESS;
	}
	
	/**
	 * 通用 edit方法<br>
	 * 编辑实体 如直接使用父类中此方法，必须 保证从前台传入的对应实体属性是完整的，否则请在子类中重写该方法
	 * @return
	 */
	public String edit() {
				
		baseService.edit(model);
		jsonMap.put("object", model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 通用 save方法<br>
	 * 保存新的实体对象,同时返回新增的实体ID的值  保证传入的属性是完整的  否则请在子类中重写该方法
	 * @return
	 */
	public String save() {
		jsonMap.put("id", baseService.save(model));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	
	/**
	 * 检查实体名重复性
	 * @return
	 */
	public String checkName() {
		checkObjectName();	
		return "check";
	}
	
	
	/**
	 * 判断实体名称重复性<br>
	 * 新增或者修改状态下均可用<br>
	 * 子类需要重写该方法
	 */
	public void checkObjectName() {
		checkNameFlag = "true";
	};
	
	
	/**
	 * list和listAll方法中对 数据的加工<br>
	 * 子类可重写该方法，对发给前台的数据进行再一次处理
	 * @param o
	 * @return
	 */
	public Object processListData(Object o) {		
		return o;
	}
	
	/**
	 * 前置验证,公用的验证可以放在此处
	 * @return
	 */
	public boolean leadValidation () {
		return true;
	}
	
	/**
	 * 通过反射动态的创建对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseAction() {
		ParameterizedType type = (ParameterizedType) this.getClass()
				.getGenericSuperclass();
		clazz = (Class) type.getActualTypeArguments()[0];
		try {
			model = (T) clazz.newInstance();
		} catch (Exception e) {
			LOGGER.error("通过反射创建BaseAction中实体对象失败!"+e.getMessage(), e);
			throw new RuntimeException(e);			
		}
	}
		
	public BaseAction setData(String key, Object value) {
		jsonMap.put(key, value);
		return this;
	}
	
	public BaseAction setReturnInfo(Integer returnCode, String returnMsg) {
		jsonMap.put("returnCode", returnCode);
		jsonMap.put("msg", returnMsg);
		return this;
	}
	
	public BaseAction setReturnInfo(Integer returnCode) {
		jsonMap.put("returnCode", returnCode);
		jsonMap.put("msg", "");
		return this;
	}
	
	public BaseAction setSuccessReturnInfo() {
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("msg", "");
		return this;
	}
	
	public BaseAction setSuccessReturnInfo(String returnMsg) {
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("msg", returnMsg);
		return this;
	}
	/************************************GET-SET***********************************************/
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	
	public void setStart(Integer start) {
		this.start = start;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@JSON(serialize=false)
	@Override
	public T getModel() {
		
		return model;
	}
	
	public Map<String, Object> getJsonMap() {
		return jsonMap;
	}

	public String getCheckNameFlag() {		
		return checkNameFlag;
	}	
	
	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}
}
