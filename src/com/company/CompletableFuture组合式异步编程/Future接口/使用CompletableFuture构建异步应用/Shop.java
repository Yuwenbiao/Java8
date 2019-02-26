package com.company.CompletableFuture组合式异步编程.Future接口.使用CompletableFuture构建异步应用;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 商店
 *
 * @author yuwb@corp.21cn.com
 * @date 19-2-26 下午12:05
 */
public class Shop {
    public double getPrice(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        Random random = new Random();
        deley();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    /**
     * 模拟1秒钟延迟的方法
     */
    public static void deley() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
