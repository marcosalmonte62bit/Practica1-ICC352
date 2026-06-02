package edu.pucmm.eict;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        IO.println("Digite una URL:");
        String url = scanner.nextLine();

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestHTML = HttpRequest.newBuilder()
                .uri(new URI(url))
                .headers("Content-Type", "text/html;charset=UTF-8")
                .GET()
                .build();

        HttpResponse<String> htmlResponse = client.send(requestHTML, HttpResponse.BodyHandlers.ofString());
        String recurso = htmlResponse.headers().firstValue("Content-Type").orElse("Desconocido");

        if (recurso.contains("text/html")) {
            Document doc = Jsoup.connect(url).get();
            IO.println("El recurso es un archivo HTML");
            IO.println(doc.title());

            Elements parrafos = doc.select("p");
            Elements imagenes = doc.select("p img");
            Elements formularios = doc.select("form");
            int posts = 0;
            int gets = 0;
            IO.println("Estado: " + htmlResponse.statusCode());
            IO.println("Content-Type: " + recurso);
            IO.println("Cantidad de lineas: " + doc.html().lines().count());
            IO.println("Parrafos: " + parrafos.size());
            IO.println("Imagenes: " + imagenes.size());
            IO.println("Formularios: " + formularios.size());

            for (Element form : formularios) {
                String metodo = form.attr("method");
                if (metodo.equalsIgnoreCase("Post")) {
                    posts++;
                    String action = form.absUrl("action");
                    HttpRequest postRequest = HttpRequest.newBuilder()
                            .uri(new URI(action))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("matricula-id", "10154267")
                            .POST(HttpRequest.BodyPublishers.ofString("asignatura=practica1"))
                            .build();

                    HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

                    IO.println("Action de Formulario: " + action);
                    IO.println("Estado del response: " + postResponse.statusCode());
                } else if (metodo.equalsIgnoreCase("Get")) {
                    gets++;
                }

                IO.println("Metodo de Formulario: " + form.attr("method"));
                Elements inputs = form.select("input");
                for (Element input : inputs) {
                    IO.println("  Nombre de Input: " + input.attr("name"));
                    IO.println("  Tipo de Input: " + input.attr("type"));
                }
            }
            IO.println("Cantidad de Formularios POSTS: " + posts);
            IO.println("Cantidad de Formularios GETS: " + gets);
            scanner.close();
        } else {
            if (recurso.contains("application/pdf")) {
                IO.println("El recurso es un pdf");
            } else if (recurso.contains("image/png") || recurso.contains("image/jpeg")) {
                IO.println("El recurso es una imagen");
            } else if (recurso.contains("application/json")) {
                IO.println("El recurso es un JSON");
            }
            scanner.close();
            return;
        }
    }
}

