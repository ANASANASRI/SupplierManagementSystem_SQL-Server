package com.afm.suppliermanagementsystem.controller;

import com.afm.suppliermanagementsystem.HelloApplication;
import com.afm.suppliermanagementsystem.model.Fournisseur;
import com.afm.suppliermanagementsystem.services.FournisseurService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuAdmins {
    @FXML
    private TextField txtnumSIREN;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtAdresse;
    @FXML
    private TextField txtNumeroTelephone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNumeroCompteBancaire;

    @FXML
    private TableView<Fournisseur> tableViewFournisseur;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpadte;
    @FXML
    private TableColumn<Fournisseur, Integer> tableColumnnumSIREN;
    @FXML
    private TableColumn<Fournisseur, String> tableColumnNom;
    @FXML
    private TableColumn<Fournisseur, String> tableColumnAdresse;
    @FXML
    private TableColumn<Fournisseur, String> tableColumnNumeroTelephone;
    @FXML
    private TableColumn<Fournisseur, String> tableColumnEmail;
    @FXML
    private TableColumn<Fournisseur, String> tableColumnNumeroCompteBancaire;
    @FXML
    private TableColumn<Fournisseur, Button> tableColumnPaiement;
    @FXML
    private TableColumn<Fournisseur, Button> tableColumnREMOVE;
    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtSearch;

    private ObservableList<Fournisseur> suppliersList;
    private ObservableList<Fournisseur> filteredList;

    private FournisseurService fournisseurService;

    public MenuAdmins() {
        fournisseurService = new FournisseurService();
    }


    @FXML
    private void initialize() {
        populateFournisseurTable();
        //
        tableViewFournisseur.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                handleRowClick();
            }
        });
        //
        filteredList = FXCollections.observableArrayList(fournisseurService.findAll());

        // Add a text change listener to the search text field
        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filterTable(newValue);
            }
        });


    }

        /*/////////////////*/

    public void populateFournisseurTable() {
        List<Fournisseur> fournisseurs = fournisseurService.findAll();

        if (fournisseurs != null) {
            fournisseurs.forEach(System.out::println);
            System.out.println("Fournisseur!");

            //
            tableColumnnumSIREN.setCellValueFactory(new PropertyValueFactory<>("numSIREN"));
            tableColumnNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            tableColumnAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            tableColumnNumeroTelephone.setCellValueFactory(new PropertyValueFactory<>("numeroTelephone"));
            tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            tableColumnNumeroCompteBancaire.setCellValueFactory(new PropertyValueFactory<>("numeroCompteBancaire"));

            setupPaiementColumn();
            setupRemoveColumn();

            tableViewFournisseur.getItems().addAll(fournisseurs);

        } else {
            System.out.println("No found.");
        }

    }



    ////////////////AJOUTER////////////////////
    @FXML
    private void handleSaveButtonAction() {
        // Create a new Fournisseur object and set its properties using the text fields
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNumSIREN(Integer.parseInt(txtnumSIREN.getText()));
        fournisseur.setNom(txtNom.getText());
        fournisseur.setAdresse(txtAdresse.getText());
        fournisseur.setNumeroTelephone(txtNumeroTelephone.getText());
        fournisseur.setEmail(txtEmail.getText());
        fournisseur.setNumeroCompteBancaire(txtNumeroCompteBancaire.getText());

        // Check if the fournisseur already exists
        if (fournisseurService.findBynumSIREN(fournisseur.getNumSIREN()) != null) {
            showAlert("Fournisseur déjà existant", "Un fournisseur avec le même numéro SIREN existe déjà.");
            return;
        }


        // Call the save method in FournisseurService to insert the fournisseur
        fournisseurService.save(fournisseur);

        // Clear the input fields after saving
        clearInputFields();

        // Refresh the table view to reflect the updated data
        refreshTableView();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void refreshTableView() {
        // Get the current data model of the table view
        ObservableList<Fournisseur> fournisseurs = tableViewFournisseur.getItems();

        // Clear the existing data
        fournisseurs.clear();

        // Fetch the updated data from the FournisseurService
        fournisseurs.addAll(fournisseurService.findAll());

        // Reapply the data model to the table view
        tableViewFournisseur.setItems(fournisseurs);
    }

    /////////////////MODIFIER//////////////////

    @FXML
    public void handleUpdateButtonAction(ActionEvent event) {
        Fournisseur selectedFournisseur = tableViewFournisseur.getSelectionModel().getSelectedItem();
        if (selectedFournisseur != null) {
            // Update the selected row's data
            selectedFournisseur.setNumSIREN(Integer.parseInt(txtnumSIREN.getText()));
            selectedFournisseur.setNom(txtNom.getText());
            selectedFournisseur.setAdresse(txtAdresse.getText());
            selectedFournisseur.setNumeroTelephone(txtNumeroTelephone.getText());
            selectedFournisseur.setEmail(txtEmail.getText());
            selectedFournisseur.setNumeroCompteBancaire(txtNumeroCompteBancaire.getText());

            // Call the update method in FournisseurService
            FournisseurService fournisseurService = new FournisseurService();
            fournisseurService.update(selectedFournisseur);

            // Refresh the table view to reflect the updated data
            tableViewFournisseur.refresh();

            clearInputFields();
        }
    }


    /////////////////REMOVE//////////////////



    ///////////////CLEARE///////////////////

    private void clearInputFields() {
        txtnumSIREN.clear();
        txtNom.clear();
        txtAdresse.clear();
        txtNumeroTelephone.clear();
        txtEmail.clear();
        txtNumeroCompteBancaire.clear();
    }

    ////////////////////////////////////


    private HelloApplication application;

    public void setApplication(HelloApplication application) {
        this.application = application;
    }


    /////////////////////////////////////////


    ///////////////////////////////////

    private void handleRowClick() {
        Fournisseur selectedRow = tableViewFournisseur.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            txtnumSIREN.setText(String.valueOf(selectedRow.getNumSIREN()));
            txtNom.setText(selectedRow.getNom());
            txtAdresse.setText(selectedRow.getAdresse());
            txtNumeroTelephone.setText(selectedRow.getNumeroTelephone());
            txtEmail.setText(selectedRow.getEmail());
            txtNumeroCompteBancaire.setText(selectedRow.getNumeroCompteBancaire());

            // Perform other actions or updates based on the selected row
        }
    }

    //////////////////////////////////////

    private void filterTable(String searchText) {
        List<Fournisseur> fournisseurs = fournisseurService.findAll();
        List<Fournisseur> searchResults = new ArrayList<>();

        // Iterate over the filteredList and add matching suppliers to the searchResults list
        for (Fournisseur supplier : fournisseurs) {
            String numSIREN = Integer.toString(supplier.getNumSIREN());
            if (numSIREN.contains(searchText)) {
                searchResults.add(supplier);
            }
        }

        // Clear the table view
        tableViewFournisseur.getItems().clear();

        // Add the search results to the table view
        tableViewFournisseur.getItems().addAll(searchResults);
    }

    ///////////////Cleare////////////////
    @FXML
    public void handleImageClick(MouseEvent mouseEvent) {
        clearInputFields();
    }

    ////////////////REMOVE///////////////

    private void setupButtonActions() {
    }

    private void setupRemoveColumn() {
        tableColumnREMOVE.setCellFactory(column -> {
            TableCell<Fournisseur, Button> remove = new TableCell<>() {
                final Button removeButton = new Button("REMOVE");

                {
                    removeButton.setOnAction(event -> {
                        Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                        fournisseurService.remove(fournisseur);
                        refreshTableView();
                        System.out.println("Removing fournisseur: " + fournisseur.getNumSIREN());
                    });
                }

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(removeButton);
                    }
                }
            };
            return remove;
        });
    }



    ////////////Paiement/////////////

    private void setupPaiementColumn() {
        tableColumnPaiement.setCellFactory(column -> {
            TableCell<Fournisseur, Button> paiement = new TableCell<>() {
                final Button paiementButton = new Button("Paiement");

                {
                    paiementButton.setOnAction(event -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/afm/suppliermanagementsystem/fxml/PaiementManager.fxml"));
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(paiementButton);
                    }
                }
            };
            return paiement;
        });
    }



    /*?*?*?*??/

     private void setupPaiementColumn() {
        tableColumnPaiement.setCellFactory(column -> {
            TableCell<Fournisseur, Button> paiement = new TableCell<>() {
                final Button paiementButton = new Button("Paiement");

                {
                    paiementButton.setOnAction(event -> {
                        Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                        openADDFournisseurWindow(fournisseur);
                    });
                }

                @Override
                protected void updateItem(Button item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(paiementButton);
                    }
                }
            };
            return paiement;
        });
    }

    private void openADDFournisseurWindow(Fournisseur fournisseur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ADDFournisseur.fxml"));
            Parent root = loader.load();

            // Access the controller of ADDFournisseur.fxml and set the fournisseur values
            Ins controller = loader.getController();
            controller.setFournisseur(fournisseur);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

}
