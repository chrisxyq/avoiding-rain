package com.example.avoidingrain.controller;

import com.example.avoidingrain.entity.TestCase;
import com.example.avoidingrain.entity.TestCaseDp;
import com.example.avoidingrain.enums.ExcelExportDPEnum;
import com.example.avoidingrain.enums.ExcelExportEnum;
import com.example.avoidingrain.utils.ExcelUtils;
import com.example.avoidingrain.utils.TestCaseGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ExportExcelController {
    @Autowired
    TestCaseGenerator testCaseGenerator;

    private static final String EXCEL_CONTENT_TYPE          = "application/msexcel";
    private static final String EXCEL_HEAD_TYPE             = "Content-Disposition";
    private static final String EXCEL_HEAD_FILE_NAME        = "attchement;filename=%s";
    private static final String EXCEL_HEAD_FILE_NAME_FORMAT = "ISO8859-1";
    private static final String GB2312                      = "gb2312";

    @RequestMapping("/exportExcelDP")
    public void exportExcelDP(HttpServletResponse response) throws IOException {
        List<String> headRowNames = Lists.newArrayList();
        headRowNames.add(ExcelExportDPEnum.ITEMS.getName());
        headRowNames.add(ExcelExportDPEnum.N.getName());
        headRowNames.add(ExcelExportDPEnum.W.getName());
        headRowNames.add(ExcelExportDPEnum.RES.getName());
        headRowNames.add(ExcelExportDPEnum.RUN_TIME.getName());
        List<Map<String, String>> listData = Lists.newArrayList();
        List<TestCaseDp> caseList = testCaseGenerator.generateDPCaseList(500);
        for (TestCaseDp testCase : caseList) {
            Map<String, String> mapItem = new HashMap<>();
            mapItem.put(ExcelExportDPEnum.ITEMS.getName(), testCase.getItems());
            mapItem.put(ExcelExportDPEnum.N.getName(), testCase.getN());
            mapItem.put(ExcelExportDPEnum.W.getName(), testCase.getW());
            mapItem.put(ExcelExportDPEnum.RES.getName(), testCase.getRes());
            mapItem.put(ExcelExportDPEnum.RUN_TIME.getName(), testCase.getRunTime());
            listData.add(mapItem);
        }
        byte[] excelData = ExcelUtils.getExcelDataDefault("sheet", headRowNames, listData);
        exportExcel(excelData, response, "20221122凑单测试case.xlsx");
    }

    /**
     * n个人k个亭子，时间复杂度：(k+1)**n
     * http://localhost:8080/exportExcel?n=10&k=10
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response,
                            @RequestParam(value = "n") Integer n,
                            @RequestParam(value = "k") Integer k) throws IOException {
        List<String> headRowNames = Lists.newArrayList();
        headRowNames.add(ExcelExportEnum.SHELTER.getName());
        headRowNames.add(ExcelExportEnum.HOMIE.getName());
        headRowNames.add(ExcelExportEnum.TARGET.getName());
        headRowNames.add(ExcelExportEnum.S.getName());
        headRowNames.add(ExcelExportEnum.RUN_TIME.getName());
        List<Map<String, String>> listData = Lists.newArrayList();
        //n个人k个亭子，时间复杂度：(k+1)**n
        List<TestCase> caseList = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < k; j++) {
                System.out.println(String.format("i:%s,j:%s", i, j));
                caseList.add(testCaseGenerator.generateCase(i, j, 5));
            }
        }
        for (TestCase testCase : caseList) {
            Map<String, String> mapItem = new HashMap<>();
            mapItem.put(ExcelExportEnum.SHELTER.getName(), testCase.getShelter());
            mapItem.put(ExcelExportEnum.HOMIE.getName(), testCase.getHomie());
            mapItem.put(ExcelExportEnum.TARGET.getName(), testCase.getTarget());
            mapItem.put(ExcelExportEnum.S.getName(), testCase.getS());
            mapItem.put(ExcelExportEnum.RUN_TIME.getName(), testCase.getRunTime());
            listData.add(mapItem);
        }
        byte[] excelData = ExcelUtils.getExcelDataDefault("sheet", headRowNames, listData);
        exportExcel(excelData, response, "20221114避雨测试case.xlsx");
    }

    private void exportExcel(byte[] excelBytes, HttpServletResponse response, String fileName) {
        try {
            OutputStream outputStream = response.getOutputStream();
            response.reset();
            String fileSet = String.format(EXCEL_HEAD_FILE_NAME, fileName);
            String fileSetInfo = new String(fileSet.getBytes(GB2312), EXCEL_HEAD_FILE_NAME_FORMAT);
            response.setHeader(EXCEL_HEAD_TYPE, fileSetInfo);
            response.setContentType(EXCEL_CONTENT_TYPE);
            outputStream.write(excelBytes);
        } catch (Exception ex) {
            log.warn("exportExcel", ex);
        }
    }
}

