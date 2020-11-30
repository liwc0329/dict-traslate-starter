package com.github.mujave.entity;

import com.google.common.collect.BiMap;

import java.util.List;

/**
 * @author: 张雨
 * @create: 2020-11-29 15:21
 **/
public class DataDictDTO {
    private DictTargetType dictTargetType;
    /**
     * 原字段
     */
    private String sourceField;
    /**
     * 当前字段翻译使用的字典
     */
    private BiMap<String, String> dictDetail;
    /**
     * 目标字段，翻译的字段name的字段名称
     */
    private String targetField;
    /**
     * 是否是多个字典值拼接的形式
     */
    private Boolean multiple;
    /**
     * 原字段是null时的缺省值
     */
    private String nullValue;
    /**
     * 找不到字典对应时的缺省值
     */
    private String undefinedValue;

    /**
     * 当翻译字段是实体类或者集合的时候，将每个字段的信息放到这个里面
     */
    private List<DataDictDTO> collectionDictInfo;


    public DataDictDTO(String sourceField, BiMap<String, String> dictDetail, String targetField, boolean multiple, String nullValue, String undefinedValue) {
        this.dictTargetType= DictTargetType.FIELD;
        this.sourceField = sourceField;
        this.dictDetail = dictDetail;
        this.targetField = targetField;
        this.multiple = multiple;
        this.nullValue = nullValue;
        this.undefinedValue = undefinedValue;
    }

    public DataDictDTO(DictTargetType dictTargetType, String sourceField, List<DataDictDTO> collectionDictInfo) {
        this.dictTargetType = dictTargetType;
        this.sourceField = sourceField;
        this.collectionDictInfo = collectionDictInfo;
    }

    public DictTargetType getDictTargetType() {
        return dictTargetType;
    }

    public void setDictTargetType(DictTargetType dictTargetType) {
        this.dictTargetType = dictTargetType;
    }

    public String getSourceField() {
        return sourceField;
    }

    public void setSourceField(String sourceField) {
        this.sourceField = sourceField;
    }

    public BiMap<String, String> getDictDetail() {
        return dictDetail;
    }

    public void setDictDetail(BiMap<String, String> dictDetail) {
        this.dictDetail = dictDetail;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public Boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getNullValue() {
        return nullValue;
    }

    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    public String getUndefinedValue() {
        return undefinedValue;
    }

    public void setUndefinedValue(String undefinedValue) {
        this.undefinedValue = undefinedValue;
    }

    public List<DataDictDTO> getCollectionDictInfo() {
        return collectionDictInfo;
    }

    public void setCollectionDictInfo(List<DataDictDTO> collectionDictInfo) {
        this.collectionDictInfo = collectionDictInfo;
    }

    @Override
    public String toString() {
        return "DataDictDTO{" +
                "dictTargetType=" + dictTargetType +
                ", sourceField='" + sourceField + '\'' +
                ", dictDetail=" + dictDetail +
                ", targetField='" + targetField + '\'' +
                ", multiple=" + multiple +
                ", nullValue='" + nullValue + '\'' +
                ", undefinedValue='" + undefinedValue + '\'' +
                ", collectionDictInfo=" + collectionDictInfo +
                '}';
    }
}
