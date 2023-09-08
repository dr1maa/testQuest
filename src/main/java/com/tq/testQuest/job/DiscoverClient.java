package com.tq.testQuest.job;

import net.bytebuddy.asm.Advice;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class DiscoverClient {
    @Autowired
    private OkHttpClient client = new OkHttpClient();

    public DiscoverClient(OkHttpClient client) {
        this.client = client;
    }

    public Response response() throws IOException {
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZTgwN2Y3Zjg1OThkNjZhMDZlY2FjNTRiZGY5ZWUxZCIsInN1YiI6IjY0ZjIzNjBiZTBjYTdmMDBhZTM5YmRiYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.UFkHevBZYjleei8IIez043l0kHUK8s2Eenxu-_4tt7c")
                .build();

        return client.newCall(request).execute();
    }
}
