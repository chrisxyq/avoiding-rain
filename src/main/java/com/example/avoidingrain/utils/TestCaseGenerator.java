package com.example.avoidingrain.utils;

import com.example.avoidingrain.AvoidRain;
import com.example.avoidingrain.BackPackDp;
import com.example.avoidingrain.entity.PathTrack;
import com.example.avoidingrain.entity.TestCase;
import com.example.avoidingrain.entity.TestCaseDp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class TestCaseGenerator {
    /**
     * n个人k个亭子，时间复杂度：(k+1)**n
     */
    public List<TestCase> generateCaseList(int n, int k, int step, int num) {
        List<TestCase> caseList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            System.out.println(String.format("生成第%s个测试数据", i));
            caseList.add(generateCase(n, k, step));
        }
        return caseList;
    }

    public List<TestCaseDp> generateDPCaseList(int n) {
        List<TestCaseDp> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            res.add(generateDPCase(i));
        }
        return res;
    }

    private TestCaseDp generateDPCase(int i) {
        long startTime = System.currentTimeMillis();
        int[] items = new int[i];
        int step = getRandom(100, 200);
        for (int temp = 0; temp < i; temp++) {
            items[temp] = getRandom(temp * step, (temp + 1) * step);
        }
        int w = 100 * i;
        BackPackDp backPackDp = new BackPackDp();
        List<Integer> compute = backPackDp.compute(items, items.length, w);
        return new TestCaseDp(Arrays.toString(items),
                String.valueOf(items.length),
                String.valueOf(w),
                JsonUtils.toJson(compute), String.valueOf(System.currentTimeMillis() - startTime));
    }

    public TestCase generateCase(int n, int k, int step) {
        int[][] shelter = new int[k][2];
        for (int i = 0; i < k; i++) {
            shelter[i][0] = getRandom(i * step, (i + 1) * step);
            shelter[i][1] = getRandom(1, 2);
        }
        int[] homie = new int[n];
        for (int i = 0; i < n; i++) {
            homie[i] = getRandom(i * step, (i + 1) * step);
        }
        //testProcedure(shelter, homie);
        AvoidRain avoidRain = new AvoidRain();
        int[][] enhanceShelter = avoidRain.enhance(shelter);
        // 记录「路径」
        long startTime = System.currentTimeMillis();
        avoidRain.backTrack(enhanceShelter, homie, 0, new PathTrack(new LinkedList<>(),
                getAvailableShelter(shelter), 0, false));
        return new TestCase(Arrays.deepToString(shelter),
                Arrays.toString(homie),
                JsonUtils.toJson(avoidRain.getMinTrack()),
                String.valueOf(avoidRain.getMinDist()),
                String.valueOf(System.currentTimeMillis() - startTime));
    }

    private int getAvailableShelter(int[][] shelter) {
        int res = 0;
        for (int i = 0; i < shelter.length; i++) {
            res += shelter[i][1];
        }
        return res;
    }
    //private void testProcedure(int[][] shelter, int[] homie) {
    //    initPara();
    //    int[][] enhanceShelter = avoidRain.enhance(shelter);
    //    // 记录「路径」
    //    avoidRain.backTrack(enhanceShelter, homie, 0, new LinkedList<>());
    //}
    //
    //private void initPara() {
    //    avoidRain.setMinDist(0);
    //    avoidRain.setMinTrack(new LinkedList<>());
    //}

    private int getRandom(int min, int max) {
        return (int) (Math.random() * max + min);
    }
}
