package com.example.hotel20;

public class Room {
    private boolean hasBalcony;
    private String bedType;
    private boolean hasAC;
    private int maxGuests;
    private double price;
    private int availableRooms;
    private String name;

    public Room(boolean hasBalcony, String bedType, boolean hasAC, int maxGuests, double price, int availableRooms, String name) {
        this.hasBalcony = hasBalcony;
        this.bedType = bedType;
        this.hasAC = hasAC;
        this.maxGuests = maxGuests;
        this.price = price;
        this.availableRooms = availableRooms;
        this.name = name;
    }

    public boolean hasBalcony() { return hasBalcony; }

    public String getBedType() { return bedType; }

    public boolean hasAC() { return hasAC; }

    public int getMaxGuests() { return maxGuests; }

    public double getPrice() { return price; }

    public int getAvailableRooms() { return availableRooms; }

    public String getName() { return name; }
}