package yi.master.business.localize.shanxi.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

/**
 * ruby-web自动化脚本模块定义
 * @author xuwangcheng
 * @version 1.0.0.0,2018.3.31
 *
 */
public class WebScriptModule implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer moduleId;
	/**
	 * 模块名称
	 */
	private String moduleName;
	/**
	 * 文件夹名称
	 */
	private String folderName;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 创建用户
	 */
	private String createUser;
	/**
	 * 脚本作者
	 */
	private String author;
	/**
	 * 备注
	 */
	private String mark;	
	/**
	 * 模块业务code
	 */
	private String moduleCode;
	
	/**
	 * 框架类型：目前支持的ruby-watir-cucumber
	 */
	private String frameworkType;

	private Set<WebScriptTask> tasks = new HashSet<WebScriptTask>();

	public WebScriptModule(Integer moduleId, String moduleName,
			String folderName, Timestamp createTime, String createUser,
			String author, String mark, String moduleCode) {
		super();
		this.moduleId = moduleId;
		this.moduleName = moduleName;
		this.folderName = folderName;
		this.createTime = createTime;
		this.createUser = createUser;
		this.author = author;
		this.mark = mark;
		this.moduleCode = moduleCode;
	}

	public WebScriptModule() {
		super();
		
	}

	public void setTasks(Set<WebScriptTask> tasks) {
		this.tasks = tasks;
	}
	
	public Set<WebScriptTask> getTasks() {
		return tasks;
	}
	
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	
	public String getModuleCode() {
		return moduleCode;
	}
	
	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public String getFolderName() {
		return folderName;
	}

	public void setFrameworkType(String frameworkType) {
		this.frameworkType = frameworkType;
	}
	
	public String getFrameworkType() {
		return frameworkType;
	}

	@Override
	public String toString() {
		return "WebScriptModule [moduleId=" + moduleId + ", moduleName="
				+ moduleName + ", folderName=" + folderName + ", createTime="
				+ createTime + ", createUser=" + createUser + ", author="
				+ author + ", mark=" + mark + ", moduleCode=" + moduleCode
				+ ", frameworkType=" + frameworkType + "]";
	}
}
