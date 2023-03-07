package com.pcc.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileValidator {

	private static final String VEN_CODE = "VenCode";
	private static final String INV_NUM = "InvNum";
	private static final String INV_DATE = "InvDate";
	private static final String FISCAL_YEAR = "FiscalYear";
	private static final String FISCAL_MONTH = "FiscalMonth";
	private static final String TRANSACTION_AMOUNT = "TransactionAmount";
	private static final String ID1099_AMOUNT = "ID1099Amount";
	private static final String INVOICE_AMOUNT = "InvoiceAmount";
	private static final String DESCRIPTION = "Description";
	private static final String ACCOUNT_NUM = "AccountNum";
	private static final String LINE_DESCRIPTION = "LineDescription";
	private static final String MESSAGE = "ErrorMessage";

	private int invalidFileCounter = 0;
	private int validFileCounter = 0;

	public static String[] CSV_HEADERS = { VEN_CODE, INV_NUM, INV_DATE, FISCAL_YEAR, FISCAL_MONTH, TRANSACTION_AMOUNT,
			ID1099_AMOUNT, INVOICE_AMOUNT, DESCRIPTION, ACCOUNT_NUM, LINE_DESCRIPTION };
	Calendar cal = Calendar.getInstance();
	int current_year = cal.get(Calendar.YEAR);
	int current_month = cal.get(Calendar.MONTH) + 1;

	public boolean skipValidation = false;

	public void validateFiles() throws IOException {
		log.info("Starting file validation");

		File folder = new File(Application.CURRENT_HOUR_FOLDER);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {

			if (file.isFile() && file.getName().endsWith(".csv")) {
				log.info("Validating file : " + file.getName());
				List<LinkedHashMap<String, String>> allRecords = readDataLineByLine(file.getCanonicalPath());
				ValidatedData data = validateAllRecords(allRecords,file.getName());

				if (data.validRecords.size() > 0) {
					validFileCounter++;
					log.info("File {} have {} valid records", file.getName(), data.validRecords.size());
				}
				if (data.invalidRecords.size() > 0) {
					log.info("File {} have {} invalid records", file.getName(), data.invalidRecords.size());
					invalidFileCounter++;
				}
				writeCSV(file.getName(), data.validRecords, true);
				writeCSV(file.getName(), data.invalidRecords, false);
			}
		}
	}

	public static List<LinkedHashMap<String, String>> readDataLineByLine(String file) {
		List<LinkedHashMap<String, String>> allRecords = new ArrayList<>();
		List<List<String>> fileRecords = new ArrayList<>();

		FileReader filereader = null;
		CSVReader csvReader = null;

		try {
			log.info("Reading file to validate {}", file);
			filereader = new FileReader(file);
			csvReader = new CSVReader(filereader);
			String[] nextRecord;
			List<String> header = new ArrayList<>();
			while ((nextRecord = csvReader.readNext()) != null) {
				List<String> dataInRecord = new ArrayList<>();

				if (csvReader.getLinesRead() == 1) {
					for (String cell : nextRecord) {
						header.add(cell);
					}
				} else {
					for (String cell : nextRecord) {
						dataInRecord.add(cell);
					}
					if (!dataInRecord.isEmpty()) {
						fileRecords.add(dataInRecord);
					}
				}
			}
			for (int i = 0; i < fileRecords.size(); i++) {
				LinkedHashMap<String, String> records = new LinkedHashMap<>();
				for (int j = 0; j < header.size(); j++) {
					records.put(header.get(j), fileRecords.get(i).get(j));
				}
				allRecords.add(records);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allRecords;
	}

	public ValidatedData validateAllRecords(List<LinkedHashMap<String, String>> allRecords,String fileName) {

		if (skipValidation) {
			return new ValidatedData(allRecords, new ArrayList<LinkedHashMap<String, String>>());
		}

		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		List<LinkedHashMap<String, String>> validRecords = new ArrayList<>();
		List<LinkedHashMap<String, String>> invalidRecords = new ArrayList<>();
		label: for (int i = 0; i < allRecords.size(); i++) {
			LinkedHashMap<String, String> valid = new LinkedHashMap<>();
			LinkedHashMap<String, String> inValid = new LinkedHashMap<>();
			for (int j = 0; j < allRecords.get(i).size(); j++) {

				// VenCode must not empty
				if (allRecords.get(i).getOrDefault(VEN_CODE, "").isEmpty()) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid VenCode");
					invalidRecords.add(inValid);
					continue label;
				}

				// Invoice number must not blank and should not have more than 17 chars
				if (allRecords.get(i).getOrDefault(INV_NUM, "").isBlank()
						|| allRecords.get(i).getOrDefault(INV_NUM, "").length() > 17) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid InvNum");
					invalidRecords.add(inValid);
					continue label;
				}

				// Invoice date must not empty
				if (allRecords.get(i).getOrDefault(INV_DATE, "").isBlank()) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid InvDate");
					invalidRecords.add(inValid);
					continue label;
				}

				// FISCAL_YEAR must not be null or empty and should be valid

				if (allRecords.get(i).getOrDefault(FISCAL_YEAR, "").isEmpty()) {
					allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(year));
				}
				if (!allRecords.get(i).getOrDefault(FISCAL_YEAR, "").matches(String.valueOf(year))) {
					if (allRecords.get(i).get(FISCAL_MONTH).matches("1")) {
						allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(year - 1));
					} else {
						allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(year));
					}
				}

				// FISCAL_MONTH Should be current or previous month and must not be null or
				// empty
				int monthValue = now.getMonthValue();
				String month = String.valueOf(monthValue);
				if ((allRecords.get(i).getOrDefault(FISCAL_MONTH, "").matches(month))
						|| (allRecords.get(i).getOrDefault(FISCAL_MONTH, "")
								.matches(String.valueOf(monthValue - 1)))) {

				} else {
					//inValid = allRecords.get(i);
					//inValid.put(MESSAGE, "Invalid fiscal month");
					//invalidRecords.add(inValid);
					//continue label;
					allRecords.get(i).replace(FISCAL_MONTH, month);
				}

				if (allRecords.get(i).get(FISCAL_YEAR).matches(String.valueOf(year - 1))
						&& (monthValue == 1)) {
					if (allRecords.get(i).get(FISCAL_MONTH).matches(month)
							|| allRecords.get(i).get(FISCAL_MONTH).matches("12")) {
					} else {
						//inValid = allRecords.get(i);
						//inValid.put(MESSAGE, "Invalid fiscal month/year info");
						//invalidRecords.add(inValid);
						//continue label;
						allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(year));
					}
				}

				// The TransactionAmount must not empty
				if (allRecords.get(i).getOrDefault(TRANSACTION_AMOUNT, "").isBlank()) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid TransactionAmount");
					invalidRecords.add(inValid);
					continue label;
				}

				// The ID1099Amount column allowed as blank
				// if (allRecords.get(i).get(ID1099_AMOUNT).isBlank()) {
				// inValid = allRecords.get(i);
				// inValid.put(MESSAGE, "Invalid ID1099Amount");
				// invalidRecords.add(inValid);
				// continue label;
				// }

				// InvoiceAmount is not allowed as blank
				if (allRecords.get(i).getOrDefault(INVOICE_AMOUNT, "").isBlank()) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid InvNum");
					invalidRecords.add(inValid);
					continue label;
				}
				// AccountNum is not allowed as blank
				if (allRecords.get(i).getOrDefault(ACCOUNT_NUM, "").isBlank()) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, "Invalid AccountNum");
					invalidRecords.add(inValid);
					continue label;
				}
				// Line description not allowed in upload file so we are replacing in file and email will be sent with all line desc
				if (!allRecords.get(i).getOrDefault(LINE_DESCRIPTION,"").isBlank()) {
					Application.LINE_DESC_FILE.computeIfAbsent(fileName, k -> new ArrayList<>()).add("<tr><td>"+allRecords.get(i).getOrDefault(INV_NUM, "")+"</td><td>"+allRecords.get(i).getOrDefault(LINE_DESCRIPTION, "")+"</td></tr>");
					allRecords.get(i).replace(LINE_DESCRIPTION, "");
				}
				valid = allRecords.get(i);
			}
			validRecords.add(valid);
		}
		return new ValidatedData(validRecords, invalidRecords);
	}

	public class ValidatedData {
		List<LinkedHashMap<String, String>> validRecords;
		List<LinkedHashMap<String, String>> invalidRecords;

		public ValidatedData(List<LinkedHashMap<String, String>> validRecords,
				List<LinkedHashMap<String, String>> invalidRecords) {
			this.validRecords = validRecords;
			this.invalidRecords = invalidRecords;
		}
	}

	public static void writeCSV(String fileName, List<LinkedHashMap<String, String>> records, boolean validData) {
		String filePath = (validData ? Application.CURRENT_HOUR_FOLDER_VALID_FILES
				: Application.CURRENT_HOUR_FOLDER_IN_VALID_FILES) + "//" + fileName;
		String message = validData ? "Valid records are empty" : "Invalid records are empty";

		if (CollectionUtils.isEmpty(records)) {
			log.info(message);
		} else {

			try {
				createFile(filePath);
				FileWriter outputfile = new FileWriter(filePath);
				CSVWriter writer =new CSVWriter(outputfile, ',',CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END);
				String[] data = records.get(0).keySet().toArray(new String[0]);
				writer.writeNext(data);
				for (int i = 0; i < records.size(); i++) {
					data = records.get(i).values().toArray(new String[0]);
					writer.writeNext(data);
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				log.info("Error while writng CSV file in FileValidator");
				e.printStackTrace();
			}
		}
	}

	public static void createFile(String path) {
		try {
			log.info("Creating file {}", path);
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			} else {
				file.createNewFile();
			}
		} catch (IOException e) {
			log.info("Error while creating file in FileValidator");
			e.printStackTrace();
		}
	}

	public int hashInvalidFiles() {
		return invalidFileCounter;
	}

	public int hashValidFiles() {
		return validFileCounter;
	}

}
