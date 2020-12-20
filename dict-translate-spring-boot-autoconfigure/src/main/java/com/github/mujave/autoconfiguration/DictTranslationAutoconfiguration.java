package com.github.mujave.autoconfiguration;

import com.github.mujave.annotation.Dict;
import com.github.mujave.annotation.DictCollection;
import com.github.mujave.annotation.DictEntity;
import com.github.mujave.annotation.DictTranslation;
import com.github.mujave.entity.DataDictDTO;
import com.github.mujave.entity.DictTargetType;
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
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典翻译切面
 *
 * @author: 张雨
 * @create: 2020-11-29 15:18
 **/
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@Configuration
@ConditionalOnBean(DictCacheService.class)
public class DictTranslationAutoconfiguration implements PriorityOrdered {

    private final Log log = LogFactory.getLog(DictTranslationAutoconfiguration.class);

    @Autowired
    DictCacheService dictCacheService;

    /**
     * 切入点，所有具有DictTranslation的方法
     */
    @Pointcut("@annotation(dictTranslation)")
    public void dictTranslationPoint(DictTranslation dictTranslation) {
    }

    /**
     * 环绕通知方式，拦截方法返回值并进行翻译后返回
     *
     * @param pjp
     * @param dictTranslation
     * @return 翻译后的值
     * @throws Throwable
     */
    @Around("@annotation(dictTranslation)")
    public Object translationAround(final ProceedingJoinPoint pjp, DictTranslation dictTranslation) throws Throwable {
        Object result = pjp.proceed();
        //返回值中的泛型类型
        Object obj;
        if (result != null) {
            //判断拦截的方法的返回值类型是否合法
            Class<?> aClass = result.getClass();
            if (result instanceof List) {
                List olist = (List) result;
                if (olist.size() == 0) {
                    return result;
                }
                obj = olist.get(0);
            } else {
                obj = result;
            }
            //收集类中的字典翻译目录
            List<DataDictDTO> dictMapping = getDictMapping(obj.getClass());
            if (dictMapping.size() == 0) {
                return result;
            }
            if (result instanceof List) {
                for (Object entity : (List) result) {
                    assign(entity, dictMapping);
                }
            } else {
                assign(result, dictMapping);
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
    private void assign(Object entity, List<DataDictDTO> dictMapping) {
        try {
            for (DataDictDTO dataDictDTO : dictMapping) {
                if (dataDictDTO.getDictTargetType() == DictTargetType.FIELD) {
                    String dictName = dataDictDTO.getSourceField();
                    String targetField = dataDictDTO.getTargetField();
                    if (StringUtils.isBlank(targetField)) {
                        targetField = dictName + "Name";
                    }
                    String nullValue = dataDictDTO.getNullValue();
                    String undefinedValue = dataDictDTO.getUndefinedValue();
                    BiMap<String, String> dict = dataDictDTO.getDictDetail();

                    Class c = entity.getClass();
                    if (c != null) {
                        Field f = c.getDeclaredField(dictName);
                        f.setAccessible(true);
                        Field fvalue = c.getDeclaredField(targetField);
                        fvalue.setAccessible(true);
                        if (fvalue.getType() != String.class) {
                            log.error("dict Translation having an error. Field " + targetField + " typeis not String");
                            continue;
                        }
                        Object preValue = f.get(entity);
                        if (!ObjectUtils.isEmpty(preValue)) {
                            String preValueStr = String.valueOf(preValue);
                            // 需要赋值的字段
                            if (dataDictDTO.isMultiple()) {
                                StringBuffer buffer = new StringBuffer();
                                String[] strings = preValueStr.split(",");
                                for (String string : strings) {
                                    String name = dict.get(string);
                                    buffer.append(name == null ? undefinedValue : name).append(",");
                                }
                                fvalue.set(entity, buffer.deleteCharAt(buffer.length() - 1).toString());
                            } else {
                                String name = dict.get(preValueStr);
                                fvalue.set(entity, name == null ? undefinedValue : name);
                            }
                        } else {
                            fvalue.set(entity, nullValue);
                        }
                    }
                } else {
                    Field fvalue = entity.getClass().getDeclaredField(dataDictDTO.getSourceField());
                    fvalue.setAccessible(true);
                    Object preValue = fvalue.get(entity);
                    if (!ObjectUtils.isEmpty(preValue)) {
                        if (dataDictDTO.getDictTargetType() == DictTargetType.COLLECTION) {
                            for (Object o : (List) preValue) {
                                assign(o, dataDictDTO.getCollectionDictInfo());
                            }
                        } else {
                            assign(preValue, dataDictDTO.getCollectionDictInfo());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("dict Translation having an error. " + e.toString());
        }
    }


    /**
     * 收集类中的字典翻译目录信息
     *
     * @param cla 要收集的类型
     * @return 定义的字典目录信息
     */
    private List<DataDictDTO> getDictMapping(Class cla) {
        Field[] fields = cla.getDeclaredFields();
        List<DataDictDTO> list = new ArrayList<>();
        DataDictDTO dataDictDTO;
        Dict dict;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Dict.class)) {
                dict = field.getAnnotation(Dict.class);
                //收集字典翻译参数信息
                dataDictDTO = new DataDictDTO(field.getName(), dictCacheService.getDictMapByName(dict.dictName()), dict.targetField(), dict.multiple(), dict.nullValueName(), dict.undefinedValue());
                list.add(dataDictDTO);
            } else if (field.isAnnotationPresent(DictEntity.class)) {
                if (cla != field.getClass()) {
                    dataDictDTO = new DataDictDTO(DictTargetType.ENTITY, field.getName(), getDictMapping(field.getType()));
                    list.add(dataDictDTO);
                } else {
                    log.error(field.getName() + "'s class type equals with parent is true");
                }
            } else if (field.isAnnotationPresent(DictCollection.class)) {
                Type genericType = field.getGenericType();
                if (genericType == null) {
                    continue;
                }
                // 如果是泛型参数的类型
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    //得到泛型里的class类型对象
                    Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
                    if (genericClazz != cla) {
                        dataDictDTO = new DataDictDTO(DictTargetType.COLLECTION, field.getName(), getDictMapping(genericClazz));
                        list.add(dataDictDTO);
                    } else {
                        log.error(field.getName() + "'s class type equals with parent is true");
                    }
                }else if (genericType instanceof Collection){
                    Collection c = (Collection) genericType;
                }
            }
        }
        return list;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
