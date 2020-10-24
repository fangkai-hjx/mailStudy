package cn.scut.mall.product;

import cn.scut.mall.product.entity.BrandEntity;
import cn.scut.mall.product.service.BrandService;
import cn.scut.mall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallProductApplicationTest {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

//    //测试文件上传
//    @Test
//    public void testUpload() throws FileNotFoundException {
////        // Endpoint以杭州为例，其它Region请按实际情况填写。
////        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
////        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
////        String accessKeyId = "LTAI4GAMWbzLyZYLJZPYVNKC";
////        String accessKeySecret = "QAub0YkfqTLIxfD3LEjvJIiMLYChd6";
//        // 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("D:\\1.jpg");
//        ossClient.putObject("fangkai-file", "2.jpg", inputStream);
//
//// 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传成功");
//    }

    @Test
    public void save() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功");
    }

    @Test
    public void select() {
        List<BrandEntity> brandId = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
        brandId.forEach((item) -> {
            System.out.println(item);
        });
        System.out.println("查询成功");
    }

    @Test
    public void test2() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        System.out.println(Arrays.asList(catelogPath));
    }
    /**
     * 测试 redis
     */
    @Test
    public void testRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", UUID.randomUUID().toString());

        String key = "hello";
        System.out.println(ops.get(key));
    }
}
