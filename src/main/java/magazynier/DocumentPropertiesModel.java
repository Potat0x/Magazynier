package magazynier;

import org.hibernate.PropertyValueException;

import java.util.ArrayList;

public class DocumentPropertiesModel {
    public ArrayList getItemsList() {
        return DAO.readTable("Item");
    }

    public ArrayList getDocTypesList() {
        return DAO.readTable("DocumentType");
    }

    public void updateDocument(Document document) throws RowNotFoundException {
        DAO.update(document);
    }

    public ArrayList getWorkersList() {
        return DAO.readTable("Worker");
    }

    public ArrayList getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public void addDocument(Document document) throws PropertyValueException {
        DAO.add(document);
    }

    public ArrayList getMarginTypesList() {
        return DAO.readTable("MarginType");
    }

    public void refreshDocument(Document document) throws RowNotFoundException {
        DAO.refresh(document);
    }
}
