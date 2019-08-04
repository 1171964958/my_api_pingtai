package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.business.user.bean.User;
import yi.master.constant.WebTestKeys;
import yi.master.util.LogModifyUtil;
import yi.master.util.PracticalUtils;

import net.sf.json.JSONArray;

/**
 * 测试元素对象
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestElement implements Serializable, LogModifyUtil, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(WebTestElement.class);
	
	private Integer elementId;
	/**
	 * 元素对象名称,自定义
	 */
	private String elementName;
	/**
	 * 元素类型：网站->模块->功能->页面->... (可以嵌套页面、frame) ...->元素（input/buton/span等）
	 */
	private String elementType;
	/**
	 * selenium通用的By方法
	 */
	private String byType;
	/**
	 * 传递给By方法的值<br>
	 * 除了通用的className/id/name/xpath/linkText/partialLinkText/tag<br>
	 * 还有其他比如：title/current_url/alert
	 */
	private String byValue;
	/**
	 * 取值顺序<br>
	 * 默认为0，大于0时，会使用getElements方法并按照下标获取
	 */
	private Integer seq = 0;
	/**
	 * 父节点
	 */	
	private Timestamp createTime;
	private User createUser;
	private String mark;
	/**
	 * 保存最近10次的修改日志
	 */
	private String modifyLog = "[]";
	
	@FieldNameMapper(fieldPath="modifyLog")
	private String modifyLogText;
	
	/**
	 * 下属子节点
	 */
	private Set<WebTestElement> childElements = new HashSet<WebTestElement>();
	
	/**
	 * 父节点
	 */
	private WebTestElement parentElement;
	
	@FieldNameMapper(fieldPath="parentElement.elementName")
	private String parentElementName;
	
	private String isParent;
	
	private Integer parentId;
	@FieldNameMapper(fieldPath="parentElement.elementName")
	private String elementPath;
	
	public WebTestElement(Integer elementId, String elementName, String elementType, String byType, String byValue,
			Integer seq, Timestamp createTime, User createUser, String mark,
			String modifyLog) {
		super();
		this.elementId = elementId;
		this.elementName = elementName;
		this.elementType = elementType;
		this.byType = byType;
		this.byValue = byValue;
		this.seq = seq;
		this.createTime = createTime;
		this.createUser = createUser;
		this.mark = mark;
		this.modifyLog = modifyLog;
	}

	public WebTestElement() {
		super();
		
	}

	
	public void setParentElementName(String parentElementName) {
		this.parentElementName = parentElementName;
	}
	
	public String getParentElementName() {
		if (parentElement != null) {
			return parentElement.getElementName();
		}
		return parentElementName;
	}
	
	public String getElementPath() {
		if (elementName != null) {
			return parseElementPath(new StringBuilder(elementName)).toString();
		}
		return elementPath;
	}
	
	public StringBuilder parseElementPath(StringBuilder path) {
		if (parentElement != null) {
			path.insert(0, parentElement.elementName + " <span class=\"c-gray en\">&gt;</span> ");
			if (parentElement.parentElement != null) {
				parentElement.parseElementPath(path);
			}
		}
		return path;
	}
	
	public String getIsParent() {
		if (StringUtils.isBlank(elementType)) return null;
		if (elementType.matches(WebTestKeys.WEB_ELEMENT_TYPE_URL + "|" + WebTestKeys.WEB_ELEMENT_TYPE_TAG)) {
			return "false";
		}
		return "true";
	}
	
	public String getModifyLogText() {
		JSONArray arr = JSONArray.fromObject(getModifyLog());
		if (!arr.isEmpty()) {
			return StringUtils.join(arr.toArray(), "\n\n");
		}
		return "";
	}
	
	public Integer getParentId() {
		if (parentElement != null) {
			return parentElement.getElementId();
		}
		return parentId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public void setParentElement(WebTestElement parentElement) {
		this.parentElement = parentElement;
	}
	
	@JSON(serialize=false)
	public WebTestElement getParentElement() {
		return parentElement;
	}
	
	public Integer getElementId() {
		return elementId;
	}

	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getByType() {
		return byType;
	}

	public void setByType(String byType) {
		this.byType = byType;
	}

	public String getByValue() {
		return byValue;
	}

	public void setByValue(String byValue) {
		this.byValue = byValue;
	}

	public Integer getSeq() {
		if (seq == null) {
			seq = 0;
		}
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getModifyLog() {
		if (StringUtils.isBlank(modifyLog)) {
			modifyLog = "[]";
		}
		return modifyLog;
	}

	public void setModifyLog(String modifyLog) {
		this.modifyLog = modifyLog;
	}

	public void setChildElements(Set<WebTestElement> childElements) {
		this.childElements = childElements;
	}
	
	@JSON(serialize=false)
	public Set<WebTestElement> getChildElements() {
		return childElements;
	}
	
	@Override
	public String toString() {
		return "WebTestElement [elementId=" + elementId + ", elementName=" + elementName + ", elementType="
				+ elementType + ", byType=" + byType + ", byValue=" + byValue + ", seq=" + seq + ", createTime="
				+ createTime + ", mark=" + mark + ", modifyLog=" + modifyLog + "]";
	}
	
	@Override
	public Object clone() {
		
		Object o = null;
		try {
			o = super.clone();
			//如果是frame则不会复制子节点
			if (childElements.size() > 0) {
				((WebTestElement) o).setChildElements(new HashSet<WebTestElement>());
			}
		} catch (Exception e) {
			
			LOGGER.warn("clone exception", e);
		}
		return o;
	}

	@Override
	public void logModify(String userName, Object oldObject) {
		
		if (oldObject == null || !(oldObject instanceof WebTestElement)) {
			return;
		}		
		WebTestElement oldElement = (WebTestElement) oldObject;
				
		StringBuilder log = new StringBuilder();
		log.append("用户" + userName + "在" + PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", new Date()) + "修改了以下内容:\n");
		boolean modifyFlag = false;
		//比较elementName、byType、byValue、seq、mark
		if (!StringUtils.equals(this.elementName, oldElement.getElementName())) {
			modifyFlag = true;
			log.append("元素名称: [" + oldElement.getElementName() + "] ==> [" + this.elementName + "]\n");
		}
		if (!(StringUtils.isBlank(this.byType) && StringUtils.isBlank(oldElement.getByType())) 
				&& !StringUtils.equals(this.byType, oldElement.getByType())) {
			modifyFlag = true;
			log.append("定位方式: [" + oldElement.getByType() + "] ==> [" + this.byType + "]\n");
		}
		if (!(StringUtils.isBlank(this.byValue) && StringUtils.isBlank(oldElement.getByValue())) 
				&& !StringUtils.equals(this.byValue, oldElement.getByValue())) {
			modifyFlag = true;
			log.append("定位值: [" + oldElement.getByValue() + "] ==> [" + this.byValue + "]\n");
		}
		if (!oldElement.getSeq().equals(this.seq)) {
			modifyFlag = true;
			log.append("取值顺序: [" + oldElement.getSeq() + "] ==> [" + this.seq + "]\n");
		}
		if (!(StringUtils.isBlank(this.mark) && StringUtils.isBlank(oldElement.getMark())) 
				&& !StringUtils.equals(this.mark, oldElement.getMark())) {
			modifyFlag = true;
			log.append("备注信息: [" + oldElement.getMark() + "] ==> [" + this.mark + "]\n");
		}
		
		if (modifyFlag) {
			JSONArray modifyLogArr = JSONArray.fromObject(this.getModifyLog());
			if (modifyLogArr.size() >= 10) {
				modifyLogArr.remove(0);
			}
			modifyLogArr.add(log.toString());
			this.modifyLog = modifyLogArr.toString();
		}		
	}
}
