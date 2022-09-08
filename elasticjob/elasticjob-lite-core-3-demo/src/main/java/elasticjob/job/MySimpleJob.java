package elasticjob.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Title: MyElasticJob
 * Description:
 *
 * @author Lin Hui
 * Created on 2021/8/24 23:22:30
 */
//@Slf4j
public class MySimpleJob implements SimpleJob {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("MyElasticJob executing. Date: " + DATE_FORMAT.format(new Date()) + ". total: "
                + shardingContext.getShardingTotalCount() + ". shardingParameter: " + shardingContext.getShardingParameter());
    }
}
