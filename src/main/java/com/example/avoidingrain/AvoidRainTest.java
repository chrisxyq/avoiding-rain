package com.example.avoidingrain;

import com.example.avoidingrain.utils.JsonUtils;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;

@Component
public class AvoidRainTest {
    @Test
    public void test1() {
        //  {5, 2}表示位置5处有处避雨处，可容纳两人。。。
        int[][] shelter = new int[][]{{5, 2}, {13, 3}, {21, 4}};
        // 表示大家独立处在的位置
        int[] homie = new int[]{0, 7, 8, 10, 13, 15, 17};
        testProcedure(shelter, homie);
    }

    @Test
    public void test2() {
        int[][] shelter = new int[][]{{5, 1}, {12, 1}};
        int[] homie = new int[]{1, 7, 13};
        testProcedure(shelter, homie);
    }

    @Test
    public void test3() {
        int[][] shelter = new int[][]{{5, 2}, {13, 3}};
        int[] homie = new int[]{1, 7, 8, 10, 15};
        testProcedure(shelter, homie);
    }

    @Test
    public void test4() {
        int[][] shelter = new int[][]{{1, 0}, {13, 1}};
        int[] homie = new int[]{2};
        testProcedure(shelter, homie);
    }

    @Test
    public void test5() {
        int[][] shelter = new int[][]{{1, 1}, {13, 1}};
        int[] homie = new int[]{2};
        testProcedure(shelter, homie);
    }

    @Test
    public void test6() {
        int[][] shelter = new int[][]{{1, 0}, {13, 0}};
        int[] homie = new int[]{2};
        testProcedure(shelter, homie);
    }

    @Test
    public void test7() {
        int[][] shelter = new int[][]{{1, 6}, {13, 15}};
        int[] homie = new int[]{2, 5, 6, 7, 8, 12, 16, 19, 31, 34, 36, 38, 40, 41, 43, 47};
        testProcedure(shelter, homie);
    }

    /**
     * n个人k个亭子，时间复杂度：(k+1)**n
     */
    @Test
    public void test8() {
        int n = 5;
        int k = 5;
        int range = 5;
        int[][] shelter = new int[k][2];
        for (int i = 0; i < k; i++) {
            shelter[i][0] = getRandom(i * range, (i + 1) * range);
            shelter[i][1] = getRandom(1, 2);
        }
        int[] homie = new int[n];
        for (int i = 0; i < n; i++) {
            homie[i] = getRandom(i * range, (i + 1) * range);
        }
        System.out.println(String.format("shelter：%s,", Arrays.deepToString(shelter)));
        System.out.println(String.format("homie：%s,", Arrays.toString(homie)));
        testProcedure(shelter, homie);
    }

    /**
     * 区间 [min,max]
     *
     * @param min
     * @param max
     * @return
     */
    private int getRandom(int min, int max) {
        return (int) (Math.random() * max + min);
    }

    private void testProcedure(int[][] shelter, int[] homie) {
        long startTime = System.currentTimeMillis();
        AvoidRain avoidRain = new AvoidRain();
        int[][] enhanceShelter = avoidRain.enhance(shelter);
        // 记录「路径」
        avoidRain.backTrack(enhanceShelter, homie, 0, new LinkedList<>());
        System.out.println(String.format("===最优避雨方案：%s，路径长度：%s===",
                JsonUtils.toJson(avoidRain.getMinTrack()), avoidRain.getMinDist()));
        long endTime = System.currentTimeMillis();
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
    }
}
