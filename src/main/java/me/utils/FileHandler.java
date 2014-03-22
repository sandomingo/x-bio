package me.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写工具
 * changed for html file processing
 *
 */
public class FileHandler {

	/**
	 * 读入流到字符串列表，默认不去除UTF-8的BOM文件头
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<String> readFileToList(InputStream is) throws IOException {
		return readFileToList(is, false);
	}
	
	/**
	 * 读入流到字符串列表
	 * @param is
	 * @param doesRemoveBomOfUTF8	是否去除UTF-8的BOM文件头
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<String> readFileToList(InputStream is, boolean doesRemoveBomOfUTF8)throws IOException {
		List<String> stringList = new ArrayList<String>();
		BufferedReader input = new BufferedReader(new InputStreamReader(is, "utf-8"));
		while (true) {
			String line = input.readLine();
			if (line == null) {
				break;
			}
			if (doesRemoveBomOfUTF8) {
				line = removeBOMofUTF8(line); // 去除UTF8文件的BOM头
			}
			stringList.add(line);
		}
		input.close();
		return stringList;
	}

	/**
	 * 读入文件到字符串列表，默认不去除UTF-8的BOM文件头
	 * 
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<String> readFileToList(String fileName) throws IOException {
		return readFileToList(fileName, false);
	}
	
	/**
	 * 读入文件到字符串列表
	 * @param fileName
	 * @param doesRemoveBomOfUTF8	是否去除UTF-8的BOM文件头
	 * @return
	 * @throws java.io.IOException
	 */
	public static List<String> readFileToList(String fileName, boolean doesRemoveBomOfUTF8) throws IOException {
		try {
			return readFileToList(new FileInputStream(fileName), doesRemoveBomOfUTF8);
		} catch (FileNotFoundException e) {
			System.err.println("Read file error. File is not found! " + fileName);
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 读入流到字符中，默认不去除UTF-8的BOM文件头
	 * 
	 * @param is
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readFileToString(InputStream is) throws IOException {
		return readFileToString(is, false);
	}
	
	/**
	 * 读入流到字符中, 每行末尾添加一个空格
	 * @param is
	 * @param doesRemoveBomOfUTF8	是否去除UTF-8的BOM文件头
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readFileToString(InputStream is, boolean doesRemoveBomOfUTF8) throws IOException {
		List<String> stringList = readFileToList(is, doesRemoveBomOfUTF8);
		StringBuffer sb = new StringBuffer();
		for (String string : stringList) {
			sb.append(string + " "); // append an extra space
		}
		return sb.toString();
	}

	/**
	 * 读入文件到字符中，默认不去除UTF-8的BOM文件头
	 * 
	 * @param fileName
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readFileToString(String fileName) throws IOException {
		return readFileToString(fileName, false);
	}

	/**
	 * 读入文件到字符中
	 * @param fileName
	 * @param doesRemoveBomOfUTF8	是否去除UTF-8的BOM文件头
	 * @return
	 * @throws java.io.IOException
	 */
	public static String readFileToString(String fileName, boolean doesRemoveBomOfUTF8) throws IOException {
		return readFileToString(new FileInputStream(fileName), doesRemoveBomOfUTF8);
	}

	/**
	 * 将字符串列表写入到文件里
	 * 
	 * @param stringList
	 * @param fileName
	 * @throws java.io.IOException
	 */
	public static void writeListToFile(List<String> stringList, String fileName) throws IOException {
		try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
			StringBuffer sb = new StringBuffer();
			for (String string : stringList) {
				sb.append(string + "\n");
			}
			output.write(sb.toString());
			output.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + fileName);
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 去除UTF-8的BOM文件头
	 * 
	 * @param text
	 * @return
	 */
	public static String removeBOMofUTF8(String text) {
		String BOM = String.valueOf((char)65279); 	// BOM头
		return text.startsWith(BOM) ? text.substring(BOM.length(), text.length()) : text;
	}
}
