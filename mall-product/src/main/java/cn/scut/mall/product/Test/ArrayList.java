package cn.scut.mall.product.Test;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ArrayList {
    /**
     * ThreadLocal 中保存的数据是 Map
     * ThreadLocal 提供了一种方式，让在多线程环境下，每个线程都可以拥有自己独特的数据，并且
     * 可以在整个线程执行过程中，从上而下的传递。
     */
    static final ThreadLocal<Map<String, String>> context = new ThreadLocal<>();

    private static String getFromComtext() {
        String value1 = context.get().get("key1");
        log.info("从 ThreadLocal 中取出上下文， key1 对应的值为： {}", value1);
        return value1;
    }

    public static void main(String[] args) {
        // 从上下文中拿出 Map
        Map<String, String> contextMap = context.get();
        if (CollectionUtils.isEmpty(contextMap)) {
            contextMap = Maps.newHashMap();
        }
        contextMap.put("key1", "value1");
        context.set(contextMap);
        log.info("key1， value1 被放到上下文中");
        // 从上下文中拿出刚才放进去的数据
        getFromComtext();
    }
}
