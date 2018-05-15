package magazynier;

import java.util.ArrayList;

public class DocumentPropertiesModel {
    public ArrayList getItemsList() {
        return DAO.readTable("Item");
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
}
