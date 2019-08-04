package yi.master.util.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;

/**
 * upload上传工具类
 * @author xuwangcheng
 * @version 20171205,1.0.0.0
 *
 */
public class Upload {
	
	private static final Logger LOGGER = Logger.getLogger(Upload.class);
	
	/**
	 * 上传单个文件
	 * @param Object  要上传的文件或者文件输入流
	 * @param fileName  文件原名
	 * @return 保存的文件路径
	 */
	public static String singleUpload (Object file, String fileName) {
		String projectPath = SystemConsts.EXCEL_FILE_FOLDER + File.separator + newName(fileName);
		String path = FrameworkUtil.getProjectPath() + File.separator + projectPath;
		InputStream is = null;
        OutputStream os = null;
        
        try {
        	
        	os = new FileOutputStream(new File(path));
			byte[] buffer = new byte[1024];
            int length = 0;
            
        	if (file instanceof InputStream) {
        		is = (InputStream) file;
        		while ((length = is.read(buffer, 0, 1024)) != -1) {  
    			    os.write(buffer, 0, length);  
    			}       		
        		
        	} else if (file instanceof File) {
        		is = new FileInputStream((File)file);
        		while((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
        	} else {
        		return null;
        	}						
            
		} catch (Exception e) {
			
			LOGGER.error("上传文件失败：源文件名-" + fileName + "目的文件-" + path, e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
        return path;
	}
	
	/**
	 * 上传多个文件
	 * @param files  多个文件
	 * @param fileNames  对应的多个文件名
	 * @return 上传保存成功的的文件路径
	 */
	public static String[] batchUpload (File[] files, String[] fileNames) {
		String[] paths = new String[files.length];
        
		for (int i = 0; i < files.length; i++) {
			paths[i] = singleUpload(files[i], fileNames[i]);
		}
		
		return paths;
	}
	
	/**
	 * 重命名文件
	 * @param srcName 文件原名
	 * @return  重名之后的名称
	 */
	public static String newName (String srcName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String ext = srcName.substring(srcName.lastIndexOf(".")), dt = sdf.format(new java.util.Date()), rd = Math.round(Math.random() * 900) + 100 + "";
        return dt + rd + ext;
	}
}
