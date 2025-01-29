package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import common.AddressEntry;
import common.ReportEntry;

// Creational Patterns: Singleton
public final class BitcoinAbuse implements EventListener {

	private static BitcoinAbuse instance;

	public EventManager event_manager;

	private static ReportEntry report_entry;
	private static String report_url;

	private String request_url;
	private String api_token;

	private BitcoinAbuse() {
		event_manager = new EventManager("Scan Results");

		report_url = "https://www.bitcoinabuse.com/api/reports/";
		request_url = report_url + "check";
		api_token = "LaLUCyWeNisLmu1pDi3F54b2VAfFKEVkeOW7KY4pTlUwXQTJH6qE50bn60H3";
		// api_token = "GpTwDcZsUrv4D41M4fPnqlN7KZsGQvW2M7kANAkEQRdah9WSeNJZsIHcvwRb";
		report_entry = null;
	}

	public static BitcoinAbuse getInstance() {
		if (instance == null) {
			instance = new BitcoinAbuse();
		}
		return instance;
	}

	private boolean checkAddress(String address) {
		String check_url = request_url + "?address=" + address + "&api_token=" + api_token;
		System.out.println("HttpRequest: " + check_url);
		report_entry = null;
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(check_url)).GET().build();
			client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
					.thenAccept(BitcoinAbuse::parse)
					// .thenAccept(System.out::println)
					.join();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// https://www.youtube.com/watch?v=qzRKa8I36Ww
	private static void parse(String response_body) {
		System.out.println("HttpResponse: " + response_body);
		JSONArray data = new JSONArray("[" + response_body + "]");
		JSONObject entry = data.getJSONObject(0);

		String address = entry.getString("address");
		String report_count = Integer.toString(entry.getInt("count"));
		String link = report_url + address;
		report_entry = new ReportEntry(address, report_count, link);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(String eventType, Object obj) {
		if (eventType.equals("Scan Addresses")) {
			try {
				ArrayList<AddressEntry> address_list = (ArrayList<AddressEntry>) obj;
				ArrayList<ReportEntry> report_entry_list = new ArrayList<>();
				for (AddressEntry address_entry : address_list) {
					if (checkAddress(address_entry.getAddress())) {
						report_entry_list.add(report_entry);
					} else {
						throw new Exception();
					}
				}
				event_manager.notify("Scan Results", report_entry_list);
			} catch (Exception e) {
				e.printStackTrace();
				event_manager.notify("Scan Results", null);
			}
		}
	}
}
