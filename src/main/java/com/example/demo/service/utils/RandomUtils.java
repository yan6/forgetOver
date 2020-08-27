package com.example.demo.service.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.Random;
import java.util.UUID;

/**
 * Created by yqf on 16-6-22.
 */
public class RandomUtils {

    public static final int TOKEN_LENGTH = 16;

    public static String getRandomSalt() {
        return UUID.randomUUID().toString();
    }

    public static String getRandomToken() {
        return RandomStringUtils.random(TOKEN_LENGTH, true, true);
    }

    public static int getRandomInt(int min, int max) {
        if (max < min) {
            throw new RuntimeException("getRandomInt max can not less than min");
        }
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static int[] randomArray(int min, int max, int n) {
        int len = max - min + 1;

        if (max < min || n > len) {
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min + len; i++) {
            source[i - min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getRandomInt(1, 1));
        System.out.println(Math.random());
        System.out.println( new RandomDataGenerator().nextLong(1, 100));
        for (int i = 0; i <10 ; i++) {

            System.out.println(getRandomInt(2,3));
        }
    }
}
