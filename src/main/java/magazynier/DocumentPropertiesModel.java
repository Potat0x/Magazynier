package magazynier;

import magazynier.contractor.Contractor;
import magazynier.item.Item;
import magazynier.item.VatRate;
import magazynier.warehouse.Warehouse;
import magazynier.worker.Worker;
import org.hibernate.PropertyValueException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;

public class DocumentPropertiesModel {

    private static ArrayList<VatRate> VatRates = DAO.readTable("VatRate");

    public ArrayList<Item> getItemsList() {
        return DAO.readTable("Item");
    }

    public ArrayList<DocumentType> getDocTypesList() {
        return DAO.readTable("DocumentType");
    }

    public void updateDocument(Document document, Set<DocumentItem> deletedItems) throws RowNotFoundException {
        DAO.updateDocument(document, deletedItems);
        insertOrUpdateAssortment(document);
    }

    public ArrayList<Worker> getWorkersList() {
        return DAO.readTable("Worker");
    }

    public ArrayList<Contractor> getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public void addDocument(Document document) throws PropertyValueException {
        DAO.add(document);
        insertOrUpdateAssortment(document);
    }

    private void insertOrUpdateAssortment(Document document) {
        try {
            DAO.refresh(document);
        } catch (RowNotFoundException e) {
            e.printStackTrace();
        }
        document.getItems().forEach(di -> DAO.saveOrUpdate(new Assortment(di.getId(), di.getWarehouse().getId(), di.getBatchNumber())));
    }

    public ArrayList<MarginType> getMarginTypesList() {
        return DAO.readTable("MarginType");
    }

    public void refreshDocument(Document document) throws RowNotFoundException {
        DAO.refresh(document);
    }

    public ArrayList<VatRate> getVatRatesList() {
        return VatRates;
    }

    private Integer countDocumentsByDay(LocalDate day) {
        return DAO.countDocumentsByDay(day);
    }

    public String getNextDocName(LocalDate date) {
        return 1 + countDocumentsByDay(date) + date.format(DateTimeFormatter.ofPattern("/dd-MM-yyyy"));
    }

    public ArrayList<Warehouse> getWarehousesList() {
        return DAO.readTable("Warehouse");
    }

    public Integer findWarehouse(DocumentItem di) {
        return (Integer) DAO.findWarehouseIdByDocItemId(di.getId());
    }

    public void deleteItemFromAssortment(DocumentItem item) {
        DAO.delete(item.getId());
    }

    public Double getAvailableQuantityInWarehouse(DocumentItem docItem, Warehouse warehouse) {
        if (docItem == null)
            return -1.99;
        if (warehouse == null)
            return -2.99;
        else
            return DAO.getAvailableQuantityInWarehouse(docItem.getItem().getId(), warehouse.getId());
    }
}
