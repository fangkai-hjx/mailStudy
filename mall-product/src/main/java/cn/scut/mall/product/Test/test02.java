package cn.scut.mall.product.Test;

import lombok.Data;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class test02 implements Serializable{
    private HashMap mChooseMap;

    public HashMap getChooseMap(){
        return mChooseMap;
    }
    public void setChooseMap(HashMap chooseMap){
        mChooseMap = chooseMap;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        test02 contactItem = new test02();
        contactItem.setChooseMap(new LinkedHashMap() {
            {
                put("男", "1");
                put("女", "2");
            }
        });
//        LinkedHashMap genderMap = new LinkedHashMap<>();
//
//        genderMap.put("男", "1");
//
//        genderMap.put("女", "2");
//        contactItem.setChooseMap(genderMap);
        test02 people1 = deepClone(contactItem);
        System.out.println(people1.getChooseMap());
    }
    public static<T> T deepClone(T o) throws IOException, ClassNotFoundException {
        //将对象写到流里
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(o);
        //从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        return (T) oi.readObject();
    }
}
@Data
class People implements Serializable{
    private String username;
    private Integer age;
}

