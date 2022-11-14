package com.example.avoidingrain.utils;

import com.example.avoidingrain.AvoidRain;
import com.example.avoidingrain.entity.TestCase;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
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
            System.out.println(String.format("生成第%s个测试数据",i));
            caseList.add(generateCase(n, k, step));
        }
        return caseList;
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
        avoidRain.backTrack(enhanceShelter, homie, 0, new LinkedList<>());
        return new TestCase(Arrays.deepToString(shelter),
                Arrays.toString(homie),
                JsonUtils.toJson(avoidRain.getMinTrack()),
                String.valueOf(avoidRain.getMinDist()),
                String.valueOf(System.currentTimeMillis() - startTime));
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
