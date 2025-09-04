package com.ljt.study;

/**
 * @author LiJingTang
 * @date 2025-07-23 09:14
 */
public class JdkDebugTest {

    public static void main(String[] args) throws Exception {
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 60L, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(),
//                r -> new Thread(r, "test-"),
//                new ThreadPoolExecutor.AbortPolicy());
//
//        executor.shutdownNow();
//        executor.shutdown();

        System.out.println("hello hi".replace("h", "H"));

    }



}
