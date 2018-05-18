package magazynier;

import magazynier.item.Item;
import magazynier.item.VatRate;

import java.util.Optional;

public class DocumentItem {

    private Integer transactionSign;
    private Double quantity;
    private Double price;
    private VatRate vatRate;
    private Double tax;
    private MarginType marginType;
    private Double margin;
    private String batchNumber;
    private Integer id;
    private Item item;
    private Document document;

    public DocumentItem() {
    }

    public DocumentItem(Item item, Document document) {
        this.item = item;
        this.document = document;
        vatRate = item.getVatRate();
        price = item.getCurrentPrice();
        margin = 0.0;
        transactionSign = Optional.ofNullable(document.getDocumentType()).map(dt -> dt.getTag()).orElse(0);
    }

    public Integer getTransactionSign() {
        return transactionSign;
    }

    public void setTransactionSign(Integer transactionSign) {
        this.transactionSign = transactionSign;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public VatRate getVatRate() {
        return vatRate;
    }

    public void setVatRate(VatRate vatRate) {
        this.vatRate = vatRate;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public MarginType getMarginType() {
        return marginType;
    }

    public void setMarginType(MarginType marginType) {
        this.marginType = marginType;
    }

    public Double getMargin() {
        return margin;
    }

    public void setMargin(Double margin) {
        this.margin = margin;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentItem)) return false;

        DocumentItem item1 = (DocumentItem) o;

        if (transactionSign != null ? !transactionSign.equals(item1.transactionSign) : item1.transactionSign != null)
            return false;
        if (quantity != null ? !quantity.equals(item1.quantity) : item1.quantity != null) return false;
        if (price != null ? !price.equals(item1.price) : item1.price != null) return false;
        if (vatRate != null ? !vatRate.equals(item1.vatRate) : item1.vatRate != null) return false;
        if (tax != null ? !tax.equals(item1.tax) : item1.tax != null) return false;
        if (marginType != null ? !marginType.equals(item1.marginType) : item1.marginType != null) return false;
        if (margin != null ? !margin.equals(item1.margin) : item1.margin != null) return false;
        if (batchNumber != null ? !batchNumber.equals(item1.batchNumber) : item1.batchNumber != null) return false;
        if (id != null ? !id.equals(item1.id) : item1.id != null) return false;
        return item != null ? item.equals(item1.item) : item1.item == null;
    }

    @Override
    public int hashCode() {
        int result = transactionSign != null ? transactionSign.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (vatRate != null ? vatRate.hashCode() : 0);
        result = 31 * result + (tax != null ? tax.hashCode() : 0);
        result = 31 * result + (marginType != null ? marginType.hashCode() : 0);
        result = 31 * result + (margin != null ? margin.hashCode() : 0);
        result = 31 * result + (batchNumber != null ? batchNumber.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        /*  !!!     result = 31 * result + (document != null ? document.hashCode() : 0);    !!!  todo: remove doc mapping as obj   */
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
