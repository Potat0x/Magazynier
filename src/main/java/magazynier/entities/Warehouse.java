package magazynier.entities;

public class Warehouse {
    private String name;
    private Integer id;

    public Warehouse(String name) {
        this.name = name;
    }

    public Warehouse() {
        this.name = "_warehouse_";
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
    public String toString() {
        return "[" + id + "] " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Warehouse)) return false;

        Warehouse warehouse = (Warehouse) o;

        if (name != null ? !name.equals(warehouse.name) : warehouse.name != null) return false;
        return id != null ? id.equals(warehouse.id) : warehouse.id == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
