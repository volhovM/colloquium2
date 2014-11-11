package ru.ifmo.md.colloquium2;

/**
 * @author volhovm
 *         Created on 11/11/14
 */

public class Candidate {
    private int votes = 0;
    private String name;

    public Candidate(String name) {
        this.name = name;
    }

    public Candidate(String name, int votes) {
        this.name = name;
        this.votes = votes;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote() {
        this.votes++;
    }

    @Override
    public String toString() {
        return name + ": " + votes;
    }
}
