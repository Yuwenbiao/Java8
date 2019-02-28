package com.company.CompletableFuture组合式异步编程.Future接口.使用CompletableFuture构建异步应用;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
        return shops.stream().map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product))).collect(toList());
    }

    /**
     * 使用并行流对请求进行并行操作
     */
    private List<String> findPricesParallel(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        return shops.parallelStream().map(shop -> String.format("%s price is %.2f", shop.getShopName(), shop.getPrice(product))).collect(toList());
    }

    /**
     * 使用CompletableFuture发起异步请求
     */
    private List<String> findPricesCompletableFuture(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getShopName() + " price is " + shop.getPrice(product))).collect(toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
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
        List<CompletableFuture<String>> priceFutures = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getShopName() + " price is " + shop.getPrice(product), executor)).collect(toList());
        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
    }

    /**
     * 添加折扣
     */
    private String getPricesDiscount(String product) {
        Random random = new Random();
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", getShopName(), price, code);
    }

    /**
     * 使用Discount服务
     */
    public List<String> findPricesDiscount(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        return shops.stream().map(shop -> shop.getPricesDiscount(product)).map(Quote::parse).map(Discount::applyDiscount).collect(toList());
    }

    /**
     * 使用 CompletableFuture 实现 findPrices 方法
     */
    public List<String> findPricesDiscountCompletableFuture(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPricesDiscount(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor))).collect(toList());

//         合并两个独立的 CompletableFuture 对象
//        Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product)).thenCombine(CompletableFuture.supplyAsync(() -> exchangeService.getRate(Money.EUR, Money.USD)),(price, rate) -> price * rate);
        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
    }

    private static final Random random = new Random();

    /**
     * 一个模拟生成0.5秒至2.5秒随机延迟的方法
     */
    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 响应 CompletableFuture 的 completion 事件
     */

    /**
     * 重构 findPrices 方法返回一个由 Future 构成的流
     */
    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"), new Shop("LetsSaveBig"), new Shop("MyFavoriteShop"), new Shop("BuyItASll"));
        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        return shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getPricesDiscount(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
    }

    private void testFindPricesStream() {
        findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println));

        //响应 CompletableFuture 的 completion 事件
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream("myPhone27S").map(f -> f.thenAccept(s -> System.out.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " msecs)"))).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000) + " msecs");
    }
}
