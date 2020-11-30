package com.github.mujave.annotation;

import java.lang.annotation.*;

/**
 * 标记这个字典时一个进行字典翻译的实体类
 * @author: 张雨
 * @create: 2020-11-29 15:14
 **/
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictEntity {
}
