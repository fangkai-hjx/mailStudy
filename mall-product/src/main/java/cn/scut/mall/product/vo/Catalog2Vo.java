package cn.scut.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  "1":[
 *         {
 *             "catalog1Id":"1",
 *             "catalog3List":[
 *                 {
 *                     "catalog2Id":"1",
 *                     "id":"1",
 *                     "name":"电子书"
 *                 }....
 *             ],
 *             "id":"1",
 *             "name":"电子书刊"
 *         },...
 */
// 二级分类vo
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2Vo {
    private String catalog1Id;
    private List<Catalog3Vo> catalog3List;
    private String id;
    private String name;

    // 三级分类vo
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catalog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
