package com.example.avoidingrain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathTrack {
    private LinkedList<Integer> track;
    private int availableShelter;
    private int sumDist;
    private boolean containsIgnore;

}
