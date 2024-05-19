package com.example.hotel20;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class ReservationManager {
    private static final ReservationManager instance = new ReservationManager();
    private final Map<String, Reservation> reservations = new HashMap<>();
    private final Path reservationDirectory = Paths.get("reservations");

    private ReservationManager() {
        loadReservations();
    }

    public static ReservationManager getInstance() {
        return instance;
    }

    public synchronized void addReservation(String reservationNumber, Reservation reservation) {
        reservations.put(reservationNumber, reservation);
        saveReservationToFile(reservationNumber, reservation);
    }

    public synchronized Reservation getReservation(String reservationNumber) {
        if (!reservations.containsKey(reservationNumber)) {
            // Try to load the reservation from the file
            Path filePath = reservationDirectory.resolve(reservationNumber + ".txt");
            System.out.println("Looking for reservation file: " + filePath.toAbsolutePath().toString());
            if (Files.exists(filePath)) {
                try {
                    List<RoomInfo> roomInfos = new ArrayList<>();
                    LocalDate checkInDate = null;
                    LocalDate checkOutDate = null;
                    String contactName = null;
                    String contactEmail = null;
                    String contactPhone = null;
                    try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                        checkInDate = LocalDate.parse(reader.readLine());
                        checkOutDate = LocalDate.parse(reader.readLine());
                        contactName = reader.readLine();
                        contactEmail = reader.readLine();
                        contactPhone = reader.readLine();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            roomInfos.add(RoomInfo.fromString(line));
                        }
                    }
                    Reservation reservation = new Reservation(checkInDate, checkOutDate, roomInfos, contactName, contactEmail, contactPhone);
                    reservations.put(reservationNumber, reservation);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Error parsing reservation file: " + filePath);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Reservation file not found: " + filePath.toAbsolutePath().toString());
            }
        }
        return reservations.get(reservationNumber);
    }

    public Map<String, Reservation> getAllReservations() {
        return new HashMap<>(reservations);
    }

    public void showRoomSelectionDialog(String reservationNumber) {
        List<RoomInfo> availableRooms = HotelRooms.getRooms();
        ChoiceDialog<RoomInfo> dialog = new ChoiceDialog<>(availableRooms.get(0), availableRooms);
        dialog.setTitle("Select Room");
        dialog.setHeaderText("Select a room to add to the reservation");
        dialog.setContentText("Available Rooms:");

        Optional<RoomInfo> result = dialog.showAndWait();
        result.ifPresent(roomInfo -> addRoomToReservation(reservationNumber, roomInfo));
    }

    private void addRoomToReservation(String reservationNumber, RoomInfo roomInfo) {
        Reservation reservation = getReservation(reservationNumber);
        if (reservation != null) {
            reservation.getRooms().add(roomInfo);
            addReservation(reservationNumber, reservation);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Room added successfully!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private synchronized void saveReservationToFile(String reservationNumber, Reservation reservation) {
        try {
            if (!Files.exists(reservationDirectory)) {
                Files.createDirectory(reservationDirectory);
                System.out.println("Created reservation directory: " + reservationDirectory.toAbsolutePath().toString());
            }
            Path filePath = reservationDirectory.resolve(reservationNumber + ".txt");
            System.out.println("Saving reservation to file: " + filePath.toAbsolutePath().toString());
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write(reservation.getCheckInDate().toString());
                writer.newLine();
                writer.write(reservation.getCheckOutDate().toString());
                writer.newLine();
                writer.write(reservation.getContactName());
                writer.newLine();
                writer.write(reservation.getContactEmail());
                writer.newLine();
                writer.write(reservation.getContactPhone());
                writer.newLine();
                for (RoomInfo roomInfo : reservation.getRooms()) {
                    writer.write(roomInfo.toString());
                    writer.newLine();
                }
            }
            System.out.println("Saved reservation to file: " + filePath.toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        if (Files.exists(reservationDirectory)) {
            try {
                Files.list(reservationDirectory).forEach(filePath -> {
                    if (filePath.toString().endsWith(".txt")) {
                        String reservationNumber = filePath.getFileName().toString().replace(".txt", "");
                        List<RoomInfo> roomInfos = new ArrayList<>();
                        LocalDate checkInDate = null;
                        LocalDate checkOutDate = null;
                        String contactName = null;
                        String contactEmail = null;
                        String contactPhone = null;
                        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                            System.out.println("Reading file: " + filePath);
                            checkInDate = LocalDate.parse(reader.readLine());
                            checkOutDate = LocalDate.parse(reader.readLine());
                            contactName = reader.readLine();
                            contactEmail = reader.readLine();
                            contactPhone = reader.readLine();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("Read line: " + line);
                                roomInfos.add(RoomInfo.fromString(line));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.err.println("Error parsing reservation file: " + filePath);
                            e.printStackTrace();
                        }
                        Reservation reservation = new Reservation(checkInDate, checkOutDate, roomInfos, contactName, contactEmail, contactPhone);
                        reservations.put(reservationNumber, reservation);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Reservation directory not found: " + reservationDirectory.toAbsolutePath().toString());
        }
    }}