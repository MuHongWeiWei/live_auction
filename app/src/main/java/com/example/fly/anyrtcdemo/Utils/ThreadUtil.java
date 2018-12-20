package com.example.fly.anyrtcdemo.Utils;

public class ThreadUtil {
    private static ThreadPool mThreadPool = getThreadPool();

    private ThreadUtil() {
    }

    public static void runInUIThread(Runnable task, long delayMillis) {
        AppUtils.getHandler().postDelayed(task, delayMillis);
    }

    public static ThreadPool getThreadPool() {
        if (mThreadPool == null) {
            synchronized (ThreadUtil.class) {
                if (mThreadPool == null) {
                    int count = 15;
                    mThreadPool = new ThreadPool(count, count, 0L);
                }
            }
        }
        return mThreadPool;
    }

    public static class ThreadPool {
        private int corePoolSize;//核心線程數
        private int maximumPoolSize;// 最大線程數
        private long keepAliveTime;// 保持活躍時間

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }
    }
}