package net.coderlin.demo.caffeine;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Title: CaffeineTest
 * Description:
 * <p>
 * Caffeine 提供了灵活的构造来创建具有以下功能组合的缓存：
 * <p>
 * 自动将条目自动加载到缓存中，可以选择异步加载
 * 基于频率和新近度超过最大值时基于大小的逐出
 * 自上次访问或上次写入以来测得的基于时间的条目到期
 * 发生第一个陈旧的条目请求时，异步刷新
 * 键自动包装在弱引用中
 * 值自动包装在弱引用或软引用中
 * 逐出（或以其他方式删除）条目的通知
 * 写入传播到外部资源
 * 缓存访问统计信息的累积
 *
 * @author Lin Hui
 * Created on 2022/9/8 16:23:24
 */
public class CaffeineAddTest {
    /**
     * 手动加载
     */
    @Test
    public void testManualLoading() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
        String key = "zhangsan";
        // 查找一个缓存元素， 没有查找到的时候返回null
        String value = cache.getIfPresent(key);
        Assert.assertNull(value);
        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        // 通过 cache.get(key, k -> value) 的方式将要缓存的元素通过原子计算的方式 插入到缓存中，以避免和其他写入进行竞争
        value = cache.get(key, k -> createExpensiveGraph(key));
        Assert.assertEquals("new value", value);
        // 添加或者更新一个缓存元素
        cache.put(key, "updated value");
        Assert.assertEquals("updated value", cache.getIfPresent(key));
        // 移除一个缓存元素
        cache.invalidate(key);
        Assert.assertNull(cache.getIfPresent(key));
    }

    /**
     * 自动加载
     */
    @Test
    public void testAutoLoading() {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(this::createExpensiveGraph);
        String key = "zhangsan";
        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String value = cache.get(key);
        cache.put("wangwu", "updated value");
        // 批量查找缓存，如果缓存不存在则生成缓存元素
        Map<String, String> map = cache.getAll(Arrays.asList("zhangsan", "lisi", "wangwu", "zhaoliu"));
        // {zhangsan=new value, lisi=new value, wangwu=new value}
        System.out.println(map);
    }

    /**
     * 手动异步加载
     */
    @Test
    public void manualAsyncLoading() {
        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .buildAsync();
        String key = "zhangsan";
        // 查找一个缓存元素， 没有查找到的时候返回null
        CompletableFuture<String> value = cache.getIfPresent(key);
        // 查找缓存元素，如果不存在，则异步生成
        value = cache.get(key, k -> createExpensiveGraph(key));
        // 添加或者更新一个缓存元素
        cache.put(key, value);
        // 移除一个缓存元素
        cache.synchronous().invalidate(key);
    }

    /**
     * 自动异步加载
     */
    @Test
    public void autoAsyncLoading() throws ExecutionException, InterruptedException {
        AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 你可以选择: 去异步的封装一段同步操作来生成缓存元素
                .buildAsync(this::createExpensiveGraph);
        // 你也可以选择: 构建一个异步缓存元素操作并返回一个future
        // .buildAsync((key, executor) -> createExpensiveGraphAsync(key));
        String key = "zhangsan";
        // 查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<String> value = cache.get(key);
        // 批量查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<Map<String, String>> values = cache.getAll(Arrays.asList("zhangsan", "lisi", "wangwu", "zhaoliu"));
    }

    private String createExpensiveGraph(String key) {
        return key + "_newValue";
    }

}
