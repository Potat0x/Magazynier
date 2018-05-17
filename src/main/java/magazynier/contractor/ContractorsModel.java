package magazynier.contractor;

import magazynier.DAO;
import magazynier.RowNotFoundException;

import java.util.ArrayList;

public class ContractorsModel {

    ArrayList getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public ArrayList getContractorTypesList() {
        return DAO.readTable("ContractorType");
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
