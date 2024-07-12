package com.husseinamro.exchange;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Parent implements Initializable, OnPageCompleteListener {
    public BorderPane borderPane;
    public Button transactionButton;
    public Button loginButton;
    public Button registerButton;
    public Button logoutButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {updateNavigation(); }
    public void ratesSelected() {
        swapContent(Section.RATES);
    }
    public void transactionsSelected() {
        swapContent(Section.TRANSACTIONS);
    }
    public void loginSelected() {
        swapContent(Section.LOGIN);
    }
    public void registerSelected() {
        swapContent(Section.REGISTER);
    }
    public void logoutSelected() {
        Authentication.getInstance().deleteToken();
        swapContent(Section.RATES);
    }
    private void swapContent(Section section) {
        try {
            URL url = getClass().getResource(section.getResource());
            FXMLLoader loader = new FXMLLoader(url);
            borderPane.setCenter(loader.load());
            if (section.doesComplete()) {
                ((PageCompleter) loader.getController()).setOnPageCompleteListener(this);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        updateNavigation();
    }
    private enum Section {
        RATES, TRANSACTIONS, LOGIN, REGISTER;
        public String getResource() {
            return switch (this) {
                case RATES ->
                        "/com/husseinamro/exchange/rates/layout.fxml";
                case TRANSACTIONS ->
                        "/com/husseinamro/exchange/transactions/transactions.fxml";
                case LOGIN ->
                        "/com/husseinamro/exchange/login/login.fxml";
                case REGISTER ->
                        "/com/husseinamro/exchange/register/register.fxml";
                default -> null;
            };
        }
        public boolean doesComplete() {
            return switch (this) {
                case LOGIN, REGISTER -> true;
                default -> false;
            };
        }
    }
    @Override
    public void onPageCompleted() {
        swapContent(Section.RATES);
    }
    private void updateNavigation() {
        boolean authenticated = Authentication.getInstance().getToken() !=
                null;
        transactionButton.setManaged(authenticated);
        transactionButton.setVisible(authenticated);
        loginButton.setManaged(!authenticated);
        loginButton.setVisible(!authenticated);
        registerButton.setManaged(!authenticated);
        registerButton.setVisible(!authenticated);
        logoutButton.setManaged(authenticated);
        logoutButton.setVisible(authenticated);
    }
}