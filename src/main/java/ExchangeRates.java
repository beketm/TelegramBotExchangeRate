import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

public class ExchangeRates {
    private final Integer cacheSize = 20;

    private String X_RAPIDAPI_HOST = "";
    private String X_RAPIDAPI_KEY = "";

    private TreeMap<String, Map<String, Double>> cachedData = new TreeMap<>();

    private final List<String> currencies = Arrays.asList("USD", "EUR", "RUB");

    public Integer getCacheSize() {
        return cacheSize;
    }

    public Map<String, Map<String, Double>> getCachedData() {
        return cachedData;
    }

    public Integer getCurrentCachedDataSize() {
        return cachedData.size();
    }


    public List<String> getCurrencies() {
        return currencies;
    }

    public ExchangeRates() {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        X_RAPIDAPI_HOST = dotenv.get("X_RAPIDAPI_HOST");
        X_RAPIDAPI_KEY = dotenv.get("X_RAPIDAPI_KEY");
    }

    public double getExchangeRate(LocalDate date, String from) {
        if (!currencies.contains(from)) {
            throw new RuntimeException("Currency \"" + from + "\" is not supported in our system!");
        }
        return getExchangeRate(date, from, "KZT");
    }

    public boolean isTodaysDateInAPI(){
        JSONObject jsonResponse = getResponse("KZT", LocalDate.now().toString());
        return jsonResponse!=null && jsonResponse.getBoolean("success");
    }

    public HashMap<String, Double> makeAPICall(LocalDate date, String to) {
        HashMap<String, Double> currencyRates = new HashMap<>();
        String dateString = date.toString();
        JSONObject jsonResponse = getResponse(to, dateString);

        if (jsonResponse == null ) {
            throw new RuntimeException("We were not able to connect to API");
        }

        if (!jsonResponse.getBoolean("success")) {
            throw new RuntimeException("Received unsuccessful response from API");
        }

        for (String currency : currencies) {
            currencyRates.put(currency, jsonResponse.getJSONObject("rates").getDouble(currency));
        }

        return currencyRates;
    }

    private JSONObject getResponse(String to, String dateString) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://fixer-fixer-currency-v1.p.rapidapi.com/" + dateString + "?symbols=USD,EUR,RUB&base=" + to))
                .header("X-RapidAPI-Host", X_RAPIDAPI_HOST)
                .header("X-RapidAPI-Key", X_RAPIDAPI_KEY)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;

        JSONObject jsonObject = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            jsonObject = new JSONObject(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public double getExchangeRate(LocalDate date, String from, String to) {
        String dateString = date.toString();

        if (!cachedData.containsKey(dateString)) {
            Map<String, Double> rates = makeAPICall(date, to);
            addToCachedDate(date, dateString, rates);
        }

        return 1 / cachedData.get(dateString).get(from);

    }

    private void addToCachedDate(LocalDate date, String dateString, Map<String, Double> rates) {

        while (cachedData.size() > cacheSize) {
            cachedData.pollFirstEntry();
        }

        cachedData.put(dateString, rates);
    }

    public Map<String, Map<String, Double>> getLast10DaysRates() {
        return getLastNDaysRates(10);
    }

    private Map<String, Map<String, Double>> getLastNDaysRates(int n) {
        if (n <= 0){
            throw new RuntimeException("n should be a positive number");
        }
        if (n > 365){
            throw new RuntimeException("n cannot be larger than 365");
        }

        int defaultNumberToSubtract = isTodaysDateInAPI() ? 1 : 0;

        LocalDate nDaysAgoDate = LocalDate.now().minusDays(n+defaultNumberToSubtract);
        TreeMap<String, Map<String, Double>> output = new TreeMap<>();

        for(int i = 0; i < n; i++){
            HashMap<String, Double> rates = new HashMap<>();
            for (String currency: currencies){
                rates.put(currency, getExchangeRate(nDaysAgoDate.plusDays(i), currency));
            }
            output.put(nDaysAgoDate.plusDays(i).toString(), rates);
        }

        return output;
    }
}
