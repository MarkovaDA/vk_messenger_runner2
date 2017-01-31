package su.vistar.multithreadingtest;

import su.vistar.multithreadingtest.service.CriteriaService;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import su.vistar.multithreadingtest.dto.CriteriaDTO;
import su.vistar.multithreadingtest.dto.MessageDTO;
import su.vistar.multithreadingtest.dto.UsersSearchResponse;
import su.vistar.multithreadingtest.service.VKApiService;

public class Job {

    private Connection dbConnection;
    private VKApiService vkApiService;
    private List<String> keys; //ключи доступа
    private static final int DAILY_PORTION_SIZE = 20; //сообщений в день от одного профиля
    public Job() {
        try {
            this.dbConnection = JDBCFactory.getConnection();
            this.vkApiService = new VKApiService();
            getKeys();
        } catch (PropertyVetoException | SQLException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Runnable mainTask = new Runnable() {
        @Override
        public void run() {
            CriteriaService service = new CriteriaService();
            System.out.println("Task управления критериями запущен");
            //достаем из базы все необработынные критерии
            List<Integer> list = service.notViewedCriteria(dbConnection);
            list.forEach(id -> {
                //каждый критерий будет обрабатываться в отдельном потоке
                //(передаем id критерия)
                Thread criteriaTask = new Thread(new CriteriaTask(id));
                criteriaTask.start();
            });
        }
    };
    public Runnable messagingTask = new Runnable() {
        @Override
        public void run() {
            System.out.println("Task отправки сообщений запущен");
            CriteriaService criteriaService = new CriteriaService();
            int start = -1;
            MessageDTO adresat;
            //и здесь всего 20 в сутки; 
            //изменить размер, чередовать ключ
            while (true) {
                synchronized (dbConnection) {
                    adresat = criteriaService.getNextAdresat(++start, dbConnection);
                    System.out.println(adresat.getUid());
                }
                if (adresat.getUid() == null || start >= keys.size() * DAILY_PORTION_SIZE)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
                try {
                    /*if (start % DAILY_PORTION_SIZE == 0)
                        keys.get(start / DAILY_PORTION_SIZE);*/
                    vkApiService.sendMessage(adresat.getUid(), adresat.getText());
                    Thread.sleep(300);   
                } 
                catch (InterruptedException | IOException ex) {
                    Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
    
    private void getKeys(){
        synchronized (dbConnection){
            keys = new CriteriaService().getActiveKeys(dbConnection);
        }
    }
    //задача, в рамках которой выполняется разбор отдельного критерия
    class CriteriaTask implements Runnable {

        private int id;

        public CriteriaTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println("Task для критерия № " + id + " запущен");
            CriteriaService criteriaService = new CriteriaService();
            CriteriaDTO criteria;
            synchronized (dbConnection) {
                criteria = criteriaService.getCriteria(id, dbConnection);
                criteriaService.updateCriteriaStatus(id, dbConnection);
            }
            try {
                UsersSearchResponse answer;
                int offset = criteria.getOffset();
                do {
                    synchronized (vkApiService) {
                        answer = vkApiService.getPeople(criteria.getCondition(), offset);
                    }
                    if (answer == null || answer.getResponse().getItems().isEmpty()) {
                        Thread.currentThread().interrupt();
                        break;//никогда сюда не зайдет 
                    }
                    offset += answer.getResponse().getItems().size();
                    synchronized (dbConnection) {
                        criteriaService.insert(id, answer.getResponse().getItems(), dbConnection);
                    }
                    Thread.sleep(340);
                } while (true);
                synchronized (dbConnection) {
                    criteriaService.updateCriteriaOffset(id, offset, dbConnection);
                }
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

 

