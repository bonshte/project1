package com.trading212.project1.core.models.scraping;

public enum AccommodationType {
    ROOM,
    ONE_ROOM,
    TWO_ROOM,
    THREE_ROOM,
    FOUR_ROOM,
    STUDIO,
    MANY_ROOMS,
    MAISONETTE;

    public String toDescriptionString() {
        return switch (this) {
            case ROOM -> "room";
            case ONE_ROOM -> "one room";
            case TWO_ROOM -> "two room";
            case THREE_ROOM -> "three room";
            case FOUR_ROOM -> "four room";
            case MANY_ROOMS -> "many room";
            case STUDIO -> "studio";
            case MAISONETTE -> "maisonette";
        };
    }
}