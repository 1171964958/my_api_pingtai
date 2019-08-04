package yi.master.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldNameMapper {
	String fieldPath() default ""; //对应的HQL中的查询名
	boolean ifSearch() default true; //该字段是否需要被全局模糊查询
	boolean ifOrder() default true;//是否可以被排序
}
