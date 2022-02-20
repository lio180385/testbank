package com.bank.services;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.bank.model.TrasactionHistory;
import com.bank.model.Users;
import com.bank.repo.TransactionHistoryRepo;
import com.bank.repo.TrasactionTypeRepo;
import com.bank.response.History;

import java.io.*;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelGenerator {
	@Autowired
	private TransactionHistoryRepo historyRepo;

    /* export */
    public ByteArrayInputStream exportExcel(Iterable<Users> siswas) throws Exception{
        String[] columns = {"Tanggal Transaksi", "Jumlah", "Type"};
        try(
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
                )
        {
            CreationHelper creationHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Transaction History");
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            //Row ofor Header
            Row headerRow = sheet.createRow(0);

            //Header
            for(int i=0;i<columns.length;i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }


            int rowIdx = 1;
            for(Users his : siswas) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(his.getUsername());
                row.createCell(1).setCellValue(his.getPasswordhash());
                row.createCell(2).setCellValue(his.getAccountnumber());
              
                rowIdx++;
            }

            workbook.write(out);
            workbook.close();
            return new ByteArrayInputStream(out.toByteArray());
        }catch(Exception e) {

        }
        return null;
    }

    /* Import */
    public void importExcel(MultipartFile file) throws Exception{

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for(int i=0;i<(CoutRowExcel(sheet.rowIterator()));i++) {
            if(i == 0) {
                continue;
            }

            Row row = sheet.getRow(i);

            String tgl = row.getCell(1).getStringCellValue();
            String jumlah = row.getCell(2).getStringCellValue();
            String type = row.getCell(3).getStringCellValue();
            String account = row.getCell(4).getStringCellValue();
//            historyRepo.save(new TrasactionHistory(0, tgl, jumlah, type,account));
        }

    }

    /* Cout Row of Excel Table */
    public static int CoutRowExcel(Iterator<Row> iterator) {
        int size = 0;
        while(iterator.hasNext()) {
            Row row = iterator.next();
            size++;
        }
        return size;
    }
}
