package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import magazynier.utils.AlertLauncher;
import magazynier.utils.MoneyValueFormat;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;

import static magazynier.ActionMode.EDIT;
import static magazynier.ActionMode.PREVIEW;
import static magazynier.utils.WindowCreator.createWindowFromFxml;

public class DocumentsController {
    public TableView<Document> docTable;
    public TableColumn<Document, String> dateCol;
    public TableColumn<Document, String> nameCol;
    public TableColumn<Document, String> contractorCol;
    public TableColumn<Document, String> worker;
    public Button showDocumentButton;
    public Button editDocumentButton;
    public Button deleteDocumentButton;
    public TableColumn<Document, String> netValCol;
    public TableColumn<Document, String> grossValCol;
    public TableColumn<Document, String> profitCol;

    private DocumentModel model;

    public DocumentsController() {
        model = new DocumentModel();
    }

    @FXML
    public void initialize() {
        MoneyValueFormat format = new MoneyValueFormat();
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        worker.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getWorker().getFullName()));
        contractorCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getContractor().getFullName()));
        netValCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(format.format(p.getValue().netVal())));
        grossValCol.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(format.format(p.getValue().grossVal())));
        refreshTable();

        docTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showDocumentButton.setDisable(newValue == null);
            editDocumentButton.setDisable(newValue == null);
            deleteDocumentButton.setDisable(newValue == null);
        });
    }

    private void refreshTable() {
        ArrayList<Document> wl = model.getDocumentsList();
        docTable.getItems().clear();
        docTable.getItems().addAll(wl);
    }

    private ActionResult showDocumentWindow(Document document, ActionMode mode) {

        DocumentPropertiesController dcp = new DocumentPropertiesController(document, mode);
        try {
            Stage itemStage = createWindowFromFxml("/fxml/document_properties.fxml", dcp, "Dokument");
            itemStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dcp.getActionResult();
    }

    private void showDocumentWindow(ActionMode mode) {
        Document slectedDoc = docTable.getSelectionModel().getSelectedItem();

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
        showDocumentWindow(newDoc, ActionMode.ADD);
        refreshTable();
    }

    public void deleteDocument() {
        try {
            model.deleteDocument(docTable.getSelectionModel().getSelectedItem());
            refreshTable();
        } catch (RowNotFoundException e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "Nie znaleziono dokumentu. Mógł zostać wcześniej usunięty przez innego użytkownika.");
            refreshTable();
        } catch (PersistenceException e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "Z tym dokumentem jest powiązany asortyment.");
        } catch (Exception e) {
            AlertLauncher.showAndWait(Alert.AlertType.ERROR, "Błąd", "Nie można usunąć dokumentu.",
                    "Nieznany błąd.");
            e.printStackTrace();
            refreshTable();
        }
    }
}
