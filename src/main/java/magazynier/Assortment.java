package magazynier;


import java.io.Serializable;

public class Assortment implements Serializable {

    private Integer documentItemId;
    private Integer warehouseId;
    private String batchNumber;

    public Assortment() {
    }

    public Assortment(Integer documentItemId, Integer warehouseId, String batchNumber) {
        this.documentItemId = documentItemId;
        this.warehouseId = warehouseId;
        this.batchNumber = batchNumber;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getDocumentItemId() {
        return documentItemId;
    }

    public void setDocumentItemId(Integer documentItemId) {
        this.documentItemId = documentItemId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public String toString() {
        return "Assortment{" +
                "warehouseId=" + warehouseId +
                ", documentItemId=" + documentItemId +
                ", batchNumber='" + batchNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assortment)) return false;

        Assortment that = (Assortment) o;

        if (documentItemId != null ? !documentItemId.equals(that.documentItemId) : that.documentItemId != null)
            return false;
        if (warehouseId != null ? !warehouseId.equals(that.warehouseId) : that.warehouseId != null) return false;
        return batchNumber != null ? batchNumber.equals(that.batchNumber) : that.batchNumber == null;
    }

    @Override
    public int hashCode() {
        int result = documentItemId != null ? documentItemId.hashCode() : 0;
        result = 31 * result + (warehouseId != null ? warehouseId.hashCode() : 0);
        result = 31 * result + (batchNumber != null ? batchNumber.hashCode() : 0);
        return result;
    }
}
