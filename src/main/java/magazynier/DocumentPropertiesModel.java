package magazynier;

import magazynier.contractor.Contractor;
import magazynier.item.Item;
import magazynier.worker.Worker;
import org.hibernate.PropertyValueException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DocumentPropertiesModel {

    private static ArrayList VatRates = DAO.readTable("VatRate");

    public ArrayList<Item> getItemsList() {
        return DAO.readTable("Item");
    }

    public ArrayList<DocumentType> getDocTypesList() {
        return DAO.readTable("DocumentType");
    }

    public void updateDocument(Document document) throws RowNotFoundException {
        DAO.update(document);
    }

    public ArrayList<Worker> getWorkersList() {
        return DAO.readTable("Worker");
    }

    public ArrayList<Contractor> getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public void addDocument(Document document) throws PropertyValueException {
        DAO.add(document);
    }

    public ArrayList<MarginType> getMarginTypesList() {
        return DAO.readTable("MarginType");
    }

    public void refreshDocument(Document document) throws RowNotFoundException {
        DAO.refresh(document);
    }

    public ArrayList getVatRatesList() {
        return VatRates;
    }

    public Integer countDocumentsByDay(LocalDate day) {
        return DAO.countDocumentsByDay(day);
    }

    public String getNextDocName(LocalDate date) {
        return 1 + countDocumentsByDay(date) + date.format(DateTimeFormatter.ofPattern("/dd-MM-yyyy"));
    }
}
