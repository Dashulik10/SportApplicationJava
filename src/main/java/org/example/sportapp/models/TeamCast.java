package org.example.sportapp.models;

public enum TeamCast {
    RESERVE("Резерв"),
    PLAYERS("Основа");

    private final String displayName;

    private TeamCast(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
