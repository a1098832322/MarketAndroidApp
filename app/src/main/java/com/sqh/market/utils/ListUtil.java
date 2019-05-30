package com.sqh.market.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * List工具类
 *
 * @author 郑龙
 */
public class ListUtil {
    /**
     * 通过随机数，从原始List中截取指定数量的数据集作为新List返回
     *
     * @param originList  原始List
     * @param listMaxSize 返回的最大List长度
     * @param <T>         list类型
     * @return 截取后的list
     */
    public static <T> List<T> getRandomList(List<T> originList, int listMaxSize) {
        List<T> resultList = new ArrayList<>();

        if (listMaxSize > originList.size()) {
            //如果需要的list长度大于原始数据列长度，则直接返回原始数据列
            return originList;
        } else {
            //否则随机返回
            int[] randoms = randomCommon(0, originList.size(), listMaxSize);

            for (int i = 0; i < randoms.length; i++) {
                int index = randoms[i];
                T item = originList.get(index);
                resultList.add(item);
            }
            return resultList;
        }
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    private static int[] randomCommon(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }
}
