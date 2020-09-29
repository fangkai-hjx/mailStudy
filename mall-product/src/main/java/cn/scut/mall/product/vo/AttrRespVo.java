package cn.scut.mall.product.vo;


import cn.scut.mall.product.entity.AttrEntity;
import lombok.Data;

/**
 * "attrId": 0, //属性id
 * "attrName": "string", //属性名
 * "attrType": 0, //属性类型，0-销售属性，1-基本属性
 * "catelogName": "手机/数码/手机", //所属分类名字--------------------------------------加上
 * "groupName": "主体", //所属分组名字-------------------------------------------------加上
 * "enable": 0, //是否启用
 * "icon": "string", //图标
 * "searchType": 0,//是否需要检索[0-不需要，1-需要]
 * "showDesc": 0,//是否展示在介绍上；0-否 1-是
 * "valueSelect": "string",//可选值列表[用逗号分隔]
 * "valueType": 0//值类型[0-为单个值，1-可以选择多个值]
 */
@Data
public class AttrRespVo extends AttrVo {

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
