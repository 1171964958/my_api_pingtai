package yi.master.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomConditionSetting {
	public static final String DATETIME_TYPE = "datetime";
	public static final String STRING_TYPE = "string";
	String conditionType() default STRING_TYPE;//条件类型，目前支持string、datetime
	String operator() default "like"; //运算符，目前仅支持 ‘like’ 和 ‘=’ '>' '<'
}
