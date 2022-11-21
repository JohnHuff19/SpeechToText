import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import com.google.gson.Gson;

public class RestAPI {
    public static void main (String [] args) throws Exception{


        System.out.print("Enter an audio URL: ");
        Scanner sc = new Scanner(System.in);
        String url=sc.nextLine();
        Transcript transcript = new Transcript();
        transcript.setAudio_url(url+"?raw=true");
        Gson gson = new Gson();
        String jsonRequest= gson.toJson(transcript);
        HttpRequest postRequest= HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "acc1d71c87ee476d9761bcbed37108e0")
                .POST(BodyPublishers.ofString(jsonRequest))
                .build();   
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse=  httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        transcript= gson.fromJson(postResponse.body(), Transcript.class);

        HttpRequest getRequest= HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/"+transcript.getId()))
                .header("Authorization", "acc1d71c87ee476d9761bcbed37108e0")
                .build();
        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());
            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())){
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println(transcript.getText());
    }
}