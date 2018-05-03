package magazynier;

import javassist.NotFoundException;
import magazynier.entities.Warehouse;

import java.util.ArrayList;

class WarehousesModel {

    ArrayList getWarehousesList() {
        return DAO.readTable("Warehouse");
    }

    void updateWarehouse(Warehouse warehouse) throws NotFoundException {
        DAO.update(warehouse);
    }

    public void addWarehouse(Warehouse newWarehouse) {
        DAO.add(newWarehouse);
    }

    void deleteWarehouse(Warehouse warehouse) throws NotFoundException {
        DAO.delete(warehouse);
    }
}
