package magazynier.worker;

import magazynier.DAO;
import magazynier.RowNotFoundException;

import java.util.ArrayList;

public class WorkersModel {

    ArrayList <Worker>getWorkersList() {
        return DAO.readTable("Worker");
    }

    public void updateWorker(Worker selectedWorker) throws RowNotFoundException {
        DAO.update(selectedWorker);
    }

    public void addWorker(Worker newWorker) {
        DAO.add(newWorker);
    }

    public void deleteWorker(Worker selectedWorker) throws RowNotFoundException {
        DAO.delete(selectedWorker);
    }
}

