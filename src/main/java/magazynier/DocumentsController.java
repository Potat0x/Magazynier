package magazynier;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

import static magazynier.DocumentPropertiesController.*;

@SuppressWarnings("unchecked")
public class DocumentsController {
    public TableView docTable;
    public TableColumn dateCol;
    public TableColumn nameCol;
    public TableColumn contractorCol;
    public TableColumn worker;

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
    }

    void refreshTable() {
        ArrayList wl = model.getDocumentsList();
        docTable.getItems().clear();
        docTable.getItems().addAll(wl);
    }

    private ActionResult showDocumentWindow(Document document, Mode mode) {
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

    public void editDocumentProperties() {
        Document slectedDoc = (Document) docTable.getSelectionModel().getSelectedItem();
        ActionResult actionResult = showDocumentWindow(slectedDoc, Mode.EDIT_ITEM);
        if (actionResult == ActionResult.FAIL) {
            refreshTable();
        } else {
            docTable.refresh();
        }
    }

    public void addDocument(ActionEvent actionEvent) {
        Document newDoc = new Document();
        ActionResult actionResult = showDocumentWindow(newDoc, Mode.ADD_ITEM);
        refreshTable();
    }
}
