package yi.master.coretest.message.process;

import org.apache.commons.lang.StringUtils;

import yi.master.constant.MessageKeys;

/**
 * 在进行测试前需要进行特殊处理的操作,比如加密、解密、格式化等
 * @author xuwangcheng
 * @version 2018.4.19
 *
 */
public abstract class MessageProcess {	
	private static final ShanXiOpenApiMessageProcess sxOpenApiProcess = new ShanXiOpenApiMessageProcess();
	private static final AnHuiAPPEncryptMessageProcess ahAPPEncryptProcess = new AnHuiAPPEncryptMessageProcess();
	
	/**
	 * 处理请求报文
	 * @param requestMessage 请求报文
	 * @param processParameter 处理类型的参数设定
	 * @return 返回经过处理的入参报文
	 */
	public abstract String processRequestMessage(String requestMessage, String processParameter);
	/**
	 * 处理返回报文
	 * @param requestMessage
	 * @param processParameter
	 * @return
	 */
	public abstract String processResponseMessage(String responseMessage, String processParameter);
	
	
	public static MessageProcess getProcessInstance(String processType) {
		if (StringUtils.isBlank(processType)) return null;
		switch (processType) {
		case MessageKeys.PROCESS_TYPE_SHANXI_OPEN_API:
			return sxOpenApiProcess;
		case MessageKeys.PROCESS_TYPE_ANHUI_APP:
			return ahAPPEncryptProcess;
		default:
			break;
		}		
		return null;
	}	
}
