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

    public void showDocumentProperties(ActionEvent actionEvent) {
        FXMLLoader itemStageLoader = new FXMLLoader(getClass().getResource("/fxml/document_properties.fxml"));

        Document slectedDoc = (Document) docTable.getSelectionModel().getSelectedItem();
        DocumentPropertiesController ic = new DocumentPropertiesController(slectedDoc, DocumentPropertiesController.Mode.EDIT_ITEM);
        itemStageLoader.setController(ic);
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
    }
}
