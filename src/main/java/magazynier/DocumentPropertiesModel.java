package magazynier;

import java.util.ArrayList;

public class DocumentPropertiesModel {
    public ArrayList getItemsList() {
        return DAO.readTable("Item");
    }

    public ArrayList getWorkersList() {
        return DAO.readTable("Worker");
    }

    public ArrayList getContractorsList() {
        return DAO.readTable("Contractor");
    }
}
