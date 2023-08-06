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

    public static AccommodationType fromGPTFunctionArgument(String argument) {
        switch (argument) {
            case "one room": return AccommodationType.ONE_ROOM;
            case "two room": return AccommodationType.TWO_ROOM;
            case "three room": return AccommodationType.THREE_ROOM;
            case "four room": return AccommodationType.FOUR_ROOM;
            case "many room": return AccommodationType.MANY_ROOMS;
            case "studio": return AccommodationType.STUDIO;
            case "maisonette": return AccommodationType.MAISONETTE;
            default: throw new RuntimeException("invalid gpt function call argument for apartment type");
        }
    }
}