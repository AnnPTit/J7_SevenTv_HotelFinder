package com.example.demo.util;

import com.example.demo.dto.RevenueDTO;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    public static byte[] exportToExcel(List<RevenueDTO> revenueList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Thống kê doanh thu từng tháng");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("STT");
        headerRow.createCell(1).setCellValue("Tháng");
        headerRow.createCell(2).setCellValue("Năm");
        headerRow.createCell(3).setCellValue("Doanh thu");

        int rowNum = 1;
        double totalRevenue = 0.0;

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0"));

        for (int i = 0; i < revenueList.size(); i++) {
            RevenueDTO revenue = revenueList.get(i);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(revenue.getMonth());
            row.createCell(2).setCellValue(revenue.getYear());
            Cell revenueCell = row.createCell(3);
            revenueCell.setCellValue(revenue.getRevenue().doubleValue());
            revenueCell.setCellStyle(currencyStyle);
            totalRevenue += revenue.getRevenue().doubleValue();
        }

        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(2).setCellValue("Tổng doanh thu");

        Cell totalRevenueCell = totalRow.createCell(3);
        totalRevenueCell.setCellValue(totalRevenue);
        totalRevenueCell.setCellStyle(currencyStyle);

        for (int i = 0; i < 14; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] excelBytes = byteArrayOutputStream.toByteArray();
        workbook.close();

        return excelBytes;
    }

}
