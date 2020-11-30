package com.github.mujave.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标记方法返回值，将进行字典自动翻译.
 * @author: 张雨
 * @create: 2020-11-29 15:13
 **/
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictTranslation {
}
