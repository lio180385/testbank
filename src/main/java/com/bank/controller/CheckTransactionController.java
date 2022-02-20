package com.bank.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.model.TransactionType;
import com.bank.model.TrasactionHistory;
import com.bank.model.Users;
import com.bank.repo.TransactionHistoryRepo;
import com.bank.repo.TrasactionTypeRepo;
import com.bank.repo.UserRepo;
import com.bank.response.History;
import com.bank.services.ExcelGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.bcel.internal.generic.NEW;
 
 
 
 
 
 

@RestController
@RequestMapping("/api/transaction")
public class CheckTransactionController {
	@Autowired
	private TrasactionTypeRepo typeRepo;
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TransactionHistoryRepo historyRepo;
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
    private ExcelGenerator excel;
	static String SHEET = "Mutasi";
	static String[] HEADERp = { "DATE",  "AMOUNT",  "TYPE","ACCOUNT NAME"  };
		 

//insert Type by user
	@RequestMapping(value = "/saveUserType", method = RequestMethod.POST)
	public ResponseEntity<?> saveUserType(@RequestBody JsonNode userType, @AuthenticationPrincipal UserDetails details)
			throws Exception {
		TransactionType transactionType = new TransactionType();
		transactionType.setTransactioncode(userType.get("transactioncode").asText());
		transactionType.setTransactionname(userType.get("transactionname").asText());
		List<Users> lisUsers = new ArrayList<>();
		for (JsonNode jsonNode : userType.get("userid")) {
			System.out.println("id :" + jsonNode.get("id").asLong());
			Users dataUsers = userRepo.findByid(jsonNode.get("id").asLong());
			lisUsers.add(dataUsers);

		}
		transactionType.setUsers(lisUsers);

		return ResponseEntity.ok(typeRepo.save(transactionType));

	}

//inser Transaction
	@RequestMapping(value = "/saveTransaction", method = RequestMethod.POST)
	public ResponseEntity<?> saveUserTransaction(@RequestBody JsonNode transaction,
			@AuthenticationPrincipal UserDetails details) throws Exception {

		TrasactionHistory history = new TrasactionHistory();
		history.setAmount(transaction.get("amount").asInt());
		history.setActivitydate(new Timestamp(System.currentTimeMillis()));

		Users dataUser = userRepo.findByUsername(details.getUsername());
		TransactionType transactionType = typeRepo.findByid(transaction.get("typeid").asLong());
		history.setUser(dataUser);
		history.setTransactiontype(transactionType);

		return ResponseEntity.ok(historyRepo.save(history));

	}

	@RequestMapping(value = "/checkTransaction", method = RequestMethod.POST)
	public ResponseEntity<?> CheckUserTransaction(@RequestBody JsonNode transaction,
			@AuthenticationPrincipal UserDetails details) throws Exception {

		Users dataUser = userRepo.findByUsername(details.getUsername());
	 
		List<TrasactionHistory> historyTrasactionHistory = historyRepo.findSearchDate(dataUser.getId(),transaction.get("startDate").asText(), transaction.get("endDate").asText());

		List<History> history = new ArrayList<>();
		SimpleDateFormat formatNowDay = new SimpleDateFormat("dd-MM-yyyy");
		for (TrasactionHistory tr : historyTrasactionHistory) {
			History  datahis= new History();
			Date date = tr.getActivitydate();
		
			String codate = formatNowDay.format(date);
			datahis.setActivitydate(codate);
			datahis.setAmount(tr.getAmount());
			datahis.setType(tr.getTransactiontype().getTransactionname());
			history.add(datahis);
		 }
		
		  Map<String, List<History>> groupByType = 
				  history.stream().collect(Collectors.groupingBy(History::getActivitydate));

		   
 
		return ResponseEntity.ok(groupByType);

	}

	

	@GetMapping("/download/excel")
	public ResponseEntity<InputStreamResource> downloadCSV(@RequestParam("startdate") String startdate,@RequestParam("enddate") String enddate,@RequestParam("User") String User,
			HttpServletResponse response) throws IOException, ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy");
		 
		String filename = "Mutasi" +dateFormat.format(new Date()) + ".xlsx";
		InputStreamResource file = new InputStreamResource(load(startdate,enddate,User));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(file);
	}

	
	private InputStream load(String periode, String enddate, String user) throws ParseException {
		// TODO Auto-generated method stub

		Users dataUser = userRepo.findByUsername(user);
		 
		List<TrasactionHistory> historyTrasactionHistory = historyRepo.findSearchDate(dataUser.getId(),periode, enddate);

		SimpleDateFormat input = new SimpleDateFormat("MM-yyyy");
		String dateValue = input.format(new Date());
		ByteArrayInputStream in = generateToExcel(dateValue,historyTrasactionHistory);
		return in;
	}

	private ByteArrayInputStream generateToExcel(String dateValue, List<TrasactionHistory> historyTrasactionHistory) {
		// TODO Auto-generated method stub
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy");
			String Dates = dateFormat.format(new Date());
			Sheet sheet = workbook.createSheet(SHEET + " -" + Dates);
//			Title one
			Row rowsTitleOne = sheet.createRow((short) 1);
			Cell cellsTitleOne = rowsTitleOne.createCell((short) 0);
			cellsTitleOne.setCellValue("MUTASI Bank ");
		
			cellsTitleOne.getSheet().addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
			CellStyle styleOne = workbook.createCellStyle();
			Font fontOne = workbook.createFont();
			fontOne.setFontName("Calibri");
			fontOne.setBold(false);
			fontOne.setColor(HSSFColor.BLACK.index);
			fontOne.setFontHeightInPoints((short) 16);
			styleOne.setFont(fontOne);
			styleOne.setAlignment(HorizontalAlignment.LEFT);
			styleOne.setVerticalAlignment(VerticalAlignment.CENTER);
			cellsTitleOne.setCellStyle(styleOne);
////	    	Title two
			Row rowtwo = sheet.createRow((short) 2);
			Cell celltwo = rowtwo.createCell((short) 0);
			celltwo.setCellValue("BULAN " + Dates);
			celltwo.getSheet().addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
//
			CellStyle styletwo = workbook.createCellStyle();
			Font fonttwo = workbook.createFont();
			fonttwo.setFontName("Calibri");
			fonttwo.setBold(false);
			fonttwo.setColor(HSSFColor.BLUE.index);
			fonttwo.setFontHeightInPoints((short) 16);
			styletwo.setFont(fonttwo);
			styletwo.setAlignment(HorizontalAlignment.LEFT);
			styletwo.setVerticalAlignment(VerticalAlignment.CENTER);
			celltwo.setCellStyle(styletwo);

			CellStyle styleheaders = workbook.createCellStyle();
			Font fontheaders = workbook.createFont();
			fontheaders.setFontName("Calibri");
			fontheaders.setBold(true);
			fontheaders.setColor(HSSFColor.RED.index);
			fontheaders.setFontHeightInPoints((short) 11);
			styleheaders.setAlignment(HorizontalAlignment.CENTER);
			styleheaders.setVerticalAlignment(VerticalAlignment.CENTER);
			styleheaders.setBorderTop(BorderStyle.MEDIUM);
			styleheaders.setBorderBottom(BorderStyle.MEDIUM);
			styleheaders.setBorderLeft(BorderStyle.MEDIUM);
			styleheaders.setBorderRight(BorderStyle.MEDIUM);
			styleheaders.setFont(fontheaders);
			styleheaders.setFillForegroundColor(HSSFColor.YELLOW.index);
			styleheaders.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			Row headerRowParentone = sheet.createRow((short) 3);
			for (int col = 0; col < HEADERp.length; col++) {
//			 
				Cell cell = headerRowParentone.createCell(col);
				cell.setCellValue(HEADERp[col]);
				cell.setCellStyle(styleheaders);

			}
			CellStyle styledetail = workbook.createCellStyle();
			SimpleDateFormat dateFormattabel = new SimpleDateFormat("dd-MM-yyyy");
			int rowIdx = 4;
			for (TrasactionHistory rowdata : historyTrasactionHistory) {
				Row row = sheet.createRow(rowIdx++);
				Date date = rowdata.getActivitydate();
				
				row.createCell(0).setCellValue(dateFormattabel.format(date));
				row.createCell(1).setCellValue(rowdata.getAmount());
				row.createCell(2).setCellValue(rowdata.getTransactiontype().getTransactionname());
				row.createCell(3).setCellValue(rowdata.getUser().getUsername());
				
				 
				 
			}
			
			
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());

		} catch (IOException e) {
			throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
		}
	}
	 

	 
}
