package pt.witzrs.orders;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class FulfilmentService {
private static String SEARCH_URL = "http://192.168.1.220:8080/search?street=%s&postalcode=%s";
    private static JSONObject getAddressInformation(HashMap<String,String> address)throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        String st = address.get("street").replace(" ", "%20");
        String pc = address.get("postal_code");
        String url = String.format(SEARCH_URL,st,pc);
        URI uri = URI.create(url);

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());


        JSONObject jsobj = new JSONObject(response.body().substring(1,response.body().length()-1));
        /*        System.out.println(response.body[0].get("lon"));*/
        return jsobj;

    }
    private static Long getAddressCoordinates(HashMap<String,String> address){

        return 1L;
    }
    private static double calculateDeliveryCost(HashMap<String,String> address) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String st = address.get("street").replace(" ", "%20");
        String pc = address.get("postal_code");
        String url = String.format("http://192.168.1.220:8080/search?street=%s&postalcode=%s",st,pc);
        System.out.println(url);
        URI uri = URI.create(url);
        System.out.println(String.format("Time before get request:\t %s ",System.nanoTime()/1000000));

        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        System.out.println(System.nanoTime()/1000000);
        System.out.println(String.format("Time before json object serialization:\t %s ",System.nanoTime()/1000000));
        JSONObject jsobj = new JSONObject(response.body().substring(1,response.body().length()-1));
        System.out.println(String.format("Time after json object serialization:\t %s ",System.nanoTime()/1000000));

        System.out.println(jsobj.get("lon"));
        /*        System.out.println(response.body[0].get("lon"));*/
        return 0.0;
    }
    public static void main(String[] args) {
        HashMap<String,String> address = new HashMap<>();
        address.put("street","Estrada João Gonçalves Zarco");
        address.put("postal_code","9325-085");

        try {
            System.out.println(getAddressInformation(address));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
