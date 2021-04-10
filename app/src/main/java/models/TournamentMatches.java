package models;

import java.util.ArrayList;

import interfaces.IMatch;

public class TournamentMatches {
    private String name;
    private ArrayList<IMatch> matches;
    public TournamentMatches(String name, ArrayList<IMatch> matches) {
        this.name = name;
        this.matches = matches;
    }

    public String getName() {
        return name;
    }

    public ArrayList<IMatch> getMatches() {
        return matches;
    }
}
