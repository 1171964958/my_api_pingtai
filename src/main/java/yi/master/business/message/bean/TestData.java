package yi.master.business.message.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;
// default package


import yi.master.annotation.FieldRealSearch;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.util.PracticalUtils;

/**
 * 
 * 所属测试场景的测试数据
 * @author xuwangcheng
 * @version 1.0.0.0,2017.5.5
 *
 */

public class TestData implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields    

     private Integer dataId;
     private MessageScene messageScene;
     
     /**
      * json串存储数据,节点路径名作为key,数据作为value
      */
     private String paramsData;
     
     /**
      * 可用状态
      * <br>0-可用  1-不可用/已使用
      * <br>新增   2 - 可重复使用，不论接口类型
      */
     @FieldRealSearch(names = {"可用", "已使用", "可重复使用"}, values = {"0", "1", "2"})
     private String status;
     
     /**
      * 数据标记
      * <br>用户自定义,根据接口、报文、场景的不同有所不同
      * <br>不允许重复
      */
     private String dataDiscr;
     
     /**
      * 通过处理将数据和原始报文结合之后的报文内容
      */
     private String dataJson;

     
     /**
      * 所属测试环境
      */
     private String systems;
     
     /**
      * 所属测试环境
      */
     private Set<BusinessSystem> businessSystems;
     
     /**
      * 是否为默认数据,默认数据适用于所有的测试环境,但是选择优先级低于属于其他数据<br>
      * 0 - 是<br>
      * 1 - 否
      */
     private String defaultData;

    // Constructors

    /** default constructor */
    public TestData() {
    }

    
    /** full constructor */
    public TestData(MessageScene messageScene, String paramsData, String status,String dataDiscr) {
        this.messageScene = messageScene;
        this.paramsData = paramsData;
        this.status = status;
        this.dataDiscr = dataDiscr;
    }

    public boolean checkSystem (String systemId) {
    	if ("0".equals(this.defaultData)) {
    		return true;
    	}
    	
    	Set<String> systems = new HashSet<String>(Arrays.asList(this.systems.split(",")));
    	return systems.contains(systemId);
    }
   
    // Property accessors
    
    
    public void setDefaultData(String defaultData) {
		this.defaultData = defaultData;
	}
    
    public String getDefaultData() {
		return defaultData;
	}

    public String getSystems() {
 		return systems;
 	}
     
     public void setSystems(String systems) {
 		this.systems = systems;
 	}
     
     public Set<BusinessSystem> getBusinessSystems() {
 		return PracticalUtils.getSystems(this.systems);
 	}
    
    public Integer getDataId() {
        return this.dataId;
    }
    
    public String getDataJson() {
		return dataJson;
	}


	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}


	public String getDataDiscr() {
		return dataDiscr;
	}


	public void setDataDiscr(String dataDiscr) {
		this.dataDiscr = dataDiscr;
	}


	public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }
    
    @JSON(serialize=false)
    public MessageScene getMessageScene() {
        return this.messageScene;
    }
    
    public void setMessageScene(MessageScene messageScene) {
        this.messageScene = messageScene;
    }

    public String getParamsData() {
        return this.paramsData;
    }
    
    public void setParamsData(String paramsData) {
        this.paramsData = paramsData;
    }

    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }


	@Override
	public int hashCode() {
		
		int result = 17;
		result = result * 31 + dataId;
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof TestData)) {
			return false;
		}
		
		TestData d = (TestData)obj;
		return d.dataId == dataId;
	}


	@Override
	public String toString() {
		return "TestData [dataId=" + dataId + ", paramsData=" + paramsData
				+ ", status=" + status + ", dataDiscr=" + dataDiscr
				+ ", dataJson=" + dataJson + ", systems=" + systems
				+ ", defaultData=" + defaultData + "]";
	}

    
    

}