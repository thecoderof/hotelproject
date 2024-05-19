package com.example.hotel20;

import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.List;

public class ReservationDateCell extends DateCell {

    private final List<Reservation> reservations;

    public ReservationDateCell(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            for (Reservation reservation : reservations) {
                if (!item.isBefore(reservation.getCheckInDate()) && !item.isAfter(reservation.getCheckOutDate())) {
                    setTooltip(new Tooltip(reservation.getDetails()));
                    setGraphic(new Rectangle(10, 10, Color.RED));
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
}