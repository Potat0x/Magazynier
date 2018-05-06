package magazynier;

import magazynier.RowNotFoundException;
import magazynier.entities.Warehouse;

import java.util.ArrayList;

class WarehousesModel {

    ArrayList getWarehousesList() {
        return DAO.readTable("Warehouse");
    }

    void updateWarehouse(Warehouse warehouse) throws RowNotFoundException {
        DAO.update(warehouse);
    }

    public void addWarehouse(Warehouse newWarehouse) {
        DAO.add(newWarehouse);
    }

    void deleteWarehouse(Warehouse warehouse) throws RowNotFoundException {
        DAO.delete(warehouse);
    }
}
