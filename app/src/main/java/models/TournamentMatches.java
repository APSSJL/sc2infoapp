package models;

import java.util.ArrayList;

import interfaces.IMatch;

public class TournamentMatches {
    private String name;
    private ArrayList<IMatch> matches;
    private UserTournament parseTournament = null;
    boolean isUserCreated = false;
    public TournamentMatches(String name, ArrayList<IMatch> matches) {
        this.name = name;
        this.matches = matches;
    }

    public String getName() {
        return name;
    }

    public UserTournament getParseTournament() {
        return parseTournament;
    }

    public void setParseTournament(UserTournament parseTournament) {
        this.parseTournament = parseTournament;
    }

    public boolean isUserCreated() {
        return isUserCreated;
    }

    public void setUserCreated(boolean userCreated) {
        isUserCreated = userCreated;
    }

    public ArrayList<IMatch> getMatches() {
        return matches;
    }
}
