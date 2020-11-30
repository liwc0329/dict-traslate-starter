package com.github.mujave.annotation;

import java.lang.annotation.*;

/**
 * @author: 张雨
 * @create: 2020-11-29 15:16
 **/
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictMap {

    DcitMapper[] value();
}
