package magazynier;

public enum DocumentType {
    PRZYJECIE_WEWNETRZNE("Przyjęcie zewnętrzne"),
    WYDANIE_ZEWNETRZNE("Wydanie zewnętrzne");

    private String type;

    DocumentType(String docType) {
        type = docType;
    }

    public String getType() {
        return type;
    }
}
