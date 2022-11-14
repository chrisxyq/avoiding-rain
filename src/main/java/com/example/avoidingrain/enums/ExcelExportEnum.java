package com.example.avoidingrain.enums;

public enum ExcelExportEnum {
    SHELTER(0, "shelter"),
    HOMIE(1, "homie"),
    TARGET(2, "target"),
    S(4, "s"),
    RUN_TIME(5, "runTime(ms)");
    private final int    index;
    private final String name;


    ExcelExportEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static ExcelExportEnum getEnum(Integer index) {
        for (ExcelExportEnum codeTypeEnum : values()) {
            if (codeTypeEnum.getIndex() == index) {
                return codeTypeEnum;
            }
        }
        return null;
    }
}
