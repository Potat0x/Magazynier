package magazynier;

import magazynier.contractor.Contractor;
import magazynier.utils.Indexed;
import magazynier.worker.Worker;

import java.sql.Date;
import java.util.Set;

public class Document implements Indexed {
    private Date date;
    private String name;
    private Contractor contractor;
    private Worker worker;
    private Integer id;
    private Set<DocumentItem> items;

    public Set<DocumentItem> getItems() {
        return items;
    }

    public void setItems(Set<DocumentItem> items) {
        this.items = items;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
