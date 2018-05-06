package magazynier.entities;

public class VatRate {

    private Double tax;
    private String name;
    private Integer id;

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VatRate)) return false;

        VatRate vatRate = (VatRate) o;

        if (tax != null ? !tax.equals(vatRate.tax) : vatRate.tax != null) return false;
        if (name != null ? !name.equals(vatRate.name) : vatRate.name != null) return false;
        return id != null ? id.equals(vatRate.id) : vatRate.id == null;
    }

    @Override
    public int hashCode() {
        int result = tax != null ? tax.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + tax + "%)";
    }
}
