package net.coderlin.demo.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Title: CaffeineRemovalTest
 * Description:
 *
 * @author Lin Hui
 * Created on 2022/9/9 15:19:44
 */
public class CaffeineRemovalTest {
    /**
     * 显式移除
     */
    @Test
    public void testExplicitRemoval() throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .build();
        cache.put("zhangsan", "hello");
        System.out.println(cache.getIfPresent("zhangsan"));
        Thread.sleep(5000);
        System.out.println(cache.getIfPresent("zhangsan"));
    }

    @Test
    public void testRemovalListener() throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(3, TimeUnit.SECONDS)
                .evictionListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was evicted (%s)%n", key, cause))
                .removalListener((String key, String value, RemovalCause cause) ->
                        System.out.printf("Key %s was removed (%s)%n", key, cause))
                .build();
        cache.put("zhangsan", "hello");
        System.out.println("zhangsan: " + cache.getIfPresent("zhangsan"));
        Thread.sleep(1000L);
        cache.put("lisi", "helloworld");
        // lisi写入后，因超过最大值，zhangsan缓存需剔除
        Thread.sleep(1000L);
        // 输出null
        System.out.println("zhangsan: " + cache.getIfPresent("zhangsan"));
        Thread.sleep(1000L);
        // 正常输出
        System.out.println("lisi: " + cache.getIfPresent("lisi"));
        Thread.sleep(5000);
        // 输出null，因为超时，超过预设的3秒了
        System.out.println("lisi: " + cache.getIfPresent("lisi"));
    }
}
