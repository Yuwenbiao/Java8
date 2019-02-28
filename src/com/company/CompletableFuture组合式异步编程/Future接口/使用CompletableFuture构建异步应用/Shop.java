package com.company.CompletableFuture组合式异步编程.Future接口.使用CompletableFuture构建异步应用;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 商店
 *
 * @author yuwb@corp.21cn.com
 * @date 19-2-26 下午12:05
 */
public class Shop {
    private String shopName;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product) {
//        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
//        new Thread(() -> {
//            try {
//                double price = calculatePrice(product);
//                futurePrice.complete(price);
//            } catch (Exception ex) {
//                //抛出CompletableFuture内的异常
//                futurePrice.completeExceptionally(ex);
//            }
//        }).start();
//        return futurePrice;

        //使用工厂方法supplyAsync创建CompletableFuture对象
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
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

    /**
     * 使用异步API
     */
    public static void main(String[] args) {
        Shop shop = new Shop("test");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        doSomethingElse();

        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievaTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievaTime + " msecs");
    }

    private static void doSomethingElse() {
        System.out.println("doSomethingElse");
    }

    /**
     * 采用顺序查询所有商店的方式实现的findPrices方法
     */
    private List<String> findPrices(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        return shops.stream().map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    /**
     * 使用并行流对请求进行并行操作
     */
    private List<String> findPricesParallel(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        return shops.parallelStream().map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product))).collect(Collectors.toList());
    }

    /**
     * 使用CompletableFuture发起异步请求
     */
    private List<String> findPricesCompletableFuture(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getShopName() + " price is " + shop.getPrice(product))).collect(Collectors.toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 寻找更好的方案
     */
    private List<String> findPricesSetThreadCount(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getShopName() + " price is " + shop.getPrice(product), executor)).collect(Collectors.toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 添加折扣
     */
    private String getPricesDiscount(String product) {
        return "";
    }
}
