package magazynier;

public class MeasurementUnit {

    private Integer id;
    private String unitName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public String toString() {
        return getUnitName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementUnit)) return false;

        MeasurementUnit that = (MeasurementUnit) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return unitName != null ? unitName.equals(that.unitName) : that.unitName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (unitName != null ? unitName.hashCode() : 0);
        return result;
    }
}
