package com.github.mujave.autoconfiguration;

import com.github.mujave.annotation.DcitMapper;
import com.github.mujave.annotation.DictMap;
import com.github.mujave.entity.DataDictDTO;
import com.github.mujave.service.DictCacheService;
import com.google.common.collect.BiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: 张雨
 * @create: 2020-11-29 21:23
 **/
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@Configuration
@ConditionalOnBean(DictCacheService.class)
public class DictMapAutoconfiguration implements PriorityOrdered {

    private final Log log = LogFactory.getLog(DictMapAutoconfiguration.class);

    @Autowired
    DictCacheService dictCacheService;

    /**
     * 切入点，所有具有dictMap的方法
     */
    @Pointcut("@annotation(dictMap)")
    public void dictMapPoint(DictMap dictMap) {
    }

    /**
     * 环绕通知方式，拦截方法返回值并进行翻译后返回
     *
     * @param pjp
     * @param dictMap
     * @return 翻译后的值
     * @throws Throwable
     */
    @Around("@annotation(dictMap)")
    public Object translationAround(final ProceedingJoinPoint pjp, DictMap dictMap) throws Throwable {
        Object result = pjp.proceed();
        Object obj = null;
        if (result != null) {
            Class<?> aClass = result.getClass();
            if (result instanceof List) {
                List olist = (List) result;
                if (olist.size() == 0) {
                    return result;
                } else {
                    obj = ((List) result).get(0);
                }
            }
            if (obj != null && !(obj instanceof Map)) {
                return result;
            }
            //收集类中的字典翻译目录
            List<DataDictDTO> dictMapping = getDictMapping(dictMap.value());
            if (dictMapping.size() == 0) {
                return result;
            }
            if (result instanceof List) {
                for (Map entity : (List<Map>) result) {
                    assign(entity, dictMapping);
                }
            } else {
                assign((Map) result, dictMapping);
            }
        }
        return result;
    }


    /**
     * 翻译方法
     * <p>按照{@code dictMapping}中的目录参数信息，将{@code entity}中的字段翻译</p>
     *
     * @param entity      需要翻译的对象
     * @param dictMapping 具体字段翻译的配置
     */
    private void assign(Map entity, List<DataDictDTO> dictMapping) {
        for (DataDictDTO dataDictDTO : dictMapping) {
            String dictName = dataDictDTO.getSourceField();
            String targetField = dataDictDTO.getTargetField();
            if (StringUtils.isBlank(targetField)) {
                targetField = dictName + "Name";
            }
            String nullValue = dataDictDTO.getNullValue();
            String undefinedValue = dataDictDTO.getUndefinedValue();
            BiMap<String, String> dict = dataDictDTO.getDictDetail();
            String preValue = entity.get(dataDictDTO.getSourceField()).toString();
            if (StringUtils.isNotEmpty(preValue)) {
                if (dataDictDTO.isMultiple()) {
                    StringBuffer buffer = new StringBuffer();
                    String[] strings = preValue.split(",");
                    for (String string : strings) {
                        String name = dict.get(string);
                        buffer.append(name == null ? undefinedValue : name).append(",");
                    }
                    entity.put(targetField, buffer.deleteCharAt(buffer.length() - 1).toString());
                } else {
                    String name = dict.get(preValue);
                    entity.put(targetField, name == null ? undefinedValue : name);
                }
            } else {
                entity.put(entity, nullValue);
            }

        }

    }


    /**
     * 收集类中的字典翻译目录信息
     *
     * @param mappers 要收集的类型
     * @return 定义的字典目录信息
     */
    private List<DataDictDTO> getDictMapping(DcitMapper[] mappers) {
        List<DataDictDTO> list = new ArrayList<>();
        for (DcitMapper mapper : mappers) {
            //收集字典翻译参数信息
            DataDictDTO dataDictDTO = new DataDictDTO(mapper.fieldName(), dictCacheService.getDictMapByName(mapper.dictName()), mapper.targetField(), mapper.multiple(), mapper.nullValueName(), mapper.undefinedValue());
            list.add(dataDictDTO);
        }
        return list;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
