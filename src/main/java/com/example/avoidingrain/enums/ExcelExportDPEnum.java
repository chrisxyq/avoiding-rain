package com.example.avoidingrain.enums;

public enum ExcelExportDPEnum {
    ITEMS(0, "items"),
    N(1, "n"),
    W(2, "w"),
    RES(4, "res"),
    RUN_TIME(5, "runTime(ms)");
    private final int    index;
    private final String name;


    ExcelExportDPEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static ExcelExportDPEnum getEnum(Integer index) {
        for (ExcelExportDPEnum codeTypeEnum : values()) {
            if (codeTypeEnum.getIndex() == index) {
                return codeTypeEnum;
            }
        }
        return null;
    }
}
