package net.coderlin.middleware.demo.powerjob.processor;

import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * Title: MySimpleProcessor
 * Description: 基础处理器实现，任务运行逻辑。
 *
 * @author Lin Hui
 * Created on 2022/12/20 16:41:49
 */
public class MySimpleProcessor implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        System.out.println(taskContext.toString());
        return new ProcessResult(true);
    }
}
