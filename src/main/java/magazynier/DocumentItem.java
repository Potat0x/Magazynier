package magazynier;

import magazynier.item.Item;
import magazynier.item.VatRate;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.function.Function;

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

        DocumentItem that = (DocumentItem) o;

        if (transactionSign != null ? !transactionSign.equals(that.transactionSign) : that.transactionSign != null)
            return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (vatRate != null ? !vatRate.equals(that.vatRate) : that.vatRate != null) return false;
        if (tax != null ? !tax.equals(that.tax) : that.tax != null) return false;
        if (marginType != null ? !marginType.equals(that.marginType) : that.marginType != null) return false;
        if (margin != null ? !margin.equals(that.margin) : that.margin != null) return false;
        if (batchNumber != null ? !batchNumber.equals(that.batchNumber) : that.batchNumber != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return item != null ? item.equals(that.item) : that.item == null;
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
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
