package edu.pucmm.eict;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        IO.println("Hello World!");

        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/Michael_Jackson").get();
        IO.println(doc.title());

        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (Element headline : newsHeadlines) {
            System.out.printf("%s\n\t%s%n",
                    headline.attr("title"), headline.absUrl("href"));
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestHTML = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/get"))
                .headers("Content-Type", "text/html;charset=UTF-8")
                .GET()
                .build();

        HttpResponse<String> htmlResponse = client.send(requestHTML, HttpResponse.BodyHandlers.ofString());

        Elements parrafos = doc.select("p");
        Elements imagenes = doc.select("img");
        Elements formularios = doc.select("form");
        IO.println("Estado: " + htmlResponse.statusCode());
        IO.println("Cantidad de lineas: " + doc.html().lines().count());
        IO.println("Parrafos: " + parrafos.size());
        IO.println("Imagenes: " + imagenes.size());
        IO.println("Formularios: " + formularios.size());

        for (Element form : formularios) {
            String metodo = form.attr("method");
            if (metodo.equalsIgnoreCase("Post")){
                String action = form.attr("abs:action");
                HttpRequest postRequest = HttpRequest.newBuilder()
                        .uri(new URI(action))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("matricula-id", "10154267")
                        .POST(HttpRequest.BodyPublishers.ofString("asignatura=practica1"))
                        .build();

                HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

                System.out.println("Form action: " + action);
                System.out.println("Response status: " + postResponse.statusCode());
                System.out.println("Response body: " + postResponse.body());
            }


            IO.println("Metodo de Formulario: " + form.attr("method"));
            Elements inputs = form.select("input");
            for (Element input : inputs) {
                IO.println("  Nombre de Input: " + input.attr("name"));
                IO.println("  Tipo de Input: " + input.attr("type"));
            }
        }


    }
}