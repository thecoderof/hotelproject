package com.example.hotel20;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoomStatusWindow {
    private Stage stage;
    private ObservableList<RoomInfo> roomInfos;
    private String role;

    public RoomStatusWindow(Stage stage, Reservation reservation, String role) {
        this.stage = stage;
        this.roomInfos = FXCollections.observableArrayList(reservation.getRooms());
        this.role = role;
        initializeUI();
    }

    private void initializeUI() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("vbox");
        layout.setId("room-status-window");

        Label titleLabel = new Label("Room Statuses");
        titleLabel.getStyleClass().add("label");

        TableView<RoomInfo> table = new TableView<>(roomInfos);
        table.getStyleClass().add("table-view");

        TableColumn<RoomInfo, String> nameColumn = new TableColumn<>("Room Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.getStyleClass().add("table-column-header");

        TableColumn<RoomInfo, String> statusColumn = new TableColumn<>("Cleaning Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.getStyleClass().add("table-column-header");

        TableColumn<RoomInfo, Double> minibarColumn = new TableColumn<>("Minibar Payments");
        minibarColumn.setCellValueFactory(new PropertyValueFactory<>("minibarPayments"));
        minibarColumn.getStyleClass().add("table-column-header");

        table.getColumns().addAll(nameColumn, statusColumn, minibarColumn);

        if ("worker".equals(role)) {
            TableColumn<RoomInfo, Void> editColumn = new TableColumn<>("Actions");
            editColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Edit");

                {
                    editButton.getStyleClass().add("button");
                    editButton.setOnAction(event -> {
                        RoomInfo roomInfo = getTableView().getItems().get(getIndex());
                        showEditDialog(roomInfo);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(editButton);
                    }
                }
            });
            table.getColumns().add(editColumn);
        }

        layout.getChildren().addAll(titleLabel, table);

        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Room Status");
        stage.setScene(scene);
    }

    private void showEditDialog(RoomInfo roomInfo) {
        Stage dialogStage = new Stage();
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getStyleClass().add("vbox");

        Label nameLabel = new Label("Room: " + roomInfo.getName());
        nameLabel.getStyleClass().add("label");

        Label statusLabel = new Label("Status:");
        statusLabel.getStyleClass().add("label");

        TextField statusField = new TextField(roomInfo.getStatus());
        statusField.getStyleClass().add("text-field");

        Label minibarLabel = new Label("Minibar Payments:");
        minibarLabel.getStyleClass().add("label");

        TextField minibarField = new TextField(String.valueOf(roomInfo.getMinibarPayments()));
        minibarField.getStyleClass().add("text-field");

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("button");
        saveButton.setOnAction(e -> {
            roomInfo.setStatus(statusField.getText());
            roomInfo.setMinibarPayments(Double.parseDouble(minibarField.getText()));
            dialogStage.close();
        });

        dialogLayout.getChildren().addAll(nameLabel, statusLabel, statusField, minibarLabel, minibarField, saveButton);

        Scene dialogScene = new Scene(dialogLayout, 500, 400);
        dialogScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogStage.setTitle("Edit Room");
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    public void show() {
        stage.show();
    }
}