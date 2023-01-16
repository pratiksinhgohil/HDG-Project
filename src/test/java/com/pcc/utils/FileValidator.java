package com.pcc.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.pcc.app.Application;

public class FileValidator {

	private int invalidFileCounter = 0;
	private int validFileCounter = 0;
	public static String[] CSV_HEADERS = { "VenCode", "InvNum", "InvDate", "FiscalYear", "FiscalMonth",
			"TransactionAmount", "ID1099Amount", "InvoiceAmount", "Description", "AccountNum", "LineDescription" };

	public void validateFiles() {
		System.out.println("Validating files");

		File folder = new File(Application.CURRENT_HOUR_FOLDER);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			System.out.println("Validating file " + file.getName());
			if(file.isFile() && file.getName().endsWith(".csv")) {
				readCsvFile(file.getName());	
			}			
		}
	}

	private void readCsvFile(String inputFile) {

		try {
			FileReader fileReader = new FileReader(Application.CURRENT_HOUR_FOLDER+"//"+inputFile);
			CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();

			List<String[]> allData = csvReader.readAll();
			System.out.println("Read csv file..");
			validateRecords(inputFile, allData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateRecords(String fileName, List<String[]> record) {

		/*
		 * List<String[]> validData = new ArrayList<>(); List<String[]> invalidData =
		 * new ArrayList<>();
		 * 
		 * Calendar cal = Calendar.getInstance(); int current_year =
		 * cal.get(Calendar.YEAR); int current_month = cal.get(Calendar.MONTH) + 1; //
		 * here month number are 0 based that's why added 1
		 * 
		 * for (String[] row : record) { int count = 0;
		 * 
		 * for (int i = 0; i < row.length; i++) {
		 * 
		 * if (row[i] == row[0] && row[0].isEmpty()) { break; } if (row[i] == row[1] &&
		 * row[1].isEmpty() || row[1].length() > 17) { break; } if (row[i] == row[2] &&
		 * row[2].isEmpty()) { break; } if (row[i] == row[3] && row[3].isEmpty() &&
		 * row[3] != null) { if (row[3].matches(String.valueOf(current_year))) { row[3]
		 * = String.valueOf(current_year); } } if (row[i] == row[4]) {
		 * 
		 * if (row[4] == null || row[4].isEmpty()) { row[4] =
		 * String.valueOf(current_year); } else if (current_month == 1) { if
		 * (!row[4].matches(String.valueOf(current_month))) { break; } else { if
		 * (row[4].matches(String.valueOf(12))) { row[3] = String.valueOf(current_year -
		 * 1); } else { row[3] = String.valueOf(current_year); row[4] =
		 * String.valueOf(current_month); } } } else if (current_month != 1) { if
		 * (row[4].matches(String.valueOf(current_month)) ||
		 * row[4].matches(String.valueOf(current_month - 1))) { } } } if (row[i] ==
		 * row[5] && row[5].isEmpty()) { break; } if (row[i] == row[7] &&
		 * row[7].isEmpty()) { break; } if (row[i] == row[9] && row[9].isEmpty()) {
		 * break; } if (row[i] == row[10] && !row[10].isEmpty()) { break; } else {
		 * count++; } } if (count == 11) { validData.add(row); } else if (count < 11) {
		 * invalidData.add(row); } } System.out.println("Total valid record: " +
		 * validData.size()); System.out.println("Total invalid record: " +
		 * invalidData.size());
		 */
		writeInCsv(fileName, record, null);
	}

	private void writeInCsv(String fileName, List<String[]> validData, List<String[]> invalidData) {

		try {

			if (CollectionUtils.isNotEmpty(validData)) {
				createFile(Application.CURRENT_HOUR_FOLDER_VALID_FILES + "//" + fileName);
				CSVWriter validRecordWriter = new CSVWriter(
						new FileWriter(Application.CURRENT_HOUR_FOLDER_VALID_FILES + "//" + fileName));

				validRecordWriter.writeNext(CSV_HEADERS);
				validRecordWriter.writeAll(validData);
				validRecordWriter.flush();
				validRecordWriter.close();
				System.out.println("Data entered into ValidCsv file");
				validFileCounter++;

			}
			if (CollectionUtils.isNotEmpty(invalidData)) {
				createFile(Application.CURRENT_HOUR_FOLDER_IN_VALID_FILES + "//" + fileName);
				CSVWriter invalidRecordWriter = new CSVWriter(
						new FileWriter(Application.CURRENT_HOUR_FOLDER_IN_VALID_FILES + "//" + fileName));

				List<String[]> opInValid = invalidData;
				invalidRecordWriter.writeNext(CSV_HEADERS);
				invalidRecordWriter.writeAll(opInValid);
				invalidRecordWriter.flush();
				invalidRecordWriter.close();
				System.out.println("Data entered into inValidCsv file");
				invalidFileCounter++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int hashInvalidFiles() {
		return invalidFileCounter;
	}

	public int hashValidFiles() {
		return validFileCounter;
	}

	public void createFile(String path) {
		try {
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
