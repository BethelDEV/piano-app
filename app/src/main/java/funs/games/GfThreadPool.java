package funs.games;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: GfThreadPool
 * @Description: 后台绘制 线程池
 * SingleThreadExecutor：单个后台线程 (其缓冲队列是无界的)。
 * <p>
 * 单线程的线程池。这个线程池只有一个核心线程在工作，也就是相当于单线程串行执行所有任务。
 * 如果这个唯一的线程因为异常结束，那么会有一个新的线程来替代它。
 * 此线程池保证所有任务的执行顺序按照任务的提交顺序执行。
 * <p>
 * ThreadPoolExecutor(...)
 * int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue
 * <p>
 * corePoolSize：核心线程数
 * maximumPoolSize：最大线程数
 * <p>
 * keepAliveTime：空闲线程回收时间间隔
 * unit：空闲线程回收时间间隔单位
 * <p>
 * workQueue：提交任务的队列，当线程数量超过核心线程数时，可以将任务提交到任务队列中。
 * 比较常用的有：ArrayBlockingQueue; LinkedBlockingQueue; SynchronousQueue;
 */
public class GfThreadPool {
    private static ExecutorService pool = null;

    /* 初始化线程池 */
    public static void init() {
        if (pool == null) {
//            pool = Executors.newSingleThreadExecutor();
            pool = new ThreadPoolExecutor(1, 8,
                    10L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        }
    }

    /* 提交任务执行 */
    public static void execute(Runnable r) {
        init();
        pool.execute(r);
    }

    /* 关闭线程池 */
    public static void unInit() {
        if (pool == null || pool.isShutdown()) return;
        pool.shutdownNow();
        pool = null;
    }
}
