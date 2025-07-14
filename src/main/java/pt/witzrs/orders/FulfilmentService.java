package pt.witzrs.orders;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class FulfilmentService {
    private static final String DISTANCE_API_URL = "http://192.168.1.220:5000/route/v1/driving/%s,%s;%s,%s";
    private static final String ADDRESS_TO_COORDINATES_API_URL = "http://192.168.1.220:8080/search?street=%s&postalcode=%s";
    private static final String SUSHIPOINT_LAT = "32.6433202273946";
    private static final String SUSHIPOINT_LON = "-16.830936296339853";
    private static final HttpClient client = HttpClient.newHttpClient();


    private static String makeApiCalls(String url, List<String> params) throws URISyntaxException, IOException, InterruptedException {
        url = String.format(url,params.toArray());
        url=url.replace(" ","%20");
        System.out.println(url);
        URI uri = new URI(url);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return response.body();
    }

    private static JSONArray getAddressInformation(HashMap<String,String> address) throws IOException, InterruptedException, URISyntaxException {
        List<String> params = Arrays.asList(address.get("street"),address.get("postal_code"));
        String resp = makeApiCalls(ADDRESS_TO_COORDINATES_API_URL,params);
        JSONArray jsArr = new JSONArray(resp);
        System.out.println(jsArr);
        return jsArr;

    }

    private static Double calculateAverageDistance(JSONArray jsArr,String county) throws URISyntaxException, IOException, InterruptedException {
        Double distance = 0.0;
        int count = 0;
        for (int i=0; i<jsArr.length();i++) {
            JSONObject obj = jsArr.getJSONObject(i);
            if (obj.get("display_name").toString().contains(county)) {
                count++;
                List<String> params = Arrays.asList(SUSHIPOINT_LON,SUSHIPOINT_LAT, obj.get("lon").toString(),obj.get("lat").toString());
                distance += calculateDistance(params);
            }
        }
        return distance/count;

    }

    private static double calculateDistance(List<String> params) throws URISyntaxException, IOException, InterruptedException {
        String s = makeApiCalls(DISTANCE_API_URL, params);
        JSONObject jsObj = new JSONObject(s);
        System.out.println("1");
        JSONArray ja = new JSONArray(jsObj.get("routes").toString());
        JSONObject elem = new JSONObject(ja.get(0));
        System.out.println(elem);
        System.out.println(elem.keySet());
        //System.out.println(elem.get("distance"));
        return 1.0;
    }

    /*TODO*/
    private static double calculateDeliveryCost(HashMap<String,String> address) throws IOException, InterruptedException {
        String st = address.get("street").replace(" ", "%20");
        String pc = address.get("postal_code");
        List<String> l = new ArrayList<>();
        l.add(st);
        l.add(pc);
        String url = String.format("http://192.168.1.220:8080/search?street=%s&postalcode=%s",l.toArray());
        System.out.println(url);
        URI uri = URI.create(url);
        System.out.println(String.format("Time before get request:\t %s ",System.nanoTime()/1000000));

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(System.nanoTime()/1000000);
        System.out.println(String.format("Time before json object serialization:\t %s ",System.nanoTime()/1000000));
        JSONObject jsArr = new JSONObject(response.body().substring(1,response.body().length()-1));
        System.out.println(String.format("Time after json object serialization:\t %s ",System.nanoTime()/1000000));

        System.out.println(jsArr.get("lon"));
        /*        System.out.println(response.body[0].get("lon"));*/
        return 0.0;
    }



    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        HashMap<String,String> address = new HashMap<>();
        address.put("street","Estrada João Gonçalves Zarco");
        address.put("postal_code","9325-085");
        address.put("county","Caniço");
        List<String> params = Arrays.asList(SUSHIPOINT_LAT,SUSHIPOINT_LON);

        JSONArray jsArr = getAddressInformation(address);
        System.out.println(calculateAverageDistance(jsArr,address.get("county")));

        //System.out.println(jsArr);
        for (int i=0; i<jsArr.length();i++) {
            if (jsArr.getJSONObject(i).get("display_name").toString().contains(address.get("county"))) {
                String destination_latitude = jsArr.getJSONObject(i).get("lat").toString();
                String destination_longitude = jsArr.getJSONObject(i).get("lon").toString();
                String url = String.format(DISTANCE_API_URL,SUSHIPOINT_LAT,SUSHIPOINT_LON,destination_latitude,destination_longitude);
                URI uri = new URI(url);
                HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
                HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

                //System.out.println(String.format("The Coordinates are:\t%s,%s (Lat,Lon) in %s",,jsArr.getJSONObject(i).get("lon"), jsArr.getJSONObject(i).get("display_name")));
            }
            System.out.println();

            }
    }



}
