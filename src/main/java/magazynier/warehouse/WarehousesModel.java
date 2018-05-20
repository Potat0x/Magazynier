package magazynier.warehouse;

import magazynier.DAO;
import magazynier.RowNotFoundException;

import java.util.ArrayList;

class WarehousesModel {

    ArrayList <Warehouse>getWarehousesList() {
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

    public Double getWarehouseGrossValue(Warehouse value) {
        return DAO.getWarehouseGrossValue(value.getId());
    }

    public Double getWarehouseNetValue(Warehouse value) {
        return DAO.getWarehouseNetValue(value.getId());
    }
}
