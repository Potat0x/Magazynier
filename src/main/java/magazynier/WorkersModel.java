package magazynier;

import javassist.NotFoundException;
import magazynier.entities.Worker;

import java.util.ArrayList;

public class WorkersModel {

    ArrayList getWorkersList() {
        return DAO.readTable("Worker");
    }

    public void updateWorker(Worker selectedWorker) throws NotFoundException {
        DAO.update(selectedWorker);
    }

    public void addWorker(Worker newWorker) {
        DAO.add(newWorker);
    }

    public void deleteWorker(Worker selectedWorker) throws NotFoundException {
        DAO.delete(selectedWorker);
    }
}

