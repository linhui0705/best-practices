package net.coderlin.middleware.demo.powerjob;

import com.google.common.collect.Lists;
import tech.powerjob.worker.PowerJobWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;
import tech.powerjob.worker.common.constants.StoreStrategy;

/**
 * Title: MySimpleJobDemo
 * Description:
 *
 * @author Lin Hui
 * Created on 2022/12/20 16:32:51
 */
public class MySimpleJobDemo {
    public static void main(String[] args) throws Exception {
        createPowerJobWorker().init();
    }

    public static PowerJobWorker createPowerJobWorker() {
        // 1. 创建配置文件
        PowerJobWorkerConfig config = new PowerJobWorkerConfig();
        config.setPort(28888);
        config.setAppName("best-practices");
        // 需启动Server，Worker才能初始化
        config.setServerAddress(Lists.newArrayList("127.0.0.1:7700", "127.0.0.1:7701"));
        // 如果没有大型 Map/MapReduce 的需求，建议使用内存来加速计算
        config.setStoreStrategy(StoreStrategy.MEMORY);

        // 2. 创建 Worker 对象，设置配置文件
        PowerJobWorker worker = new PowerJobWorker();
        worker.setConfig(config);
        return worker;
    }
}
