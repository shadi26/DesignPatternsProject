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

    private static final String API_URL = "https://api.chainabuse.com/v0/reports";
    private static final String API_TOKEN = "Y2FfTkVRMldrUjVla05FV1VsWGVUVjJaVUZZYW5WbGIwc3hMa2szTkd4S2NVNXhaRUZwZEhSbGVGQnlNMVJrYkZFOVBROmNhX05FUTJXa1I1ZWtORVdVbFhlVFYyWlVGWWFuVmxiMHN4TGtrM05HeEtjVTV4WkVGcGRIUmxlRkJ5TTFSa2JGRTlQUQ==";

    private BitcoinAbuse() {
        event_manager = new EventManager("Scan Results");
        report_entry = null;
    }

    public static BitcoinAbuse getInstance() {
        if (instance == null) {
            instance = new BitcoinAbuse();
        }
        return instance;
    }

    private boolean checkAddress(String address) {
        try {
            String check_url = API_URL + "?includePrivate=false&page=1&perPage=50&address=" + address;
            System.out.println("HttpRequest: " + check_url);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(check_url))
                    .header("accept", "application/json")
                    .header("authorization", "Basic " + API_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                parse(response.body() , address);
                return true;
            } else {
                System.out.println("API Error: " + response.statusCode() + " - " + response.body());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void parse(String response_body , String Address) {
        System.out.println("HttpResponse: " + response_body);

        try {
            JSONArray reports = new JSONArray(response_body); 

            if (reports.length() > 0) {
                JSONObject firstReport = reports.getJSONObject(0);

                String address = firstReport.getJSONArray("addresses")
                                            .getJSONObject(0)
                                            .getString("address");

                int reportCount = reports.length();  // Count of abuse reports
                String link = "https://chainabuse.com/report/" + address;  // ChainAbuse Report Link

                report_entry = new ReportEntry(address, String.valueOf(reportCount), link);
            } else {
                System.out.println("No reports found for this address.");
                
                report_entry = new ReportEntry(Address, "0" , "no link");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
