package magazynier;


import java.util.ArrayList;

public class DocumentModel {
    ArrayList getDocumentsList() {
        return DAO.readTable("Document");
    }

    public void updateDocument(Document selectedDocument) throws RowNotFoundException {
        DAO.update(selectedDocument);
    }

    public void addDocument(Document newDocument) {
        DAO.add(newDocument);
    }

    public void deleteDocument(Document selectedDocument) throws RowNotFoundException {
        DAO.delete(selectedDocument);
    }
}
