package com.example.hotel20;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private final String reservationNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<RoomInfo> rooms;
    private String contactName;
    private String contactEmail;
    private String contactPhone;

    public Reservation(LocalDate checkInDate, LocalDate checkOutDate, List<RoomInfo> rooms, String contactName, String contactEmail, String contactPhone) {
        this.reservationNumber = "RSV-" + UUID.randomUUID().toString().substring(0, 8); // Generate a unique reservation number
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.rooms = rooms;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public List<RoomInfo> getRooms() {
        return rooms;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Check-in Date: ").append(checkInDate).append("\n");
        details.append("Check-out Date: ").append(checkOutDate).append("\n");
        details.append("Contact Name: ").append(contactName).append("\n");
        details.append("Contact Email: ").append(contactEmail).append("\n");
        details.append("Contact Phone: ").append(contactPhone).append("\n");
        details.append("Rooms:\n");
        for (RoomInfo room : rooms) {
            details.append(room.getName()).append(" - Max Guests: ").append(room.getMaxGuests()).append(" - Price: ").append(room.getPrice()).append("\n");
        }
        return details.toString();
    }
}