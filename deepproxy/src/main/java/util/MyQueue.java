package util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangfei on 2018/1/10.
 */

public class MyQueue {
    private static ThreadPoolExecutor
        executor = new ThreadPoolExecutor(3, 6, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    public static void runInBack(Runnable  runnable){
        executor.execute(runnable);
    }
}
