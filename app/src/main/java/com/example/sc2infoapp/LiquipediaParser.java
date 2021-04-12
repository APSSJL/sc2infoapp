package com.example.sc2infoapp;

import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getPhotoLink(JSONObject json) throws JSONException {
        return json.getJSONObject("properties").getString("metaimageurl");
    }

    public ArrayList<ExternalMatch> getTournamentMatches(String tournamentText)
    {
        Pattern groupMatches = Pattern.compile("\\{\\{Match maps.*?date=(.*?) \\{\\{.*?(finished=(true|))\\n?.*?player1=(.*?|)\\\\n.*?player2=(.*?|)\\\\n.*?(winner=(1|2|)).*?\\}\\}", Pattern.MULTILINE);
        Pattern ro1 = Pattern.compile("R\\d\\d?D\\d\\d?=(.*?) .*?R\\d\\d?D\\d\\d?=(.*?) (.*?)date=(.*?) \\{.*?\\}\\}.*?\\}\\}",Pattern.MULTILINE);
        Pattern ro = Pattern.compile("R\\d\\d?W\\d\\d?=(.*?) .*?R\\d\\d?W\\d\\d?=(.*?) (.*?)date=(.*?) \\{.*?\\}\\}.*?\\}\\}", Pattern.MULTILINE);
        ArrayList<ExternalMatch> res = new ArrayList<>();
        ArrayList<ExternalMatch> matches = new ArrayList<>();
        SimpleDateFormat dateParser = new SimpleDateFormat("MMMM dd, yyyy - HH:mm");
        Matcher matcher = groupMatches.matcher(tournamentText);
        getGroup(matches, matcher, dateParser);
        Matcher matcher1 = ro1.matcher(tournamentText);
        getRo(matcher1, matches, dateParser);
        Matcher matcher2 = ro.matcher(tournamentText);
        getRo(matcher2, matches, dateParser);
        Log.i("","");
        return matches;
    }

    private void getGroup(ArrayList<ExternalMatch> matches, Matcher matcher, SimpleDateFormat dateParser) {
        Pattern map = Pattern.compile("map", Pattern.MULTILINE);
        while (matcher.find()) {
            String t = matcher.group(0);
            Matcher mapMatcher = map.matcher(t);
            try {
                String bo = "-1";
                Pattern p = Pattern.compile(".*map(\\d)");
                Matcher m = p.matcher(t);
                if(m.find()) {
                    bo = (m.group(1));
                }
                String date = "";
                String givenDate = matcher.group(1);
                if(givenDate != null && dateParser.parse(givenDate) != null)
                    date = ExternalMatch.DATE_FORMATTER.format(dateParser.parse(givenDate));
                matches.add(new ExternalMatch(matcher.group(4) + " vs " + matcher.group(5), date, bo));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    public void getRo(Matcher matcher, ArrayList<ExternalMatch> matches, SimpleDateFormat dateParser)
    {
        while (matcher.find())
        {
            String t = matcher.group(0);
            try {
                String bo = "-1";
                Pattern p = Pattern.compile(".*map(\\d)");
                Matcher m = p.matcher(t);
                if(m.find()) {
                    bo = (m.group(1));
                }
                String date = "";
                String givenDate = matcher.group(4);
                if(givenDate != null && dateParser.parse(givenDate) != null)
                    date = ExternalMatch.DATE_FORMATTER.format(dateParser.parse(givenDate));
                matches.add(new ExternalMatch(matcher.group(1) + " vs " + matcher.group(2), date, bo));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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

    public String getCountry(Document doc)
    {
        String name = "";
        Elements d1 = doc.selectFirst("div.fo-nttax-infobox-wrapper").child(0).children();
        for(Element e : d1)
        {
            if(e.child(0).text().contains("Country:"))
                return  e.child(1).text();
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
            Log.i("tester", String.format("%s vs %s, bo %s, time %s, tournament %s", player1, player2, bo, time, tournament));
        };
        Log.i("tester", matches.toString());
        return matches;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<ExternalMatch> parseUpcomingTournamentMatches(Document data, String upcomingTournament)
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

            if (tournament.equals(upcomingTournament)) {
                matches.add(new ExternalMatch(String.format("%s vs %s", player1, player2), time, boInt, tournament));
            }

            Log.i("tester", String.format("%s vs %s, bo %s, time %s, tournament %s", player1, player2, bo, time, tournament));
        };
        Log.i("tester", matches.toString());
        return matches;
    }
}

