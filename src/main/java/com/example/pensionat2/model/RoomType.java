package com.example.pensionat2.model;
public enum RoomType {
    SINGLE("Enkelrum"),
    DOUBLE("Dubbelrum");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return name();
    }

}





