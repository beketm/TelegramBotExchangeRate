import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class Main {

    private static String formatDate(String date){
        String[] array = date.split("-");
        List<String> list = Arrays.asList(array);
        Collections.reverse(list);
        return String.join(".", list);
    }

    public static void main(String[] args){
        ExchangeRates er = new ExchangeRates();
        System.out.println("Hello Words");

        String str = "";

        Map<String, Map<String, Double>> output = er.getLast10DaysRates();

        for (Map.Entry<String, Map<String, Double>> entry : output.entrySet()) {
            str += "1\n";
            System.out.println(formatDate(entry.getKey()));
            for(Map.Entry<String, Double> e: entry.getValue().entrySet()){
                System.out.println("             " + e.getKey() + " : " + String.format("%.2f", e.getValue()));
            }
        }

        System.out.println(str);

    }
}
