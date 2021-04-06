package com.example.sc2infoapp;

import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import models.ExternalMatch;

public class LiquipediaParser {
    public String getRace(JSONObject json) throws JSONException {
        JSONArray categories = json.getJSONArray("categories");
        for(int i = 0; i < categories.length(); i++)
        {
            String title = categories.getJSONObject(i).getString("category");
            if(title.equals("Terran_Players"))
                return "Terran";
            if(title.equals("Protoss_Players"))
                return "Protoss";
            if(title.equals("Zerg_Players"))
                return "Zerg";
        }
        return "";
    }

    public String getPhotoLink(JSONObject json, String name) throws JSONException {
        JSONArray images = json.getJSONArray("images");
        name = name.toLowerCase();
        for (int i = 0; i < images.length(); i++)
        {
            if(images.getString(i).toLowerCase().contains(name))
            {
                return  "https://liquipedia.net/starcraft2/File:" + images.getString(i);
            }
        }
        return null;
    }

    public String getBio(Document doc) throws JSONException {
        Elements x = doc.select("p");
        StringBuilder sb = new StringBuilder();
        int size = x.size();
        for (int i = 1; i < 3; i++)
        {
            String v = x.get(i).text();
            if(v.contains("&#10;\n"))
                break;
            sb.append(v);
        }
        return sb.toString();
    }

    public String getName(Document doc)
    {
        String name = "";
        Elements d1 = doc.selectFirst("div.fo-nttax-infobox-wrapper").child(0).children();
        for(Element e : d1)
        {
            if(e.child(0).text().contains("Romanized Name:"))
                return  e.child(1).text();
            if(e.child(0).text().contains("Name:"))
                name = e.child(1).text();
        }
        return name;
    }

    public ArrayList<ExternalMatch> getRecentMatches(Document doc, String playerName)
    {
        ArrayList<ExternalMatch> opponents = new ArrayList<>();
        Elements tags = doc.select("div.fo-nttax-infobox-wrapper");
        Element matches = tags.get(tags.size() - 1);
        Elements bodies = matches.select("tbody");
        for(Element e : bodies)
        {
            Elements td = e.select("td");
            String opp = td.get(2).select("span").get(1).selectFirst("a").attr("title");
            String time = td.get(3).selectFirst("span").text();
            opponents.add(new ExternalMatch(opp, time, -1));
        }

        if(tags.size() == 3)
        {
            matches = tags.get(tags.size() - 2);
            bodies = matches.select("tbody");
            for(Element e : bodies)
            {
                Elements td = e.select("td");
                String opp = td.get(2).select("span").get(1).selectFirst("a").attr("title");
                String time = td.get(3).selectFirst("span").text();

                String bo = td.get(1).select("abbr").attr("title");
                String[] x = bo.split(" ");
                Integer boInt = Integer.parseInt(x[2]);
                opponents.add(new ExternalMatch(playerName + " vs " + opp, time, boInt));
            }
        }

        return opponents;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<ExternalMatch> parseUpcomingMatches(Document data)
    {
        ArrayList<ExternalMatch> matches = new ArrayList<>();
        Elements tables = data.select("table");
        for(Element table : tables)
        {
            Elements rows = table.select("td");
            String player1 = rows.get(0).selectFirst("a").text();
            String player2 = rows.get(2).select("a").last().text();
            String bo = rows.get(1).select("abbr").attr("title");
            String time = rows.get(3).child(0).child(0).text();
            String tournament = rows.get(3).selectFirst("div").selectFirst("div").text();

            String[] x = bo.split(" ");
            Integer boInt = 1;
            if(x.length == 3)
                boInt = Integer.parseInt(x[2]);

            matches.add(new ExternalMatch(String.format("%s vs %s", player1, player2), time, boInt, tournament));
            //Log.i(TAG, String.format("%s vs %s, bo %s, time %s, tournament %s", player1, player2, bo, time, tournament));
        };
        return matches;
    }
}

