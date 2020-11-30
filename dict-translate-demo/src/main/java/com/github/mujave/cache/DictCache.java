package com.github.mujave.cache;

import com.github.mujave.service.DictCacheService;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.stereotype.Component;

/**
 * @author: 张雨
 * @create: 2020-11-29 17:23
 **/
@Component
public class DictCache implements DictCacheService {
    @Override
    public BiMap<String, String> getDictMapByName(String dictName) {
        BiMap<String, String> map = HashBiMap.create();
        switch (dictName) {
            case "sex":
                map.put("1", "男");
                map.put("2", "女");
                break;
            case "car:color":
                map.put("1", "芭比粉");
                map.put("2", "烈焰红");
                break;
            case "license:type":
                map.put("A1","大型汽车");
                map.put("C1","小型汽车");
                map.put("C2","小型自动挡汽车");
                map.put("D","普通三轮摩托车");
                break;
        }
        return map;
    }
}
