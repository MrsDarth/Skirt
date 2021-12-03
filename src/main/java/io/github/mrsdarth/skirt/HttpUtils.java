package io.github.mrsdarth.skirt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class HttpUtils {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static <T> Optional<HttpResponse<T>> send(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            return Optional.of(CLIENT.send(request, bodyHandler));
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public static <T> Optional<HttpResponse<T>> getRequest(String uri, HttpResponse.BodyHandler<T> bodyHandler) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).build();
        return send(request, bodyHandler);
    }

    public static <T> Optional<T> getBody(String uri, HttpResponse.BodyHandler<T> bodyHandler) {
        return getRequest(uri, bodyHandler).map(HttpResponse::body);
    }

    public static Optional<String> simpleGetRequest(String uri) {
        return getBody(uri, HttpResponse.BodyHandlers.ofString());
    }

}
