package com.example.sc2infoapp;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

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
        for (int i = 1; i < 5; i++)
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

    public ArrayList<Pair<String, String>> getRecentMatches(Document doc)
    {
        ArrayList<Pair<String, String>> opponents = new ArrayList<>();
        Elements tags = doc.select("div.fo-nttax-infobox-wrapper");
        Element matches = tags.get(tags.size() - 1);
        Elements bodies = matches.select("tbody");
        for(Element e : bodies)
        {
            Elements td = e.select("td");
            String opp = td.get(2).select("span").get(1).selectFirst("a").attr("title");
            String time = td.get(3).selectFirst("span").text();
            opponents.add(new Pair<>(opp, time));
        }
        return opponents;
    }
}

