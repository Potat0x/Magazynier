package magazynier.entities;

import magazynier.utils.Indexed;

public class Item implements Indexed {

    private String ean;
    private String itemModelNumber;
    private String name;
    private String measurementUnit;
    private Double currentPrice;
    private Double desiredQuantity;
    private String description;
    private Integer id;
    private Integer vatRateId;

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getItemModelNumber() {
        return itemModelNumber;
    }

    public void setItemModelNumber(String itemModelNumber) {
        this.itemModelNumber = itemModelNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getDesiredQuantity() {
        return desiredQuantity;
    }

    public void setDesiredQuantity(Double desiredQuantity) {
        this.desiredQuantity = desiredQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVatRateId() {
        return vatRateId;
    }

    public void setVatRateId(Integer vatRateId) {
        this.vatRateId = vatRateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        if (ean != null ? !ean.equals(item.ean) : item.ean != null) return false;
        if (itemModelNumber != null ? !itemModelNumber.equals(item.itemModelNumber) : item.itemModelNumber != null)
            return false;
        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        if (measurementUnit != null ? !measurementUnit.equals(item.measurementUnit) : item.measurementUnit != null)
            return false;
        if (currentPrice != null ? !currentPrice.equals(item.currentPrice) : item.currentPrice != null) return false;
        if (desiredQuantity != null ? !desiredQuantity.equals(item.desiredQuantity) : item.desiredQuantity != null)
            return false;
        if (description != null ? !description.equals(item.description) : item.description != null) return false;
        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        return vatRateId != null ? vatRateId.equals(item.vatRateId) : item.vatRateId == null;
    }

    @Override
    public int hashCode() {
        int result = ean != null ? ean.hashCode() : 0;
        result = 31 * result + (itemModelNumber != null ? itemModelNumber.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (measurementUnit != null ? measurementUnit.hashCode() : 0);
        result = 31 * result + (currentPrice != null ? currentPrice.hashCode() : 0);
        result = 31 * result + (desiredQuantity != null ? desiredQuantity.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (vatRateId != null ? vatRateId.hashCode() : 0);
        return result;
    }
}
