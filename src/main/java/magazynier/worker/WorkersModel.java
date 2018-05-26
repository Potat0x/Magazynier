package magazynier.worker;

import magazynier.DAO;
import magazynier.MessageNotification;
import magazynier.RowNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

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

    //chat = = = = =
    public ArrayList<MessageNotification> getNotificationsList(Worker thisWorker) {
        if (thisWorker != null) {
            return DAO.getNotificationsList(thisWorker);
        } else return new ArrayList<>();
    }

    public Optional<Worker> getNotificationSource(MessageNotification msgNotification) {
        ArrayList<Worker> workers = getWorkersList();
        return workers.stream().filter(worker -> msgNotification.getSender().getId().equals(worker.getId())).findFirst();
    }

}

