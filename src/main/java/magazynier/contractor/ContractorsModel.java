package magazynier.contractor;

import magazynier.ContractorType;
import magazynier.DAO;
import magazynier.RowNotFoundException;

import java.util.ArrayList;

public class ContractorsModel {

    ArrayList<Contractor> getContractorsList() {
        return DAO.readTable("Contractor");
    }

    public ArrayList<ContractorType> getContractorTypesList() {
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

    public Double getTotalTransactionsValue(Contractor contractor) {
        return DAO.calculateTotalTransactionsValue(contractor.getId());
    }
}
