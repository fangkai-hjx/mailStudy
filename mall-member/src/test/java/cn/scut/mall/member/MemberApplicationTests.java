package cn.scut.mall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


//@SpringBootTest
class MemberApplicationTests {

    @Test
    void contextLoads() {
        String s = DigestUtils.md5Hex("123456");//e10adc3949ba59abbe56e057f20f883e
        System.out.println(s);

        //盐值加密：随机值
        String s1 = Md5Crypt.md5Crypt("123456".getBytes(),"$1$111");//他这里默认加了盐值 B64.getRandomSalt(8, random)
        System.out.println(s1);//$1$111$jlIdVbvDgZq/syocWtLEJ1

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String ss1 = passwordEncoder.encode("abc");
        String ss2 = passwordEncoder.encode("abc");
        System.out.println(ss1);
        System.out.println(ss2);
        System.out.println(passwordEncoder.matches("abc",ss1));
        System.out.println(passwordEncoder.matches("abc",ss2));
    }

}
