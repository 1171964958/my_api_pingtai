package yi.master.exception.message;

/**
 * 接口自动化<br>
 * 拼装指定格式的报文，没有查找到足够的测试数据
 * @author xuwangcheng
 * @version 1.0.0.0,2017.4.6
 *
 */
public class NotEnoughTestDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotEnoughTestDataException() {
		super();
	}

	public NotEnoughTestDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotEnoughTestDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotEnoughTestDataException(String message) {
		super(message);
	}

	public NotEnoughTestDataException(Throwable cause) {
		super(cause);
	}

	
	
	

}
