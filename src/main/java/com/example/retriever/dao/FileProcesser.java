package com.directv.edm.dao;

//import static com.directv.edm.constant.ContentConstants.DATA_PATH;
import static com.directv.edm.constant.ContentConstants.DELTA_DIR;
import static com.directv.edm.constant.ContentConstants.FILE_VERY_VERY_LARGE;
import static com.directv.edm.constant.ContentConstants.FULL_PRFIX;
import static com.directv.edm.constant.ContentConstants.OUTPUT_PATH;
import static com.directv.edm.constant.ContentConstants.PREFIX_DELTAL;
import static com.directv.edm.constant.ContentConstants.RESTART_PRFIX;
//import static com.directv.edm.constant.ContentConstants.TMSID_DATA_PATH;
import static com.directv.edm.util.Utils.byteToString;
import static com.directv.edm.util.Utils.getCollectionAtIndext;
import static com.directv.edm.util.Utils.getDateFormat;
import static com.directv.edm.util.Utils.getElement;
import static com.directv.edm.util.Utils.randInt;
import static com.directv.edm.util.Utils.stringToByte;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

public class FileProcesser {
	private OutputStreamWriter osw;
	private Scanner scanner;
	private static String fileName;

	@Value("${dls.dir}")
	private String dlsDir;

	private String outDir;

	private String prefixDeltal;

	@Value("${data.dir}")
	private String dataDir;
	
	@Value("${tmsid.dir}")
	private String tmsIdDir;
	private String deltaPath;

	private List<Set<String>> dataSet = new ArrayList<Set<String>>();

	public FileProcesser() {
		// dlsDir = DLS_DIR;
//		dataPath = DATA_PATH;
		outDir = OUTPUT_PATH;
//		tmsIdDir = TMSID_DATA_PATH;
		deltaPath = DELTA_DIR;

		fileName = FILE_VERY_VERY_LARGE;
		// fileName = getFullFileName(dlsDir);
		prefixDeltal = PREFIX_DELTAL;

		readDatSet();
	}

	public static void main(String[] args) {
//		getFullFileName(DLS_DIR);
	}

	private static String getFullFileName(String fileDir) {

		String fName = "secondary-pgwslisting_full-20150801_103659_544.xml";
		try {
			File dir = new File(fileDir);
			String fileNames[] = dir.list();

			if (fileNames != null) {
				Arrays.sort(fileNames);
				int index = Arrays.binarySearch(fileNames, fName);
				System.out.println(index);
				for (String str : fileNames) {
					if (str.startsWith(FULL_PRFIX) && str.indexOf(".gz") < 0)
						System.out.println(fName);
					else if (str.startsWith(RESTART_PRFIX)
							&& str.indexOf(".gz") < 0)
						System.out.println(fName);
					// long ruleFileTimestamp = getRuleFileTimestamp(fileName);
					// if (ruleFileTimestamp > lastRuleFileTimestamp) {
					// lastRuleFileTimestamp = ruleFileTimestamp;
					// return fileName;
					// }
				}
			}
		} catch (Exception e) {
			System.out.printf("Error reading full file: %s", e.getMessage());
			// logger.error("Error reading rule file.", e);
		}
		return fName;
	}

	// public String getNextRuleFileName() {
	// try {
	// File dir = new File(ruleFilesDir);
	// String fileNames[] = dir.list();
	// if (fileNames != null) {
	// Arrays.sort(fileNames, fileNameComparator);
	// for (String fileName : fileNames) {
	// long ruleFileTimestamp = getRuleFileTimestamp(fileName);
	// if (ruleFileTimestamp > lastRuleFileTimestamp) {
	// lastRuleFileTimestamp = ruleFileTimestamp;
	// return fileName;
	// }
	// }
	// }
	// } catch (Exception e) {
	// logger.error("Error reading rule file.", e);
	// }
	//
	// return null;
	// }

	protected void readDatSet() {
		String[] list = new File(tmsIdDir).list();
		if (list == null || list.length == 0)
			return;

		int len = list.length;
		int index;
		for (int i = 0; i < len; i++) {
			index = stringToByte(list[i].substring(0, 2));
			if (0 < index && index < 16) {
				Set<String> lineSet = getLineSet(tmsIdDir + list[i]);
				dataSet.add(lineSet);
			}
		}
	}

	/**
	 * This method write a string to file
	 * 
	 * @param destinationFileName
	 * @param string
	 * @throws IOException
	 */
	public void writeToFile(String destinationFileName, String string,
			boolean isDeltaFile) throws IOException {
		File destinationFile = new File(destinationFileName);

		if (!destinationFile.exists()) {
			destinationFile.createNewFile();
		} else {
			destinationFile.delete();
			destinationFile.createNewFile();
		}

		osw = new OutputStreamWriter(new FileOutputStream(destinationFile),
				"UTF-8");
		osw.write(string);

		if (osw != null)
			osw.close();
	}

	/**
	 * The method get line set from a file
	 * 
	 * @param file
	 */
	public Set<String> getLineSet(String destinationFileName) {
		Set<String> lineSet = new HashSet<String>();

		try {
			scanner = new Scanner(new File(destinationFileName));

			String lineText;
			while (scanner.hasNextLine()) {
				lineText = scanner.nextLine();
				if (lineText != null && !lineText.trim().isEmpty()) {
					lineSet.add(lineText.trim());
				}
			}
			if (scanner != null)
				scanner.close();
		} catch (FileNotFoundException e) {
			System.out.format("File Not Found Exception %s\n", e.getMessage());
		}

		return lineSet;
	}

	public String writeDeltaFile(String inFile, StringBuilder strBuilder,
			String outFile) {
		File file = null;
		Scanner scanner = null;
		StringBuilder tempBuilder = new StringBuilder();
		boolean isLineFirst = true;
		try {
			file = new File(deltaPath + "//" + inFile);
			scanner = new Scanner(file);

			String line;
			while (scanner.hasNext()) {
				line = scanner.nextLine();
				if ("<!-- TODO -->".equals(line.trim())) {
					tempBuilder.append("\n").append(strBuilder);
				} else {
					if (isLineFirst) {
						tempBuilder.append(line);
						isLineFirst = false;
					} else
						tempBuilder.append("\n").append(line);
				}
			}

			writeToFile(outDir + prefixDeltal + getDateFormat() + outFile,
					tempBuilder.toString(), false);
		} catch (FileNotFoundException e) {
			System.out.format("File not found exception: %s\n", e.getMessage());
		} catch (IOException e) {
			System.out.format("IO Exception: %s\n", e.getMessage());
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		return tempBuilder.toString();
	}

	public String getFilePath(byte typeContent) {
		if (typeContent <= 0 || typeContent > dataSet.size())
			return null;

		// 1. Get file dir
		String text = byteToString(typeContent);
		String[] arrDir = new File(dataDir).list();
		String fileDir = dataDir + getElement(arrDir, text) + "\\";

		// 2. Random tmsId
		Set<String> set = dataSet.get(typeContent - 1);
		int ranInt = randInt(0, set.size() - 1);
		String tmsId = getCollectionAtIndext(set, ranInt);

		// 3. Get file name by tmsid
		String[] arrFile = new File(fileDir).list();
		String filePath = fileDir + getElement(arrFile, tmsId);

		return filePath;
	}

	public void setFullDirectory(String dlsDir) {
		this.dlsDir = dlsDir;
	}

	public void setOuputDirectory(String ouputDirectory) {
		this.outDir = ouputDirectory;
	}

	public File getFullFile() {
		return new File(dlsDir + fileName);
	}

	public boolean writeFullFile(String str) throws IOException {
		writeToFile(outDir + fileName, str, false);
		return true;
	}

	public String getFilePath() {
		return outDir + fileName;
	}
}
