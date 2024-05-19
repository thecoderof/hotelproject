package com.example.hotel20;

import javafx.beans.property.*;

public class RoomInfo {
    private final StringProperty name;
    private final IntegerProperty maxGuests;
    private final DoubleProperty price;
    private final BooleanProperty balcony;
    private final BooleanProperty ac;
    private final StringProperty status;  // e.g., "Clean", "Needs Cleaning"
    private final DoubleProperty minibarPayments;

    public RoomInfo(String name, int maxGuests, double price, boolean balcony, boolean ac) {
        this.name = new SimpleStringProperty(name);
        this.maxGuests = new SimpleIntegerProperty(maxGuests);
        this.price = new SimpleDoubleProperty(price);
        this.balcony = new SimpleBooleanProperty(balcony);
        this.ac = new SimpleBooleanProperty(ac);
        this.status = new SimpleStringProperty("Clean");
        this.minibarPayments = new SimpleDoubleProperty(0.0);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getMaxGuests() {
        return maxGuests.get();
    }

    public IntegerProperty maxGuestsProperty() {
        return maxGuests;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public boolean hasBalcony() {
        return balcony.get();
    }

    public BooleanProperty balconyProperty() {
        return balcony;
    }

    public boolean hasAC() {
        return ac.get();
    }

    public BooleanProperty acProperty() {
        return ac;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public double getMinibarPayments() {
        return minibarPayments.get();
    }

    public DoubleProperty minibarPaymentsProperty() {
        return minibarPayments;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public void setMinibarPayments(double minibarPayments) {
        this.minibarPayments.set(minibarPayments);
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%.2f,%b,%b,%s,%.2f", name.get(), maxGuests.get(), price.get(), balcony.get(), ac.get(), status.get(), minibarPayments.get());
    }

    public static RoomInfo fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid input string for RoomInfo");
        }
        String name = parts[0];
        int maxGuests = Integer.parseInt(parts[1]);
        double price = Double.parseDouble(parts[2]);
        boolean balcony = Boolean.parseBoolean(parts[3]);
        boolean ac = Boolean.parseBoolean(parts[4]);
        String status = parts[5];
        double minibarPayments = Double.parseDouble(parts[6]);
        RoomInfo roomInfo = new RoomInfo(name, maxGuests, price, balcony, ac);
        roomInfo.setStatus(status);
        roomInfo.setMinibarPayments(minibarPayments);
        return roomInfo;
    }
}