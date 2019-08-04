package yi.master.business.api.bean;

import net.sf.json.JSONObject;

/**
 * 返回json字符串
 * @author xuwangcheng
 *
 */
public class ApiReturnInfo {
	
	public static final String SUCCESS_CODE = "200";
	public static final String ERROR_CODE = "400";
	public static final String DISABLED_CODE = "100";
	
	private String returnCode;
	private String msg;
	private Object data;
	
	public ApiReturnInfo(String returnCode, String msg, Object data) {
		super();
		this.returnCode = returnCode;
		this.msg = msg;
		this.data = data;
	}
	
	public ApiReturnInfo() {
		super();
	}

	public ApiReturnInfo addData(String key, Object value) {
		if (data == null || !(data instanceof JSONObject)) {
			data = new JSONObject();
		}
		((JSONObject) data).put(key, value);
		return this;
	}
	
	public String getReturnCode() {
		return returnCode;
	}

	public ApiReturnInfo setReturnCode(String returnCode) {
		this.returnCode = returnCode;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public ApiReturnInfo setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Object getData() {
		return data;
	}
	
	public ApiReturnInfo setData(Object data) {
		this.data = data;
		return this;
	}
	
	@Override
	public String toString() {
		return "ApiReturnInfo [returnCode=" + returnCode + ", msg=" + msg
				+ ", data=" + data + "]";
	}
}
