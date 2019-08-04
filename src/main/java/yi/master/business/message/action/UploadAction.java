package yi.master.business.message.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.upload.Upload;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 文件上传Action
 * @author xuwangcheng
 * @version 20171205,1.0.0.0
 *
 */
@Controller
@Scope("prototype")
public class UploadAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,Object> jsonMap = new HashMap<String,Object>();
	
	private File file;
    
    //提交过来的file的名字
    private String fileFileName;
    
    private String downloadFileName;
    
    
    //提交过来的file的MIME类型
    private String fileFileContentType;
    
    public String upload() {
    	int returnCode = ReturnCodeConsts.SUCCESS_CODE;
    	String msg = "文件上传成功!";
    	
    	if (file == null) {
    		returnCode = ReturnCodeConsts.NO_FILE_UPLOAD_CODE;
    		msg = "未发现上传的文件!";
    	} else {
    		String fps = Upload.singleUpload(file, this.getFileFileName());
    		
    		if (fps == null) {
    			returnCode = ReturnCodeConsts.SYSTEM_ERROR_CODE;
        		msg = "上传文件失败,请重试!";
    		} else {
    			jsonMap.put("path", fps);
        		jsonMap.put("relativePath", fps.replace(FrameworkUtil.getProjectPath() + file.separator , ""));
    		}   		
    	}
    	
    	
    	jsonMap.put("msg", msg);
    	jsonMap.put("returnCode", returnCode);
    	return SUCCESS;
    }
    
    public InputStream getDownloadStream() throws FileNotFoundException {
    	String filePath = FrameworkUtil.getProjectPath() + file.separator + downloadFileName;
    	this.setFileFileName(downloadFileName.substring(downloadFileName.lastIndexOf(file.separator) + 1));
    	InputStream is = new FileInputStream(new File(filePath)); 
    	return is;
    }
    
    public String download() {
    	return "download";
    }
    
    public Map<String, Object> getJsonMap() {
		return jsonMap;
	}
    
    public void setFile(File file) {
		this.file = file;
	}
	
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	
	public void setFileFileContentType(String fileFileContentType) {
		this.fileFileContentType = fileFileContentType;
	}
	
	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}
	
	@JSON(serialize=false)
	public File getFile() {
		return file;
	}
	
	@JSON(serialize=false)
	public String getFileFileName() {
		return fileFileName;
	}
	@JSON(serialize=false)
	public String getFileFileContentType() {
		return fileFileContentType;
	}
	
	@JSON(serialize=false)
	public String getDownloadFileName() {
		return downloadFileName;
	}
	
}
