package com.example.sc2infoapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LiquipediaClient {
    public static final String BASE_URL = "https://liquipedia.net/starcraft2/api.php?";
    protected OkHttpClient client;

    public LiquipediaClient()
    {
        client = new OkHttpClient();
    }

    public String getPageByName(String name) throws IOException, JSONException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
        urlBuilder.addQueryParameter("action", "parse");
        urlBuilder.addQueryParameter("page", name);
        urlBuilder.addQueryParameter("format", "json");
        urlBuilder.addQueryParameter("formatversion", "2");
        urlBuilder.addQueryParameter("contentmodel", "wikitext");

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        String responseData = response.body().string();
        JSONObject json = null;
        json = new JSONObject(responseData);
        String text = json.getJSONObject("parse").getString("text");
        Log.i("CLIENT", text.substring(0,500));
        return text;
    }

    public String getMatches() throws IOException, JSONException {
        return  getPageByName("Liquipedia:Upcoming_and_ongoing_matches");
    }
}