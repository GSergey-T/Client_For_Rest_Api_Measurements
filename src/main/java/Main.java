import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        //authReg();
        //sensorRegistration();
        //addMeasurements();
        //get1000Measurements();
        //getRainyDays();
    }

    // Получить 1000 измерений
    public static void get1000Measurements() throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        String cookie = authentication();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        ResponseEntity<String> responseFromSecuredEndPoint
                = restTemplate.exchange("http://localhost:8080/measurements", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        //Регистрирую модуль для LocalDataTime
        objectMapper.findAndRegisterModules();

        List<MeasurementForJson> ml = objectMapper.readValue(responseFromSecuredEndPoint.getBody(), new TypeReference<>() {});

        int i = 0;
        List<Integer> xData = new ArrayList<>();
        List<Float> yData = new ArrayList<>();

    for (MeasurementForJson mfj : ml) {
        xData.add(i++);
        yData.add(mfj.getValue());

            System.out.println("<------------------------>");
            System.out.println("Value: " + mfj.getValue());
            System.out.println("Raining: " + mfj.isRaining());
            System.out.println("Time: " + mfj.getTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            System.out.println("Sensor: " + mfj.getSensor().getName());
            System.out.println("<------------------------>");
    }
        getScheme(xData, yData);
}

    //Регистрация нового пользователя
    public static void authReg() {
        String cookie = authentication();
        RestTemplate restTemplate = new RestTemplate();

        Scanner in = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = in.nextLine();
        System.out.print("Enter password: ");
        String password = in.nextLine();
        System.out.print("Enter role: ");
        String role = in.nextLine();

        Map<String,String> map =  new HashMap<>();
        map.put("name", name);
        map.put("password", password);
        map.put("role", role);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);

        ResponseEntity<String> responseFromSecuredEndPoint =
                restTemplate.exchange("http://localhost:8080/auth/reg", HttpMethod.POST, new HttpEntity<>(map,headers), String.class);

        System.out.println(responseFromSecuredEndPoint.getBody());
    }

    //Получить кол-во дождливых дней
    public static void getRainyDays() {
        RestTemplate restTemplate = new RestTemplate();
        String cookie = authentication();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);
        ResponseEntity<String> responseFromSecuredEndPoint
                = restTemplate.exchange("http://localhost:8080/measurements/rainyDaysCount", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        System.out.println(responseFromSecuredEndPoint.getBody());
    }

    //Регистрация сенсора
    public static void sensorRegistration() {
        String cookie = authentication();
        RestTemplate restTemplate = new RestTemplate();

        Scanner in = new Scanner(System.in);
        System.out.print("Enter sensorName: ");
        String sensorName = in.nextLine();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookie);

           ResponseEntity<String> responseFromSecuredEndPoint =
                   restTemplate.exchange("http://localhost:8080/sensors/registration", HttpMethod.POST, new HttpEntity<>(new Sensor(sensorName),headers), String.class);

        System.out.println(responseFromSecuredEndPoint.getBody());
    }

    // Отправить 1000 случайных измерений
    public static void addMeasurements () {
        String cookie = authentication();
        RestTemplate restTemplate = new RestTemplate();

        Scanner in = new Scanner(System.in);
        System.out.print("Enter sensorName: ");
        String sensorName = in.nextLine();

        for (int i = 0; i < 1000; i++) {

            MeasurementForJson measurement = new MeasurementForJson();

            measurement.setValue((float) (Math.random()*40));
            if (i % 2 == 0) {
                measurement.setRaining(true);
            } else
                measurement.setRaining(false);

            measurement.setSensor(new Sensor(sensorName));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);
            ResponseEntity<String> responseFromSecuredEndPoint =
                    restTemplate.exchange("http://localhost:8080/measurements/add", HttpMethod.POST, new HttpEntity<>(measurement,headers), String.class);

            System.out.println(responseFromSecuredEndPoint.getBody());
        }
    }

    public static void getScheme (List<Integer> xData , List<Float> yData) {
        // Create Chart
        XYChart chart = QuickChart.getChart("Temperature measurement", "X", "Y", "y(x)", xData, yData);
        // Show it
        new SwingWrapper(chart).displayChart();
    }

    //get cookie
    public static String authentication () {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = in.nextLine();
        System.out.print("Enter password: ");
        String password = in.nextLine();

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("username", username);
        form.set("password", password);
        ResponseEntity<String> loginResponse =
                restTemplate.postForEntity("http://localhost:8080/login", new HttpEntity<>(form, new HttpHeaders()), String.class);
        return loginResponse.getHeaders().get("Set-Cookie").get(0);
    }
}
