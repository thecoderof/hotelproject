package com.example.hotel20;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reservation Calendar");

        VBox layout = new VBox(10);

        DatePicker datePicker = new DatePicker();
        layout.getChildren().add(datePicker);

        Map<String, Reservation> reservationsMap = ReservationManager.getInstance().getAllReservations();
        List<Reservation> reservations = reservationsMap.values().stream().collect(Collectors.toList());

        datePicker.setDayCellFactory(new ReservationDateCellFactory(reservations));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static class ReservationDateCellFactory implements Callback<DatePicker, DateCell> {
        private final List<Reservation> reservations;

        public ReservationDateCellFactory(List<Reservation> reservations) {
            this.reservations = reservations;
        }

        @Override
        public DateCell call(DatePicker param) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setGraphic(null);
                        setTextFill(null);
                        for (Reservation reservation : reservations) {
                            if (!item.isBefore(reservation.getCheckInDate()) && !item.isAfter(reservation.getCheckOutDate())) {
                                setTooltip(new javafx.scene.control.Tooltip(reservation.getDetails()));
                                setGraphic(new javafx.scene.shape.Rectangle(10, 10, javafx.scene.paint.Color.RED));
                                setOnMouseClicked(event -> showReservationDetails(reservation));
                                break;
                            }
                        }
                    }
                }

                private void showReservationDetails(Reservation reservation) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Reservation Details");
                    alert.setHeaderText("Details for reservation: " + reservation.getReservationNumber());
                    alert.setContentText(reservation.getDetails());
                    alert.showAndWait();
                }
            };
        }
    }
}