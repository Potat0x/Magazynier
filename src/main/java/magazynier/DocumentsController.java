package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import magazynier.utils.AlertLauncher;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;

import static magazynier.ActionMode.EDIT;
import static magazynier.ActionMode.PREVIEW;

@SuppressWarnings("unchecked")
public class DocumentsController {
    public TableView docTable;
    public TableColumn dateCol;
    public TableColumn nameCol;
    public TableColumn contractorCol;
    public TableColumn worker;
    public Button showDocumentButton;
    public Button editDocumentButton;
    public Button deleteDocumentButton;

    private DocumentModel model;

    public DocumentsController() {
        model = new DocumentModel();
    }

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<String, Document>("date"));
        nameCol.setCellValueFactory(new PropertyValueFactory<String, Document>("name"));
        worker.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Document, String>, ObservableValue<String>>) p -> new ReadOnlyObjectWrapper(p.getValue().getWorker().getFullName()));
        contractorCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Document, String>, ObservableValue<String>>) p -> new ReadOnlyObjectWrapper(p.getValue().getContractor().getFullName()));
        refreshTable();

        docTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                showDocumentButton.setDisable(newValue == null);
                editDocumentButton.setDisable(newValue == null);
                deleteDocumentButton.setDisable(newValue == null);
            }
        });
    }

    void refreshTable() {
        ArrayList wl = model.getDocumentsList();
        docTable.getItems().clear();
        docTable.getItems().addAll(wl);
    }

    private ActionResult showDocumentWindow(Document document, ActionMode mode) {
        FXMLLoader itemStageLoader = new FXMLLoader(getClass().getResource("/fxml/document_properties.fxml"));

        DocumentPropertiesController dcp = new DocumentPropertiesController(document, mode);
        itemStageLoader.setController(dcp);
        Stage itemStage = new Stage();
        itemStage.setTitle("Dokument");
        Parent parent = null;
        try {
            parent = itemStageLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemStage.setScene(new Scene(parent));
        itemStage.showAndWait();

        return dcp.getActionResult();
    }

    //todo: remove duplicated code
    private void showDocumentWindow(ActionMode mode) {
        Document slectedDoc = (Document) docTable.getSelectionModel().getSelectedItem();

        try {
            model.refreshDocument(slectedDoc);
            ActionResult actionResult = showDocumentWindow(slectedDoc, mode);
            if (actionResult == ActionResult.FAIL) {
                refreshTable();
            } else {
                docTable.refresh();
            }
        } catch (RowNotFoundException e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie znaleziono dokumentu.", "Nie znalaziono dokumentu. Mógł zostać usunięty z bazy.");
            refreshTable();
        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można znaleźć dokumentu.", "Nieznany błąd.");
            refreshTable();
        }
    }


    @FXML
    public void editDocumentProperties() {
        showDocumentWindow(EDIT);
    }

    @FXML
    public void showDocumentProperties() {
        showDocumentWindow(PREVIEW);
    }

    public void addDocument() {
        Document newDoc = new Document();
        ActionResult actionResult = showDocumentWindow(newDoc, ActionMode.ADD);
        refreshTable();
    }

    public void deleteDocument() {
        try {
            model.deleteDocument((Document) docTable.getSelectionModel().getSelectedItem());
            refreshTable();
        } catch (RowNotFoundException e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "Nie znaleziono dokumentu. Mógł zostać wcześniej usunięty przez innego użytkownika.");
            refreshTable();
        } catch (PersistenceException e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "todo: constr");//todo
        } catch (Exception e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "Nieznany błąd.");
            e.printStackTrace();
            refreshTable();
        }
    }
}
