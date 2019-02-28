package com.company.CompletableFuture组合式异步编程.Future接口.使用CompletableFuture构建异步应用;

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
    //Discount类的具体实现这里暂且不表示
}
