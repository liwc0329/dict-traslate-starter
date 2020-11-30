package com.github.mujave.entity;

import com.github.mujave.annotation.Dict;
import lombok.Builder;
import lombok.Data;

/**
 * @author: 张雨
 * @create: 2020-11-29 20:56
 **/
@Data
@Builder
public class DrivingLicense {
    private String number;
    @Dict(dictName = "license:type", multiple = true)
    private String type;
    private String typeName;
}
