package elasticjob;


import elasticjob.job.MySimpleJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * Title: MySimpleJob
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/24 23:22:15
 */
public class MySimpleJobDemo {
    public static void main(String[] args) {
        new ScheduleJobBootstrap(createRegistryCenter(), new MySimpleJob(), createJobConfiguration()).schedule();
    }

    private static CoordinatorRegistryCenter createRegistryCenter() {
        // 注意！重点！！zookeeper必须是3.6或更高版本
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("127.0.0.1:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    private static JobConfiguration createJobConfiguration() {
        JobConfiguration simpleCoreConfig = JobConfiguration
                .newBuilder("mySimpleJob", 2)
                .cron("0/1 * * * * ?")
                .shardingItemParameters("0=A,1=B")
                .build();
        return simpleCoreConfig;
    }
}
