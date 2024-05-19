package com.example.hotel20;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu {
    private Stage stage;
    private boolean isAdmin;
    private String role;
    private VBox layout;

    public MainMenu(Stage stage, boolean isAdmin, String role) {
        this.stage = stage;
        this.isAdmin = isAdmin;
        this.role = role;
        initializeUI();
    }

    private void initializeUI() {
        layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");
        layout.setId("main-menu");

        Button newReservationButton = new Button("New reservation");
        newReservationButton.getStyleClass().add("button");
        newReservationButton.setOnAction(e -> openHotelRooms());

        Button manageReservationsButton = new Button("Manage Reservations");
        manageReservationsButton.getStyleClass().add("button");
        manageReservationsButton.setOnAction(e -> openManageReservations());

        Button viewCalendarButton = new Button("View Calendar");
        viewCalendarButton.getStyleClass().add("button");
        viewCalendarButton.setOnAction(e -> openCalendarView());

        Button checkRoomStatusButton = new Button("Check Room Status");
        checkRoomStatusButton.getStyleClass().add("button");
        checkRoomStatusButton.setOnAction(e -> openRoomStatus());

        layout.getChildren().addAll(newReservationButton, manageReservationsButton, viewCalendarButton, checkRoomStatusButton);

        if (isAdmin) {
            Button manageAccountsButton = new Button("Manage Accounts");
            manageAccountsButton.getStyleClass().add("button");
            manageAccountsButton.setOnAction(e -> openManageAccounts());
            layout.getChildren().add(manageAccountsButton);
        }

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Main Menu");
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    public VBox getLayout() {
        return layout;
    }

    private void openHotelRooms() {
        HotelRooms hotelRooms = new HotelRooms();
        Scene scene = new Scene(hotelRooms, 700, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Reservation System");
        stage.setScene(scene);
    }

    private void openManageReservations() {
        Stage manageStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        TextField reservationNumberField = new TextField();
        reservationNumberField.setPromptText("Enter Reservation Number");
        reservationNumberField.getStyleClass().add("text-field");

        Button manageReservationButton = new Button("Manage Reservation");
        manageReservationButton.getStyleClass().add("button");
        manageReservationButton.setOnAction(e -> {
            String reservationNumber = reservationNumberField.getText();
            if (!reservationNumber.isEmpty()) {
                Reservation reservation = ReservationManager.getInstance().getReservation(reservationNumber);
                if (reservation != null && !reservation.getRooms().isEmpty()) {
                    openEditReservationWindow(reservationNumber, reservation);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No Reservation Found");
                    alert.setContentText("No reservation found for the number: " + reservationNumber);
                    alert.showAndWait();
                }
            }
        });
        layout.getChildren().addAll(new Label("Reservation Number:"), reservationNumberField, manageReservationButton);

        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        manageStage.setTitle("Manage Reservations");
        manageStage.setScene(scene);
        manageStage.show();
    }

    private void openEditReservationWindow(String reservationNumber, Reservation reservation) {
        EditReservationWindow editWindow = new EditReservationWindow(
                reservationNumber,
                reservation.getRooms(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getContactName(),
                reservation.getContactEmail(),
                reservation.getContactPhone()
        );
        editWindow.show();
    }

    private void openCalendarView() {
        CalendarView calendarView = new CalendarView();
        try {
            calendarView.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openManageAccounts() {
        ManageAccountsWindow manageAccountsWindow = new ManageAccountsWindow(new Stage());
        manageAccountsWindow.show();
    }

    private void openRoomStatus() {
        Stage roomStatusStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");

        TextField reservationNumberField = new TextField();
        reservationNumberField.setPromptText("Enter Reservation Number");
        reservationNumberField.getStyleClass().add("text-field");

        Button checkStatusButton = new Button("Check Status");
        checkStatusButton.getStyleClass().add("button");
        checkStatusButton.setOnAction(e -> {
            String reservationNumber = reservationNumberField.getText();
            if (!reservationNumber.isEmpty()) {
                Reservation reservation = ReservationManager.getInstance().getReservation(reservationNumber);
                if (reservation != null && !reservation.getRooms().isEmpty()) {
                    openRoomStatusWindow(reservationNumber, reservation);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("No Reservation Found");
                    alert.setContentText("No reservation found for the number: " + reservationNumber);
                    alert.showAndWait();
                }
            }
        });

        layout.getChildren().addAll(new Label("Reservation Number:"), reservationNumberField, checkStatusButton);
        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        roomStatusStage.setTitle("Room Status");
        roomStatusStage.setScene(scene);
        roomStatusStage.show();
    }

    private void openRoomStatusWindow(String reservationNumber, Reservation reservation) {
        RoomStatusWindow roomStatusWindow = new RoomStatusWindow(new Stage(), reservation, role);
        roomStatusWindow.show();
    }}