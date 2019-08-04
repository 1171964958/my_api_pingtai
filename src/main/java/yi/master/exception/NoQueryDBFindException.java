package yi.master.exception;


/**
 * 找不到指定的查询数据库信息
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */
public class NoQueryDBFindException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoQueryDBFindException() {
		super();
	}

	public NoQueryDBFindException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoQueryDBFindException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoQueryDBFindException(String message) {
		super(message);
	}

	public NoQueryDBFindException(Throwable cause) {
		super(cause);
	}
	
	
	
}
