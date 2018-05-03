package magazynier;

import javassist.NotFoundException;
import magazynier.entities.Warehouse;

import java.util.ArrayList;

class WarehousesModel {

    ArrayList getWarehousesList() {
        return DAO.readTable("Warehouse");
    }

    void updateWarehouse(Warehouse warehouse) throws NotFoundException {
        if (DAO.checkIfExistsById(Warehouse.class, warehouse.getId())) {
            DAO.update(warehouse);
        } else {
            throw new NotFoundException("Warehouse " + warehouse.getId() + "does not exist in database.");
        }
    }
}
