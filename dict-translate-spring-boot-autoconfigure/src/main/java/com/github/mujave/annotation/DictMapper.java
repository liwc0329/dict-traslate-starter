package com.github.mujave.annotation;

import java.lang.annotation.*;

/**
 * @author: 张雨
 * @create: 2020-11-29 21:31
 **/
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictMapper {

    /**
     * 标记map中要进行翻译的字段
     */
    String fieldName();
    /**
     * 翻译使用的字典标识符，对应数据库sys_dict表中的name
     */
    String dictName();

    /**
     * 指明翻译完成后的值存放在这个字段中，默认情况为翻译字段+"Name"，且这个字段必须是{@link  String}
     */
    String targetField() default "";

    /**
     * 如果翻译字段(标记的这个字段)是null的时候，使用此值作为缺省值填充
     */
    String nullValueName() default "-";

    /**
     * 字段是否存储多个字典code,需要英文逗号分隔进行切割。例如，使用此注解的字段值为1,2，
     * 在用的字典中有{1-男,2-女}这样的定义，当 multiple 为true时会现将1,2(String)拆分为[1,2]，然后翻译为"男,女"，
     * 最终存放在targetField指明的字段值中
     */
    boolean multiple() default false;

    /**
     * 字典值未定义的时候的默认值。当前字段的值在字典中未能找到时，使用此值作为缺省值填充
     */
    String undefinedValue() default "";
}
