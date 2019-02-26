package com.company.CompletableFuture组合式异步编程.Future接口;

import java.util.concurrent.*;

/**
 * 使用Future以异步的方式执行一个耗时的操作
 *
 * @author yuwb@corp.21cn.com
 * @date 19-2-26 上午9:36
 */
public class FutureDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Double> future = executor.submit(doSomething);
        doSomethingElse();

        try {
            Double result = future.get(1, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (ExecutionException ee) {
            System.out.println("计算抛出异常");
        } catch (InterruptedException ie) {
            System.out.println("线程被中断");
        } catch (TimeoutException te) {
            System.out.println("超时");
        }
    }

    private static Callable<Double> doSomething = () -> 1d;

    private static void doSomethingElse() {
        System.out.println("执行其他事情");
    }
}
