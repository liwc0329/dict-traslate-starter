package com.github.mujave.service;

import com.google.common.collect.BiMap;

/**
 * @author: 张雨
 * @create: 2020-11-29 15:26
 **/
public interface DictCacheService {

    BiMap<String, String> getDictMapByName(String dictName);

}
