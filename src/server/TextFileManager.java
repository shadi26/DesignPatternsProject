package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TextFileManager {
	
	private String log_path;

	public TextFileManager(String log_file_name_) {
		log_path = "./txt/" + log_file_name_;
		try {
			File fid = new File(log_path);
			if (fid.createNewFile()) {
				System.out.println("File created: " + log_file_name_);
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public ArrayList<String> readInputFile(File fid) {
		ArrayList<String> str_list = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(fid);
			while (scanner.hasNextLine()) {
				str_list.add(scanner.nextLine());
			}
			scanner.close();
			System.out.println("readInputFile success.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return str_list;
	}

	public void writeToLogFile(String logstr) {
		try {
			FileWriter log_writer = new FileWriter(log_path, true);
			BufferedWriter bw = new BufferedWriter(log_writer);
			bw.write(logstr);
			bw.close();
			log_writer.close();
			System.out.println("writeToLogFile success.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
