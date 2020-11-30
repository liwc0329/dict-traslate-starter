package com.github.mujave.service;

import com.github.mujave.annotation.DcitMapper;
import com.github.mujave.annotation.DictMap;
import com.github.mujave.annotation.DictTranslation;
import com.github.mujave.entity.Car;
import com.github.mujave.entity.DrivingLicense;
import com.github.mujave.entity.People;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author: 张雨
 * @create: 2020-11-29 17:16
 **/
@Component
public class TestServiceImpl {


    @DictTranslation
    public List<People> getStudent() {
        People jok = People.builder().id(1).name("jack").sex(1).build();
        People jone = People.builder().id(2).name("john").sex(2).build();
        return Arrays.asList(jok, jone);
    }

    @DictTranslation
    public People testDictEntity() {
        DrivingLicense license = DrivingLicense.builder().number("102312000311").type("C1,D").build();
        People jok = People.builder().id(1).name("jack").sex(1).drivingLicense(license).build();
        return jok;
    }

    @DictTranslation
    public People testDictCollection() {
        Car car1 = Car.builder().name("沃尔沃").color(1).build();
        Car car2 = Car.builder().name("大奔").color(2).build();
        People mujave = People.builder().id(1).name("mujave").sex(1).car(Arrays.asList(car1, car2)).build();
        return mujave;
    }

    @DictMap({
            @DcitMapper(fieldName = "sex", dictName = "sex"),
            @DcitMapper(fieldName = "carColor", dictName = "car:color")
    })
    public HashMap<String, String> testMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "mujave");
        map.put("sex", "1");
        map.put("carColor", "1");

        return map;
    }
}
