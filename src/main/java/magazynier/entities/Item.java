package magazynier.entities;

import magazynier.utils.Indexed;

public class Item implements Indexed {

    private String ean;
    private String itemModelNumber;
    private String name;
    private String measurementUnit;
    private double currentPrice;
    private double desiredQuantity;
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

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getDesiredQuantity() {
        return desiredQuantity;
    }

    public void setDesiredQuantity(double desiredQuantity) {
        this.desiredQuantity = desiredQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
}
