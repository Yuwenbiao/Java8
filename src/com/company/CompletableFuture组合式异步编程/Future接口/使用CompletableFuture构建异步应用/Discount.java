package com.company.CompletableFuture组合式异步编程.Future接口.使用CompletableFuture构建异步应用;

import java.util.concurrent.TimeUnit;

/**
 * 以枚举类型定义的折扣代码
 *
 * @author yuwb@corp.21cn.com
 * @date 19-2-28 下午12:16
 */
public class Discount {
    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * Discount服务
     */
    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " + Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        deley();
        return price * (100 - code.percentage) / 100;
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
