package com.example.hotel20;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginPage extends Application {
    private TextField userTextField;
    private PasswordField pwBox;
    private int loginAttempts = 0;
    private final Path accountsFile = Paths.get("accounts.enc");
    private final Path keyFile = Paths.get("secret.key");
    private SecretKey secretKey;

    public LoginPage() throws Exception {
        this.secretKey = loadOrGenerateKey();
        addAdminAccount();  // Ensure admin account exists
        addWorkerAccount(); // Ensure worker account exists
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Form");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.getStyleClass().add("grid-pane");

        Label userName = new Label("Username:");
        userName.getStyleClass().add("label");
        grid.add(userName, 0, 0);

        userTextField = new TextField();
        userTextField.getStyleClass().add("text-field");
        grid.add(userTextField, 1, 0);

        Label pw = new Label("Password:");
        pw.getStyleClass().add("label");
        grid.add(pw, 0, 1);

        pwBox = new PasswordField();
        pwBox.getStyleClass().add("text-field");
        grid.add(pwBox, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");
        grid.add(loginButton, 1, 2);

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
        grid.add(registerButton, 1, 3);

        loginButton.setOnAction(actionEvent -> {
            if (loginAttempts >= 3) {
                showAlert(Alert.AlertType.ERROR, "Error Dialog", "Too Many Failed Attempts", "Please try again later or contact support.");
            } else {
                Map<String, String> accounts = loadAccounts();
                String username = userTextField.getText();
                String password = pwBox.getText();

                if (accounts.containsKey(username) && accounts.get(username).equals(encrypt(password))) {
                    primaryStage.close();
                    String role = "customer";
                    if ("admin".equals(username)) {
                        role = "admin";
                    } else if ("worker".equals(username)) {
                        role = "worker";
                    }
                    MainMenu mainMenu = new MainMenu(new Stage(), "admin".equals(username), role);
                    mainMenu.show();
                } else {
                    loginAttempts++;
                    showAlert(Alert.AlertType.ERROR, "Error Dialog", null, "Incorrect login details");
                }
            }
        });

        registerButton.setOnAction(actionEvent -> openRegistrationForm());

        Scene scene = new Scene(grid, 500, 475);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addAdminAccount() throws Exception {
        Map<String, String> accounts = loadAccounts();
        String adminUsername = "admin";
        String adminPassword = "admin123!";
        if (!accounts.containsKey(adminUsername)) {
            accounts.put(adminUsername, encrypt(adminPassword));
            saveAccount(adminUsername, encrypt(adminPassword));
        }
    }

    private void addWorkerAccount() throws Exception {
        Map<String, String> accounts = loadAccounts();
        String workerUsername = "worker";
        String workerPassword = "worker123!";
        if (!accounts.containsKey(workerUsername)) {
            accounts.put(workerUsername, encrypt(workerPassword));
            saveAccount(workerUsername, encrypt(workerPassword));
        }
    }

    public Map<String, String> loadAccounts() {
        Map<String, String> accounts = new HashMap<>();
        if (Files.exists(accountsFile)) {
            try (BufferedReader reader = Files.newBufferedReader(accountsFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = decrypt(line).split(":");
                    if (parts.length == 2) {
                        accounts.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

    private void openRegistrationForm() {
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Register New Account");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
        grid.getStyleClass().add("grid-pane");

        Label userName = new Label("Username:");
        userName.getStyleClass().add("label");
        grid.add(userName, 0, 0);

        TextField userTextField = new TextField();
        userTextField.getStyleClass().add("text-field");
        grid.add(userTextField, 1, 0);

        Label pw = new Label("Password:");
        pw.getStyleClass().add("label");
        grid.add(pw, 0, 1);

        PasswordField pwBox = new PasswordField();
        pwBox.getStyleClass().add("text-field");
        grid.add(pwBox, 1, 1);

        Label confirmPw = new Label("Confirm Password:");
        confirmPw.getStyleClass().add("label");
        grid.add(confirmPw, 0, 2);

        PasswordField confirmPwBox = new PasswordField();
        confirmPwBox.getStyleClass().add("text-field");
        grid.add(confirmPwBox, 1, 2);

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
        grid.add(registerButton, 1, 3);

        registerButton.setOnAction(actionEvent -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            String confirmPassword = confirmPwBox.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter all fields");
            } else if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Passwords do not match");
            } else if (!isValidPassword(password)) {
                showAlert(Alert.AlertType.ERROR, "Form Error!", "Password must be at least 8 characters long and contain at least one special character");
            } else {
                Map<String, String> accounts = loadAccounts();
                if (accounts.containsKey(username)) {
                    showAlert(Alert.AlertType.ERROR, "Form Error!", "Username already exists");
                } else {
                    accounts.put(username, encrypt(password));
                    saveAccount(username, encrypt(password));
                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Account created successfully");
                    registrationStage.close();
                }
            }
        });

        Scene scene = new Scene(grid, 500, 475);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        registrationStage.setScene(scene);
        registrationStage.show();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
    }

    private void saveAccount(String username, String encryptedPassword) {
        try (BufferedWriter writer = Files.newBufferedWriter(accountsFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(encrypt(username + ":" + encryptedPassword));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        showAlert(alertType, title, null, message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String message) {Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private SecretKey loadOrGenerateKey() throws Exception {
        if (Files.exists(keyFile)) {
            byte[] keyBytes = Files.readAllBytes(keyFile);
            return new SecretKeySpec(keyBytes, "AES");
        } else {
            SecretKey key = generateKey();
            Files.write(keyFile, key.getEncoded());
            return key;
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.toString());
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.toString());
        }
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}