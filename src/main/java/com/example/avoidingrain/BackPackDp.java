package com.example.avoidingrain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BackPackDp {
    /**
     * items商品价格，n商品个数, w表示满减条件，比如200
     *
     * @param items
     * @param n
     * @param w
     */
    public List<Integer> compute(int[] items, int n, int w) {
        List<Integer> res=new ArrayList<>();
        boolean[][] states = new boolean[n][3 * w + 1];//超过3倍就没有薅羊毛的价值了
        states[0][0] = true;  // 第一行的数据要特殊处理
        if (items[0] <= 3 * w) {
            states[0][items[0]] = true;
        }
        for (int i = 1; i < n; ++i) { // 动态规划
            for (int j = 0; j <= 3 * w; ++j) {// 不购买第i个商品
                if (states[i - 1][j]) {
                    states[i][j] = states[i - 1][j];
                }
            }
            for (int j = 0; j <= 3 * w - items[i]; ++j) {//购买第i个商品
                if (states[i - 1][j]) {
                    states[i][j + items[i]] = true;
                }
            }
        }

        int j;
        for (j = w; j < 3 * w + 1; ++j) {
            if (states[n - 1][j]) {
                break; // 输出结果大于等于w的最小值
            }
        }
        if (j == 3 * w + 1) {
            return res; // 没有可行解
        }
        for (int i = n - 1; i >= 1; --i) { // i表示二维数组中的行，j表示列
            if (j - items[i] >= 0 && states[i - 1][j - items[i]]) {
                // 购买这个商品
                res.add(items[i]);
                j = j - items[i];
            } // else 没有购买这个商品，j不变。
        }
        if (j != 0) {
            res.add(items[0]);
        }
        System.out.println(res);
        return res;
    }

    @Test
    public void test() {
        int[] items = new int[]{300, 500, 100, 900};
        int n = 4;
        int w = 800;
        compute(items, n, w);
    }

    @Test
    public void test1() {
        int[] items = new int[]{399, 500, 700, 1200, 1800, 2500};
        int n = 6;
        int w = 800;
        compute(items, n, w);
    }
}
