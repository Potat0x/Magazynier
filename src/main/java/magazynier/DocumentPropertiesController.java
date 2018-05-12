package magazynier;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import magazynier.contractor.Contractor;
import magazynier.worker.Worker;

import java.sql.Date;

public class DocumentPropertiesController {


    public enum Mode {
        ADD_ITEM,
        EDIT_ITEM
    }

    public enum ActionResult {
        CONFIRM,
        CANCEL,
        FAIL
    }

    @FXML
    public DatePicker date;
    @FXML
    public TextField name;
    @FXML
    public ComboBox contractor;
    @FXML
    public TextField worker;

    private Document document;
    private Mode mode;

    public DocumentPropertiesController(Document slectedDoc, Mode mode) {
        System.out.println("DocumentPropertiesController");
        this.document = slectedDoc;
        this.mode = mode;
    }

    @FXML
    public void initialize() {
        if (mode == Mode.EDIT_ITEM) {

            Date date = document.getDate();
            this.date.setValue((date != null) ? date.toLocalDate() : null);

            this.name.setText(document.getName());

            Contractor contractor = document.getContractor();
            this.contractor.setValue(((contractor != null) ? contractor.getContractorName() : null));

            Worker worker = document.getWorker();
            this.worker.setText((worker != null) ? worker.getFullName() : null);
        }
    }
}
