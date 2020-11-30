package com.github.mujave.annotation;

import java.lang.annotation.*;

/**
 * 标记没有集合字段需要进行字典翻译
 * @author: 张雨
 * @create: 2020-11-29 15:14
 **/
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictCollection {
}
