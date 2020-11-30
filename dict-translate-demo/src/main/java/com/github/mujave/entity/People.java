package com.github.mujave.entity;


import com.github.mujave.annotation.Dict;
import com.github.mujave.annotation.DictCollection;
import com.github.mujave.annotation.DictEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: 张雨
 * @create: 2020-11-29 17:16
 **/
@Data
@Builder
public class People {
    private Integer id;
    private String name;
    @Dict(dictName = "sex")
    private Integer sex;
    private String sexName;
    @DictEntity
    private DrivingLicense drivingLicense;
    @DictCollection
    private List<Car> car;
}
