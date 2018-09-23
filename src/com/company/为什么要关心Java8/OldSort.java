package com.company.为什么要关心Java8;

import com.company.为什么要关心Java8.Apple;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * 传统写法
 *
 * @author yuwb
 * @date 18-9-23 上午9:46
 */
public class OldSort {
    private void sortApple() {
        List<Apple> inventory = Arrays.asList(new Apple(80,"green"),
                new Apple(155, "green"),
                new Apple(120, "red"));

        Collections.sort(inventory, new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        //使用lambda表达式
        inventory.sort(comparing(Apple::getWeight));
    }
}
