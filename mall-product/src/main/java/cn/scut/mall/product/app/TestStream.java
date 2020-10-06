package cn.scut.mall.product.app;

import java.util.ArrayList;
import java.util.List;

public class TestStream {
    public static void main(String[] args) {
//        test01();
//        test02();
        test03();
    }
    public static void test01(){
        List<String> list = new ArrayList<>();
        list.add("张无忌");
        list.add("周芷若");
        list.add("赵敏");
        list.add("张强");
        list.add("张三丰");
        for (String name : list) {
            System.out.println(name);
        }
    }
    public static void test02(){
        List<String> list = new ArrayList<>();
        list.add("张无忌");
        list.add("周芷若");
        list.add("赵敏");
        list.add("张强");
        list.add("张三丰");
        List<String> zhangList = new ArrayList<>();
        for (String name : list) {
            if (name.startsWith("张")) {
                zhangList.add(name);
            }
        }
        List<String> shortList = new ArrayList<>();
        for (String name : zhangList) {
            if (name.length() == 3) {
                shortList.add(name);
            }
        } for (String name : shortList) {
            System.out.println(name);
        }
    }
    public static void test03(){
        List<String> list = new ArrayList<>();
        list.add("张无忌");
        list.add("周芷若");
        list.add("赵敏");
        list.add("张强");
        list.add("张三丰");
        list.stream()
                .filter(s->s.startsWith("张"))
                .filter(s->s.length()==3)
                .map(null)
                .forEach(System.out::println);
    }
}
