package com.github.mujave.entity;

import com.github.mujave.annotation.Dict;
import lombok.Builder;
import lombok.Data;

/**
 * @author: 张雨
 * @create: 2020-11-29 20:16
 **/
@Data
@Builder
public class Car {
    private String name;
    @Dict(dictName = "car:color",targetField = "carColor")
    private Integer color;
    private  String carColor;
}
