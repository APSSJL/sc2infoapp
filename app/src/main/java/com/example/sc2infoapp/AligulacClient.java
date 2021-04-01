package com.example.sc2infoapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AligulacClient {
    public  static final String BASE_URL = "http://aligulac.com/api/v1";
    protected OkHttpClient client;

    public AligulacClient()
    {
        client = new OkHttpClient();
    }

    public JSONObject getPlayer(String name) throws IOException, JSONException {
        String apiUrl = getApiUrl("/player/?");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("lp_name", name);
        urlBuilder.addQueryParameter("apikey", BuildConfig.ALIGULAC_KEY);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        JSONObject json = null;
        json = new JSONObject(responseData);
        JSONObject player = json.getJSONArray("objects").getJSONObject(0);
        Log.i("CLIENT", player.toString());
        return player;
    }

    public JSONArray getTopRating() throws IOException, JSONException {
        String apiUrl = getApiUrl("/rating/?");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("apikey", BuildConfig.ALIGULAC_KEY);
        urlBuilder.addQueryParameter("order_by", "-rating");

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        JSONObject json = null;
        json = new JSONObject(responseData);
        JSONArray topPlayers = json.getJSONArray("objects");
        Log.i("CLIENT", topPlayers.toString());
        return topPlayers;
    }

    public JSONObject getPrediction(int player1Id, int player2Id, int bo) throws IOException, JSONException {
        String apiUrl = getApiUrl(String.format("/predictmatch/%d,%d/?",player1Id,player2Id));
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("apikey", BuildConfig.ALIGULAC_KEY);
        urlBuilder.addQueryParameter("bo", String.valueOf(bo));

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        JSONObject json = null;
        json = new JSONObject(responseData);
        Log.i("CLIENT", json.toString());
        return json;
    }

    protected String getApiUrl(String path) {
        return BASE_URL + "/" + path;
    }
}