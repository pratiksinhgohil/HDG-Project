package com.pcc.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class FileValidator implements method to validate CSV files
 */
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
	public boolean skipValidation = false;

	/**
	 * Validate CSV files.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void validateFiles() throws IOException {
		log.info("Starting file validation");

		File folder = new File(Application.APP_CONFIG.getCurrentHourFolder());
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {

			String fileName = file.getName();
			if (file.isFile() && fileName.endsWith(".csv")) {
				log.info("Validating file : " + fileName + " Size >>>>" + file.length());

				if (file.length() == 0) {
					Application.APP_CONFIG.getUploadProcessingStatus().put(fileName, "This file is empty");
				} else {
					try {

						List<LinkedHashMap<String, String>> allRecords = readDataLineByLine(file.getCanonicalPath(),
								fileName);
						ValidatedData data = validateAllRecords(allRecords, fileName);

						if (data.validRecords.size() > 0) {
							validFileCounter++;
							log.info("File {} have {} valid records", fileName, data.validRecords.size());
						}
						if (data.invalidRecords.size() > 0) {
							log.info("File {} have {} invalid records", fileName, data.invalidRecords.size());
							invalidFileCounter++;
						}
						writeCSV(fileName, data.validRecords, true);
						writeCSV(fileName, data.invalidRecords, false);
						if (data.validRecords.isEmpty() && data.invalidRecords.isEmpty()) {
							Application.APP_CONFIG.getUploadProcessingStatus().put(fileName,
									"File had not any records after validation may be special character in file or format issue or file is empty");
						}
						if (data.validRecords.isEmpty() && data.invalidRecords.size() > 0) {
							Application.APP_CONFIG.getUploadProcessingStatus().put(fileName,
									"File had not any records after validation check email");
						}

					} catch (Exception e) {
						Application.APP_CONFIG.getUploadProcessingStatus().put(fileName, "Error while processing file");
					}
				}

			}
		}
	}

	/**
	 * Read data line by line.
	 *
	 * @param fileNameWithPath the file
	 * @param fileName
	 * @return the list
	 */
	public static List<LinkedHashMap<String, String>> readDataLineByLine(String fileNameWithPath, String fileName) {
		List<LinkedHashMap<String, String>> allRecords = new ArrayList<>();
		List<List<String>> fileRecords = new ArrayList<>();

		FileReader filereader = null;
		CSVReader csvReader = null;

		try {
			log.info("Reading file to validate {}", fileNameWithPath);
			filereader = new FileReader(fileNameWithPath);
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
			Application.APP_CONFIG.getUploadProcessingStatus().put(fileName,
					"Error while reading file" + e.getMessage());
		}
		return allRecords;
	}

	/**
	 * Validate all records.
	 *
	 * @param allRecords the all records
	 * @param fileName   the file name
	 * @return the validated data
	 */
	public ValidatedData validateAllRecords(List<LinkedHashMap<String, String>> allRecords, String fileName) {

		if (skipValidation) {
			return new ValidatedData(allRecords, new ArrayList<LinkedHashMap<String, String>>());
		}

		int fiscalMonthCutoff = Integer
				.valueOf(Application.APP_CONFIG.getConfigProps().getProperty("fiscal.cutoff.day", "15")).intValue();
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int monthValue = now.getMonthValue();
		boolean isLastMonthCutoffOver = fiscalMonthCutoff < now.getDayOfMonth();
		String month = String.valueOf(monthValue);
		List<LinkedHashMap<String, String>> validRecords = new ArrayList<>();
		List<LinkedHashMap<String, String>> invalidRecords = new ArrayList<>();
		label: for (int i = 0; i < allRecords.size(); i++) {
			LinkedHashMap<String, String> valid = new LinkedHashMap<>();
			LinkedHashMap<String, String> inValid = new LinkedHashMap<>();
			for (int j = 0; j < allRecords.get(i).size(); j++) {
				boolean isAnyFieldInvalid = false;
				StringBuffer invalidRecordMessage = new StringBuffer();
				String vencode = allRecords.get(i).getOrDefault(VEN_CODE, "");
				if (vencode.isEmpty()) {
					invalidRecordMessage.append("Invalid VenCode ");
					isAnyFieldInvalid = true;
				}

				// Invoice number must not blank and should not have more than 17 chars
				String invNum = allRecords.get(i).getOrDefault(INV_NUM, "");
				if (invNum.isBlank() || invNum.length() > 17) {
					 invalidRecordMessage.append(" Invalid InvNum ");
					isAnyFieldInvalid = true;
				}

				// Invoice date must not empty
				if (allRecords.get(i).getOrDefault(INV_DATE, "").isBlank()) {
				    invalidRecordMessage.append(" Invalid InvDate  ");
					isAnyFieldInvalid = true;
				}
 

				String fiscalMonth = allRecords.get(i).getOrDefault(FISCAL_MONTH, "0");

				// The TransactionAmount must not empty
				if (allRecords.get(i).getOrDefault(TRANSACTION_AMOUNT, "").isBlank()) {
					invalidRecordMessage.append(" Invalid TransactionAmount ");
					isAnyFieldInvalid = true;
				}

				// InvoiceAmount is not allowed as blank
				if (allRecords.get(i).getOrDefault(INVOICE_AMOUNT, "").isBlank()) {
					invalidRecordMessage.append(" Invalid InvoiceAmount ");
					isAnyFieldInvalid = true;
				}
				// AccountNum is not allowed as blank
				if (allRecords.get(i).getOrDefault(ACCOUNT_NUM, "").isBlank()) {
					invalidRecordMessage.append(" Invalid AccountNum ");
					isAnyFieldInvalid = true;
				}
				if (isAnyFieldInvalid) {
					inValid = allRecords.get(i);
					inValid.put(MESSAGE, invalidRecordMessage.toString());
					invalidRecords.add(inValid);
					continue label;
				} else {
					if (allRecords.get(i).getOrDefault(FISCAL_YEAR,String.valueOf(year)).matches(String.valueOf(year - 1))) {
						if (isLastMonthCutoffOver || Integer.valueOf(fiscalMonth).intValue() <= 11) {
							allRecords.get(i).replace(FISCAL_MONTH, month);
							allRecords.get(i).replace(FISCAL_YEAR, String.valueOf(year));
						}
					} else {
						if ((fiscalMonth.matches(String.valueOf(monthValue - 1)))) {
							if (isLastMonthCutoffOver) {
								allRecords.get(i).replace(FISCAL_MONTH, month);
							}
						} else {
							allRecords.get(i).replace(FISCAL_MONTH, month);
						}
					}
				}
				// Line description not allowed in upload file so we are replacing in file and
				// email will be sent with all line desc
				String lineDescription = allRecords.get(i).getOrDefault(LINE_DESCRIPTION, "");
				if (!lineDescription.isBlank()) {
					Application.APP_CONFIG.getLineDescriptionFiles().computeIfAbsent(fileName, k -> new ArrayList<>())
							.add("<tr><td>" + vencode + "</td><td>" + invNum + "</td><td>" + lineDescription
									+ "</td></tr>");
					allRecords.get(i).replace(LINE_DESCRIPTION, "");
				}

				valid = allRecords.get(i);
			}
			validRecords.add(valid);
		}
		return new ValidatedData(validRecords, invalidRecords);
	}

	/**
	 * The Class ValidatedData.
	 */
	public class ValidatedData {
		List<LinkedHashMap<String, String>> validRecords;
		List<LinkedHashMap<String, String>> invalidRecords;

		/**
		 * Instantiates a new validated data.
		 *
		 * @param validRecords   the valid records
		 * @param invalidRecords the invalid records
		 */
		public ValidatedData(List<LinkedHashMap<String, String>> validRecords,
				List<LinkedHashMap<String, String>> invalidRecords) {
			this.validRecords = validRecords;
			this.invalidRecords = invalidRecords;
		}
	}

	/**
	 * Write CSV.
	 *
	 * @param fileName  the file name
	 * @param records   the records
	 * @param validData the valid data
	 */
	public static void writeCSV(String fileName, List<LinkedHashMap<String, String>> records, boolean validData) {
		String filePath = (validData ? Application.APP_CONFIG.getCurrentHourFolderValidFiles()
				: Application.APP_CONFIG.getCurrentHourFolderInValidFiles()) + "//" + fileName; // CURRENT_HOUR_FOLDER_IN_VALID_FILES
		String message = validData ? "Valid records are empty" : "Invalid records are empty";

		if (CollectionUtils.isEmpty(records)) {
			log.info(message);
		} else {

			try {
				createFile(filePath);
				FileWriter outputfile = new FileWriter(filePath);
				CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
				String[] data = records.get(0).keySet().toArray(new String[0]);
				writer.writeNext(data);
				for (int i = 0; i < records.size(); i++) {
					data = records.get(i).values().toArray(new String[0]);
					writer.writeNext(data);
				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				log.info("Error while writng CSV file in FileValidator");
				e.printStackTrace();
				Application.APP_CONFIG.getUploadProcessingStatus().put(fileName,
						"Error while writng CSV file in FileValidator " + e.getMessage());
			}
		}
	}

	/**
	 * Creates the file.
	 *
	 * @param path the path
	 */
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
		} catch (Exception e) {
			log.info("Error while creating file in FileValidator");
			e.printStackTrace();
		}
	}

	/**
	 * Hash invalid files.
	 *
	 * @return the int
	 */
	public int hashInvalidFiles() {
		return invalidFileCounter;
	}

	/**
	 * Hash valid files.
	 *
	 * @return the int
	 */
	public int hashValidFiles() {
		return validFileCounter;
	}

}
