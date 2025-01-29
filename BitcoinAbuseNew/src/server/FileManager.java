package server;

import java.io.File;
import java.util.ArrayList;

import common.ReportEntry;

// Structural Patterns: Facade
public class FileManager implements EventListener {

	public EventManager event_manager;

	private TextFileManager text_file_manager;
	private ExcelFileManager excel_file_manager;

	public FileManager() {
		event_manager = new EventManager("Result Upload", "Result Save");

		text_file_manager = new TextFileManager("log.txt");
		excel_file_manager = new ExcelFileManager();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(String eventType, Object obj) {
		if (eventType.equals("Log")) {
			text_file_manager.writeToLogFile((String) obj);

		} else if (eventType.equals("Upload File")) {
			ArrayList<String> arr = text_file_manager.readInputFile((File) obj);
			event_manager.notify("Result Upload", arr);

		} else if (eventType.equals("Save Report")) {
			ArrayList<Object> list = (ArrayList<Object>) obj;
			boolean result_save = excel_file_manager.writeToExcelFile((String) list.get(0),
					(ArrayList<ReportEntry>) list.get(1));
			event_manager.notify("Result Save", result_save);
		}
	}
}
