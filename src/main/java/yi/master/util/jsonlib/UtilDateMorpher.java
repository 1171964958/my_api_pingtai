package yi.master.util.jsonlib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.ezmorph.ObjectMorpher;

import org.springframework.util.StringUtils;


public class UtilDateMorpher implements ObjectMorpher {

	 private String format = "yyyy-MM-dd HH:mm:ss";
	    
	    /**
	     * json转换成java object
	     * @param value json字符串
	     * 
	     */
	    @Override
	    public Object morph(Object value) {
	        SimpleDateFormat sf = new SimpleDateFormat(format);
	        if(StringUtils.isEmpty(value)){
	            return null;
	        }
	        try {
	            return sf.parse((String)value);
	        } catch (ParseException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    /**
	     * 对哪种java对象进行解析
	     */
	    @Override
	    public Class morphsTo() {
	        return Date.class;
	    }

	    /**
	     * 支持那种clazz类型的解析
	     */
	    @Override
	    public boolean supports(Class clazz) {
	        if(clazz == String.class){
	            return true;
	        }
	        return false;
	    }

	
}
