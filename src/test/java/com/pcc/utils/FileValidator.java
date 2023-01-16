package com.pcc.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileValidator {

	private static final String DESCRIPTION = "Description";
	private static final String ID1099_AMOUNT = "ID1099Amount";
	private static final String LINE_DESCRIPTION = "LineDescription";
	private static final String ACCOUNT_NUM = "AccountNum";
	private static final String INV_DATE = "InvDate";
	private static final String INVOICE_AMOUNT = "InvoiceAmount";
	private static final String TRANSACTION_AMOUNT = "TransactionAmount";
	private static final String INV_NUM = "InvNum";
	private static final String VEN_CODE = "VenCode";
	private static final String FISCAL_MONTH = "FiscalMonth";
	private static final String FISCAL_YEAR = "FiscalYear";
	private int invalidFileCounter = 0;
	private int validFileCounter = 0;

	public static String[] CSV_HEADERS = { VEN_CODE, INV_NUM, INV_DATE, FISCAL_YEAR, FISCAL_MONTH, TRANSACTION_AMOUNT,
			ID1099_AMOUNT, INVOICE_AMOUNT, DESCRIPTION, ACCOUNT_NUM, LINE_DESCRIPTION };
	Calendar cal = Calendar.getInstance();
	int current_year = cal.get(Calendar.YEAR);
	int current_month = cal.get(Calendar.MONTH) + 1;

	public void validateFiles() throws IOException {
		log.info("Starting file validation");

		File folder = new File(Application.CURRENT_HOUR_FOLDER);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			
			if (file.isFile() && file.getName().endsWith(".csv")) {
				log.info("Validating file : " + file.getName());
				List<HashMap<String, String>> allRecords = readDataLineByLine(file.getCanonicalPath());
				ValidatedData data = validation(allRecords);
				
				if (data.validRecords.size() > 0) {
					validFileCounter++;
					log.info("File {} have {} valid records", file.getName(), data.validRecords.size());
				}
				if (data.invalidRecords.size() > 0) {
					log.info("File {} have {} invalid records", file.getName(), data.invalidRecords.size());
					invalidFileCounter++;
				}				
				writeValidData(file.getName(), data.validRecords, true);
				writeValidData(file.getName(), data.invalidRecords, false);				
			}
		}
	}

	public static List<HashMap<String, String>> readDataLineByLine(String file) {
		List<HashMap<String, String>> allRecords = new ArrayList<>();
		List<List<String>> list2 = new ArrayList<>();

		FileReader filereader = null;
		CSVReader csvReader = null;

		try {
			log.info("Reading file to validate {}", file);
			filereader = new FileReader(file);
			csvReader = new CSVReader(filereader);
			String[] nextRecord;
			List<String> header = new ArrayList<>();
			while ((nextRecord = csvReader.readNext()) != null) {
				List<String> list1 = new ArrayList<>();

				if (csvReader.getLinesRead() == 1) {
					for (String cell : nextRecord) {
						header.add(cell);
					}
				} else {
					for (String cell : nextRecord) {
						list1.add(cell);
					}
					if (!list1.isEmpty()) {
						list2.add(list1);
					}
				}
			}
			for (int i = 0; i < list2.size(); i++) {
				HashMap<String, String> records = new HashMap<>();
				for (int j = 0; j < header.size(); j++) {
					records.put(header.get(j), list2.get(i).get(j));
				}
				allRecords.add(records);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allRecords;
	}

	public ValidatedData validation(List<HashMap<String, String>> allRecords) {
		LocalDateTime now = LocalDateTime.now();

		List<HashMap<String, String>> validRecords = new ArrayList<>();
		List<HashMap<String, String>> invalidRecords = new ArrayList<>();
		label: for (int i = 0; i < allRecords.size(); i++) {
			HashMap<String, String> valid = new HashMap<>();
			HashMap<String, String> inValid = new HashMap<>();
			for (int j = 0; j < allRecords.get(i).size(); j++) {
				if (allRecords.get(i).get(FISCAL_YEAR).isEmpty()) {
					allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(now.getYear()));
				}
				if (!allRecords.get(i).get(FISCAL_YEAR).matches(String.valueOf(now.getYear()))) {
					if (allRecords.get(i).get(FISCAL_MONTH).matches("1")) {
						allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(now.getYear() - 1));
					} else {
						allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(now.getYear()));
					}
				}
				if ((allRecords.get(i).get(FISCAL_MONTH).matches(String.valueOf(now.getMonthValue())))
						|| (allRecords.get(i).get(FISCAL_MONTH).matches(String.valueOf(now.getMonthValue() - 1)))) {

				} else {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}

				if (allRecords.get(i).get(FISCAL_YEAR).matches(String.valueOf(now.getYear() - 1))
						&& (now.getMonthValue() == 1)) {
					if (allRecords.get(i).get(FISCAL_MONTH).matches(String.valueOf(now.getMonthValue()))
							|| allRecords.get(i).get(FISCAL_MONTH).matches("12")) {
					} else {
						inValid = allRecords.get(i);
						invalidRecords.add(inValid);
						continue label;
					}
				}
				if (allRecords.get(i).get(VEN_CODE).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(INV_NUM).length() > 17) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(TRANSACTION_AMOUNT).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(ID1099_AMOUNT).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(INVOICE_AMOUNT).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(INV_DATE).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (allRecords.get(i).get(ACCOUNT_NUM).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				if (!allRecords.get(i).get(LINE_DESCRIPTION).isEmpty()) {
					inValid = allRecords.get(i);
					invalidRecords.add(inValid);
					continue label;
				}
				valid = allRecords.get(i);
			}
			validRecords.add(valid);
		}
		return new ValidatedData(validRecords, invalidRecords);
	}

	public class ValidatedData {
		List<HashMap<String, String>> validRecords;
		List<HashMap<String, String>> invalidRecords;

		public ValidatedData(List<HashMap<String, String>> validRecords, List<HashMap<String, String>> invalidRecords) {
			this.validRecords = validRecords;
			this.invalidRecords = invalidRecords;
		}
	}

	public static void writeValidData(String fileName, List<HashMap<String, String>> records, boolean validData) {
		String filePath = null;
		String message = null;
		
		if (validData) {
			filePath = Application.CURRENT_HOUR_FOLDER_VALID_FILES + "//" + fileName;
			message = "Valid records are empty";
		} else {
			filePath = Application.CURRENT_HOUR_FOLDER_IN_VALID_FILES + "//" + fileName;
			message = "Invalid records are empty";
		}

		if (records.isEmpty()) {
			log.info(message);
		} else {
			
			try {
				createFile(filePath);
				FileWriter outputfile = new FileWriter(filePath);
				CSVWriter writer = new CSVWriter(outputfile);
				String[] data = records.get(0).keySet().toArray(new String[0]);
				writer.writeNext(data);
				for (int i = 0; i < records.size(); i++) {
					data = records.get(i).values().toArray(new String[0]);
					writer.writeNext(data);
				}
				writer.close();
			} catch (IOException e) {
				log.info("Exception error");
				e.printStackTrace();
			}
		}
	}

	public int hashInvalidFiles() {
		return invalidFileCounter;
	}

	public int hashValidFiles() {
		return validFileCounter;
	}

	public static void createFile(String path) {
		try {
			log.info("Creating file {}",path);
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
