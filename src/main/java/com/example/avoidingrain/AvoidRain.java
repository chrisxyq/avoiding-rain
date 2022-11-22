package com.example.avoidingrain;


import com.example.avoidingrain.entity.PathTrack;
import com.example.avoidingrain.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class AvoidRain {
    private int                 minDist  = Integer.MAX_VALUE;
    private LinkedList<Integer> minTrack = new LinkedList<>();

    public void backTrack(int[][] shelter, int[] homie, int start, PathTrack pathTrack) {
        //结束条件
        if (pathTrack.getAvailableShelter()==0) {
            //结束条件：没有避雨空间，直接返回
            int temp = pathTrack.getSumDist();
            //System.out.println(String.format("当前避雨方案：%s，路径长度：%s", JsonUtils.toJson(track), temp));
            if (temp < minDist) {
                minTrack = new LinkedList<>(pathTrack.getTrack());
                minDist = temp;
            }
            return;
        } else if (pathTrack.getTrack().size() == homie.length) {
            //有避雨空间，但人已经遍历完了
            if (pathTrack.isContainsIgnore()) {
                //当使用的避雨空间数量小于人数，且还有避雨空间，结果不合法，直接返回
                return;
            } else {
                int temp = getSumDist(pathTrack.getTrack(), homie, shelter);
                //System.out.println(String.format("当前避雨方案：%s，路径长度：%s", JsonUtils.toJson(track), temp));
                if (temp < minDist) {
                    minTrack = new LinkedList<>(pathTrack.getTrack());
                    minDist = temp;
                }
                return;
            }
        }
        for (int i = 0; i < shelter.length; i++) {
            //该避雨处，无可容纳空间，跳过
            if (i < shelter.length - 1 && shelter[i][1] == 0) {
                continue;
            }
            //======做选择:路径增加，雨棚位置-1
            if (i == shelter.length - 1) {
                //不躲雨
                pathTrack.getTrack().add(-1);
                pathTrack.setContainsIgnore(true);
            } else {
                //躲雨
                pathTrack.getTrack().add(i);
                shelter[i][1] -= 1;
                pathTrack.setAvailableShelter(pathTrack.getAvailableShelter()-1);
                //todo
                pathTrack.setSumDist(pathTrack.getSumDist()+Math.abs(homie[start] - shelter[i][0]));
            }
            // 进入下一层决策树
            backTrack(shelter, homie, start + 1, pathTrack.getTrack());
            // =========取消选择:路径撤销，雨棚位置+1
            pathTrack.getTrack().removeLast();
            if (i < shelter.length - 1) {
                shelter[i][1] += 1;
                pathTrack.setAvailableShelter(pathTrack.getAvailableShelter()+1);
                pathTrack.setSumDist(pathTrack.getSumDist()-Math.abs(homie[start] - shelter[i][0]));
            }else{
                pathTrack.setContainsIgnore(false);
            }
        }
    }
    /**
     * 路径、选择列表、结束条件
     *
     * @return
     */
    public void backTrack(int[][] shelter, int[] homie, int start, LinkedList<Integer> track) {
        //结束条件
        if (noShelter(shelter)) {
            //结束条件：没有避雨空间，直接返回
            int temp = getSumDist(track, homie, shelter);
            //System.out.println(String.format("当前避雨方案：%s，路径长度：%s", JsonUtils.toJson(track), temp));
            if (temp < minDist) {
                minTrack = new LinkedList<>(track);
                minDist = temp;
            }
            return;
        } else if (track.size() == homie.length) {
            //有避雨空间，但人已经遍历完了
            if (track.contains(-1)) {
                //当使用的避雨空间数量小于人数，且还有避雨空间，结果不合法，直接返回
                return;
            } else {
                int temp = getSumDist(track, homie, shelter);
                //System.out.println(String.format("当前避雨方案：%s，路径长度：%s", JsonUtils.toJson(track), temp));
                if (temp < minDist) {
                    minTrack = new LinkedList<>(track);
                    minDist = temp;
                }
                return;
            }
        }
        for (int i = 0; i < shelter.length; i++) {
            //该避雨处，无可容纳空间，跳过
            if (i < shelter.length - 1 && shelter[i][1] == 0) {
                continue;
            }
            //做选择:路径增加，雨棚位置-1
            if (i == shelter.length - 1) {
                //不躲雨
                track.add(-1);
            } else {
                //躲雨
                track.add(i);
                shelter[i][1] -= 1;
            }
            // 进入下一层决策树
            backTrack(shelter, homie, start + 1, track);
            // 取消选择:路径撤销，雨棚位置+1
            track.removeLast();
            if (i < shelter.length - 1) {
                shelter[i][1] += 1;
            }
        }
    }

    public int getSumDist(List<Integer> track, int[] homie, int[][] shelter) {
        int sumDist = 0;
        for (int i = 0; i < track.size(); i++) {
            Integer integer = track.get(i);
            if (integer == -1) {
                continue;
            }
            sumDist += Math.abs(homie[i] - shelter[integer][0]);
        }
        return sumDist;
    }

    public boolean noShelter(int[][] shelter) {
        int sumShelter = 0;
        for (int[] ints : shelter) {
            sumShelter += ints[1];
        }
        return sumShelter == 0;
    }
    public int getMinDist() {
        return minDist;
    }

    public LinkedList<Integer> getMinTrack() {
        return minTrack;
    }

    public void setMinDist(int minDist) {
        this.minDist = minDist;
    }

    public void setMinTrack(LinkedList<Integer> minTrack) {
        this.minTrack = minTrack;
    }

    public int[][] enhance(int[][] shelter) {
        int len = shelter.length;
        int[][] enhanceShelter = new int[len + 1][];
        System.arraycopy(shelter, 0, enhanceShelter, 0, len);
        enhanceShelter[len] = new int[]{-1, 0};
        return enhanceShelter;
    }
}
