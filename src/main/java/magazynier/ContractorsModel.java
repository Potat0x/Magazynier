package magazynier;

import magazynier.RowNotFoundException;
import magazynier.entities.Contractor;

import java.util.ArrayList;

public class ContractorsModel {

    ArrayList getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public void updateContractor(Contractor contractor) throws RowNotFoundException {
        DAO.update(contractor);
    }

    public void addContractor(Contractor contractor) {
        DAO.add(contractor);
    }

    public void deleteContractor(Contractor contractor) throws RowNotFoundException {
        DAO.delete(contractor);
    }
}
