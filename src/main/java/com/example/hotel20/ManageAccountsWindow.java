package com.example.hotel20;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageAccountsWindow {
    private Stage stage;
    private TableView<Account> accountsTable;
    private ObservableList<Account> accounts;

    public ManageAccountsWindow(Stage stage) {
        this.stage = stage;
        initializeUI();
        loadAccounts();
    }

    private void initializeUI() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        accountsTable = new TableView<>();
        TableColumn<Account, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        TableColumn<Account, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        accountsTable.getColumns().addAll(usernameColumn, passwordColumn);

        Button deleteButton = new Button("Delete Account");
        deleteButton.setOnAction(e -> deleteSelectedAccount());

        layout.getChildren().addAll(accountsTable, deleteButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setTitle("Manage Accounts");
        stage.setScene(scene);
    }
    private void loadAccounts() {
        accounts = FXCollections.observableArrayList();
        LoginPage loginPage;
        try {
            loginPage = new LoginPage();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Map<String, String> accountsMap = loginPage.loadAccounts();
        accountsMap.forEach((username, password) -> accounts.add(new Account(username, loginPage.decrypt(password))));
        accountsTable.setItems(accounts);
    }

    private void deleteSelectedAccount() {
        Account selectedAccount = accountsTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            accounts.remove(selectedAccount);
            saveAccounts();
        }
    }

    private void saveAccounts() {
        try {
            LoginPage loginPage = new LoginPage();
            List<String> accountLines = accounts.stream()
                    .map(account -> loginPage.encrypt(account.getUsername() + ":" + loginPage.encrypt(account.getPassword())))
                    .collect(Collectors.toList());
            Files.write(Paths.get("accounts.enc"), accountLines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        stage.show();
    }}

class Account {
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    public Account(String username, String password) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }}