package com.example.hotel20;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;
import java.io.*;
import java.nio.file.*;

public class HotelRooms extends VBox {

    private static final List<Room> rooms = Arrays.asList(
            new Room(true, "king", true, 2, 150.00, 10, "King Balcony Suite AC"),
            new Room(false, "normal", false, 1, 75.00, 15, "Standard Solo"),
            new Room(true, "normal", true, 3, 120.00, 5, "Standard Triple Balcony AC"),
            new Room(false, "king", true, 4, 200.00, 8, "King Family Suite AC"),
            new Room(true, "king", false, 5, 250.00, 10, "Royal King Quintet Balcony")
    );

    public HotelRooms() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);

        TextField totalGuestsField = new TextField();
        TextField preferredRoomCapacityField = new TextField();
        Button submitButton = new Button("Find rooms");

        DatePicker checkInDatePicker = new DatePicker(LocalDate.now());
        DatePicker checkOutDatePicker = new DatePicker(LocalDate.now().plusDays(1));

        HBox datePickersBox = new HBox(10);
        datePickersBox.getChildren().addAll(
                new Label("Check-in Date:"), checkInDatePicker,
                new Label("Check-out Date:"), checkOutDatePicker
        );

        TableView<RoomInfo> optimalTable = new TableView<>();
        TableView<RoomInfo> preferredTable = new TableView<>();
        configureTableColumns(optimalTable);
        configureTableColumns(preferredTable);

        Button selectOptimalButton = new Button("Select Optimal");
        Button selectPreferredButton = new Button("Select Preferred");

        selectOptimalButton.setOnAction(e -> {
            ObservableList<RoomInfo> selectedRooms = optimalTable.getItems();
            if (!selectedRooms.isEmpty()) {
                showConfirmation(selectedRooms, "Optimal", new Stage(), checkInDatePicker.getValue(), checkOutDatePicker.getValue());
            }
        });

        selectPreferredButton.setOnAction(e -> {
            ObservableList<RoomInfo> selectedRooms = preferredTable.getItems();
            if (!selectedRooms.isEmpty()) {
                showConfirmation(selectedRooms, "Preferred", new Stage(), checkInDatePicker.getValue(), checkOutDatePicker.getValue());
            }
        });

        submitButton.setOnAction(event -> {
            int totalGuests = Integer.parseInt(totalGuestsField.getText());
            int preferredRoomCapacity = Integer.parseInt(preferredRoomCapacityField.getText());
            LocalDate checkInDate = checkInDatePicker.getValue();
            LocalDate checkOutDate = checkOutDatePicker.getValue();
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            List<Room> optimalRooms = findOptimalRoomCombination(totalGuests);
            List<Room> preferredRooms = findPreferredRoomCombination(totalGuests, preferredRoomCapacity);

            optimalTable.setItems(FXCollections.observableArrayList(convertToRoomInfo(optimalRooms)));
            preferredTable.setItems(FXCollections.observableArrayList(convertToRoomInfo(preferredRooms)));

            this.getChildren().setAll(
                    new Label("Total Guests:"), totalGuestsField,
                    new Label("Preferred Room Capacity:"), preferredRoomCapacityField,
                    datePickersBox, submitButton,
                    new HBox(20, optimalTable, preferredTable),
                    selectOptimalButton, selectPreferredButton
            );
        });

        this.getChildren().addAll(
                new Label("Total Guests:"), totalGuestsField,
                new Label("Preferred Room Capacity:"), preferredRoomCapacityField,
                datePickersBox, submitButton
        );
    }

    private void configureTableColumns(TableView<RoomInfo> table) {
        TableColumn<RoomInfo, String> roomNameColumn = new TableColumn<>("Room Name");
        roomNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<RoomInfo, Integer> guestsColumn = new TableColumn<>("Max Guests");
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("maxGuests"));
        TableColumn<RoomInfo, Double> costColumn = new TableColumn<>("Room Price per Night");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.getColumns().addAll(roomNameColumn, guestsColumn, costColumn);
    }

    private List<RoomInfo> convertToRoomInfo(List<Room> rooms) {
        return rooms.stream().map(room -> new RoomInfo(
                room.getName(), room.getMaxGuests(), room.getPrice(), room.hasBalcony(), room.hasAC()
        )).collect(Collectors.toList());
    }

    private void showConfirmation(ObservableList<RoomInfo> selectedRooms, String optionTitle, Stage previousStage, LocalDate checkInDate, LocalDate checkOutDate) {
        Stage confirmationStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label header = new Label("Confirmation for " + optionTitle + " Selection");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        double totalPrice = 0;
        int totalRooms = 0;
        for (RoomInfo room : selectedRooms) {
            detailsArea.appendText(String.format("%s - Max Guests: %d, Balcony: %b, AC: %b, Price per Night: $%.2f\n",
                    room.getName(), room.getMaxGuests(), room.hasBalcony(), room.hasAC(), room.getPrice()));
            totalPrice += room.getPrice();
            totalRooms++;
        }

        Label totalRoomsLabel = new Label("Total Rooms: " + totalRooms);
        Label totalPriceLabel = new Label(String.format("Total Price: $%.2f", totalPrice));

        // Add contact details form
        TextField contactNameField = new TextField();
        contactNameField.setPromptText("Contact Name");
        TextField contactEmailField = new TextField();
        contactEmailField.setPromptText("Contact Email");
        TextField contactPhoneField = new TextField();
        contactPhoneField.setPromptText("Contact Phone");

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            String reservationNumber = generateReservationNumber();
            List<RoomInfo> selectedRoomsList = new ArrayList<>(selectedRooms);
            String contactName = contactNameField.getText();
            String contactEmail = contactEmailField.getText();
            String contactPhone = contactPhoneField.getText();
            Reservation reservation = new Reservation(checkInDate, checkOutDate, selectedRoomsList, contactName, contactEmail, contactPhone);
            ReservationManager.getInstance().addReservation(reservationNumber, reservation);
            confirmationStage.close();
            previousStage.close();
            showReservationConfirmed(reservationNumber);
        });

        layout.getChildren().addAll(header, detailsArea, totalRoomsLabel, totalPriceLabel,
                new Label("Contact Details"), contactNameField, contactEmailField, contactPhoneField, confirmButton);

        Scene scene = new Scene(layout, 450, 400);
        confirmationStage.setTitle("Room Selection Confirmation");
        confirmationStage.setScene(scene);
        confirmationStage.show();
    }

    private String generateReservationNumber() {
        String number;
        do {
            number = "RSV-" + UUID.randomUUID().toString().substring(0, 8);
        } while (ReservationManager.getInstance().getReservation(number) != null);
        return number;
    }

    private void showReservationConfirmed(String reservationNumber) {
        Stage stage = new Stage();
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        Label confirmedLabel = new Label("Reservation Confirmed");
        confirmedLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label reservationLabel = new Label("Reservation Number: " + reservationNumber);
        reservationLabel.setStyle("-fx-font-size: 16px;");

        Button copyButton = new Button("Copy the number");
        copyButton.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(reservationNumber);
            clipboard.setContent(content);
        });

        Button homeButton = new Button("Home");
        homeButton.setOnAction(e -> {
            stage.close(); // Close the current window
            Stage mainStage = new Stage(); // Create a new stage for the main menu
            MainMenu mainMenu = new MainMenu(mainStage, false, "customer"); // Assuming "customer" as default role for now
            mainMenu.show(); // Show the MainMenu
        });

        layout.getChildren().addAll (confirmedLabel, reservationLabel, copyButton, homeButton);
        Scene scene = new Scene(layout, 300, 250);
        stage.setTitle("Confirmation");
        stage.setScene(scene);
        stage.show();
    }

    private List<Room> findOptimalRoomCombination(int totalGuests) {
        List<Room> optimalRooms = new ArrayList<>();
        rooms.sort(Comparator.comparingInt(Room::getMaxGuests).reversed());
        int remainingGuests = totalGuests;
        for (Room room : rooms) {
            while (remainingGuests >= room.getMaxGuests()) {
                optimalRooms.add(room);
                remainingGuests -= room.getMaxGuests();
            }
            if (remainingGuests <= 0) { break; }
        }
        return optimalRooms;
    }

    private List<Room> findPreferredRoomCombination(int totalGuests, int preferredCapacity) {
        List<Room> optimalRooms = new ArrayList<>();
        int remainingGuests = totalGuests;
        for (Room room : rooms) {
            if (room.getMaxGuests() == preferredCapacity) {
                while (remainingGuests >= room.getMaxGuests()) {
                    optimalRooms.add(room);
                    remainingGuests -= room.getMaxGuests();
                }
            }
            if (remainingGuests <= 0) break;
        }
        if (remainingGuests > 0) {
            for (Room room : rooms) {
                while (remainingGuests >= room.getMaxGuests()) {
                    optimalRooms.add(room);
                    remainingGuests -= room.getMaxGuests();
                }
                if (remainingGuests <= 0) break;
            }
        }
        return optimalRooms;
    }

    public static List<RoomInfo> getRooms() {
        return rooms.stream().map(room -> new RoomInfo(
                room.getName(), room.getMaxGuests(), room.getPrice(), room.hasBalcony(), room.hasAC()
        )).collect(Collectors.toList());
    }}