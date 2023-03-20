package com.pcc.utils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.internet.InternetAddress;

import lombok.Data;

@Data
public class AppConfig {
	private Properties configProps;
	private LocalDateTime currentTime;
	private String currentHour;
	private String currentHourFolder;
	private String currentHourFolderValidFiles;
	private String currentHourFolderInValidFiles;
	private String appBasePath;
	private String errorReportFilesPath;
	private boolean anyValidFile;
	private String emailSender;
	private List<InternetAddress> emailReceiver;
	private ConcurrentHashMap<String, List<String>> lineDescriptionFiles = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> uploadProcessingStatus = new ConcurrentHashMap<>();
	private Set<String> exceptionReports = new HashSet<>();
	private Properties hdgPccCodeMap = new Properties();
}
