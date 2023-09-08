package com.tq.testQuest.job;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DiscoverClient {
    @Autowired
    private OkHttpClient client = new OkHttpClient();
    private Response response; // Перенесите объявление поля сюда

    public DiscoverClient(OkHttpClient client) {
        this.client = client;
    }

    // Метод для выполнения запроса и сохранения response
    public void fetchData() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZTgwN2Y3Zjg1OThkNjZhMDZlY2FjNTRiZGY5ZWUxZCIsInN1YiI6IjY0ZjIzNjBiZTBjYTdmMDBhZTM5YmRiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.UFkHevBZYjleei8IIez043l0kHUK8s2Eenxu-_4tt7c")
                .build();

        response = client.newCall(request).execute();
    }

    // Метод для получения объекта Response из другого класса
    public Response getResponse() {
        return response;
    }
}
