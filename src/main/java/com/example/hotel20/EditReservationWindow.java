package com.example.hotel20;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EditReservationWindow {
    private String reservationNumber;
    private ObservableList<RoomInfo> reservation;
    private ObservableList<RoomInfo> availableRooms;
    private DatePicker checkInDatePicker;
    private DatePicker checkOutDatePicker;
    private Label totalRoomsLabel;
    private Label totalPriceLabel;

    // Fields for contact details
    private TextField contactNameField;
    private TextField contactEmailField;
    private TextField contactPhoneField;

    public EditReservationWindow(String reservationNumber, List<RoomInfo> reservation, LocalDate checkInDate, LocalDate checkOutDate, String contactName, String contactEmail, String contactPhone) {
        this.reservationNumber = reservationNumber;
        this.reservation = FXCollections.observableArrayList(reservation);
        this.availableRooms = FXCollections.observableArrayList(HotelRooms.getRooms());
        this.checkInDatePicker = new DatePicker(checkInDate);
        this.checkOutDatePicker = new DatePicker(checkOutDate);
        this.contactNameField = new TextField(contactName);
        this.contactEmailField = new TextField(contactEmail);
        this.contactPhoneField = new TextField(contactPhone);
    }

    public void show() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TableView<RoomInfo> reservationTable = createReservationTable();
        TableView<RoomInfo> availableRoomsTable = createAvailableRoomsTable();

        HBox dateBox = new HBox(10);
        dateBox.getChildren().addAll(new Label("Check-in Date:"), checkInDatePicker, new Label("Check-out Date:"), checkOutDatePicker);

        totalRoomsLabel = new Label();
        totalPriceLabel = new Label();
        updateTotalLabels();

        Button addRoomButton = new Button("Add Room");
        addRoomButton.setOnAction(e -> {
            RoomInfo selectedRoom = availableRoomsTable.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                reservation.add(selectedRoom);
                updateTotalLabels();
            }
        });

        Button deleteRoomButton = new Button("Delete Room");
        deleteRoomButton.setOnAction(e -> {
            RoomInfo selectedRoom = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                reservation.remove(selectedRoom);
                updateTotalLabels();
            }
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> saveChanges());

        layout.getChildren().addAll(
                new Label("Edit Reservation: " + reservationNumber),
                dateBox,
                new HBox(10, reservationTable, availableRoomsTable),
                new HBox(10, addRoomButton, deleteRoomButton),
                totalRoomsLabel,
                totalPriceLabel,
                new Label("Contact Details"),
                new Label("Name:"), contactNameField,
                new Label("Email:"), contactEmailField,
                new Label("Phone:"), contactPhoneField,
                saveButton
        );

        Scene scene = new Scene(layout, 1000, 800);
        stage.setTitle("Edit Reservation");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<RoomInfo> createReservationTable() {
        TableView<RoomInfo> table = new TableView<>(reservation);
        TableColumn<RoomInfo, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        TableColumn<RoomInfo, Integer> guestsColumn = new TableColumn<>("Max Guests");
        guestsColumn.setCellValueFactory(cellData -> cellData.getValue().maxGuestsProperty().asObject());
        TableColumn<RoomInfo, Double> priceColumn = new TableColumn<>("Price per Night");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        TableColumn<RoomInfo, Boolean> balconyColumn = new TableColumn<>("Balcony");
        balconyColumn.setCellValueFactory(cellData -> cellData.getValue().balconyProperty());
        TableColumn<RoomInfo, Boolean> acColumn = new TableColumn<>("AC");
        acColumn.setCellValueFactory(cellData -> cellData.getValue().acProperty());
        table.getColumns().addAll(nameColumn, guestsColumn, priceColumn, balconyColumn, acColumn);
        return table;
    }

    private TableView<RoomInfo> createAvailableRoomsTable() {
        TableView<RoomInfo> table = new TableView<>(availableRooms);
        TableColumn<RoomInfo, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        TableColumn<RoomInfo, Integer> guestsColumn = new TableColumn<>("Max Guests");
        guestsColumn.setCellValueFactory(cellData -> cellData.getValue().maxGuestsProperty().asObject());
        TableColumn<RoomInfo, Double> priceColumn = new TableColumn<>("Price per Night");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        TableColumn<RoomInfo, Boolean> balconyColumn = new TableColumn<>("Balcony");
        balconyColumn.setCellValueFactory(cellData -> cellData.getValue().balconyProperty());
        TableColumn<RoomInfo, Boolean> acColumn = new TableColumn<>("AC");
        acColumn.setCellValueFactory(cellData -> cellData.getValue().acProperty());
        table.getColumns().addAll(nameColumn, guestsColumn, priceColumn, balconyColumn, acColumn);
        return table;
    }

    private void updateTotalLabels() {
        int totalRooms = reservation.size();
        double totalPrice = reservation.stream().mapToDouble(RoomInfo::getPrice).sum();
        totalRoomsLabel.setText("Total Rooms: " + totalRooms);
        totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
    }

    private void saveChanges() {
        // Validate date range
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();
        if (checkInDate != null && checkOutDate != null) {
            // Save the updated reservation with contact details
            Reservation reservation = new Reservation(
                    checkInDate, checkOutDate, new ArrayList<>(this.reservation),
                    contactNameField.getText(), contactEmailField.getText(), contactPhoneField.getText());
            ReservationManager.getInstance().addReservation(reservationNumber, reservation);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Reservation Updated");
            alert.setContentText("The reservation has been successfully updated.");
            alert.showAndWait();
            ((Stage) totalRoomsLabel.getScene().getWindow()).close(); // Close the edit window
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Dates");
            alert.setContentText("Please select valid check-in and check-out dates.");
            alert.showAndWait();
        }
    }
}