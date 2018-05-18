package magazynier;

import magazynier.contractor.Contractor;
import magazynier.utils.Indexed;
import magazynier.utils.NullableCalc;
import magazynier.worker.Worker;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class Document implements Indexed {
    private Date date;
    private String name;
    private DocumentType documentType;
    private Contractor contractor;
    private Worker worker;
    private Integer id;
    private Set<DocumentItem> items;

    public Document() {
        this.items = new HashSet<>();
    }

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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
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

    Double netVal() {
        return items.stream().mapToDouble(i -> NullableCalc.multiplyNullable(i.getQuantity(), NullableCalc.netValue(i.getPrice(), i.getTax()))).sum();
    }

    Double grossVal() {
        return items.stream().mapToDouble(i -> NullableCalc.multiplyNullable(i.getQuantity(), i.getPrice())).sum();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        if (date != null ? !date.equals(document.date) : document.date != null) return false;
        if (name != null ? !name.equals(document.name) : document.name != null) return false;
        if (documentType != null ? !documentType.equals(document.documentType) : document.documentType != null)
            return false;
        if (contractor != null ? !contractor.equals(document.contractor) : document.contractor != null) return false;
        if (worker != null ? !worker.equals(document.worker) : document.worker != null) return false;
        if (id != null ? !id.equals(document.id) : document.id != null) return false;
        return items != null ? items.equals(document.items) : document.items == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (documentType != null ? documentType.hashCode() : 0);
        result = 31 * result + (contractor != null ? contractor.hashCode() : 0);
        result = 31 * result + (worker != null ? worker.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
