import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeRate {
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private static final int TIMEOUT = 5000;

    
    public double getExchangeRate(String fromCurrency, String toCurrency) throws Exception {
        String urlString = API_URL + fromCurrency;
        
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);

        int responseCode = conn.getResponseCode();
        if(responseCode != 200) {
            throw new Exception("API error: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder res = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            res.append(line);
        }
        in.close();

        String jsonResponse = res.toString();
        double rate = parseRate(jsonResponse, toCurrency);

        if (rate == -1) {
            throw new Exception("Currency not found: " + toCurrency);
        }

        return rate;
    }

    private double parseRate(String json, String currency) {
        String searchKey = "\"" + currency + "\":";
        int index = json.indexOf(searchKey);

        if (index == -1) {
            return -1;
        }

        int startIndex = index + searchKey.length();
        int endIndex = startIndex;
        while(endIndex < json.length()) {
            char c = json.charAt(endIndex);
            if(c == ',' | c == '}') {
                break;
            }
            endIndex++;
        }

        String rateString = json.substring(startIndex, endIndex).trim();
        return Double.parseDouble(rateString);
    }

    public ConversionResult convert(double amount, String fromCurrency, String toCurrency) throws Exception {
        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be more than 0");
        }

        double rate = getExchangeRate(fromCurrency, toCurrency);
        double convertedAmount = amount * rate;

        return new ConversionResult(amount, fromCurrency, convertedAmount, toCurrency, rate);
    }
}
