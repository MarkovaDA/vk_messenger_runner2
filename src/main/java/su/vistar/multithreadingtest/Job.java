package su.vistar.multithreadingtest;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Job {

    private Connection dbConnection;

    public Job() {
        try {
            this.dbConnection = JDBCFactory.getConnection();
        } catch (PropertyVetoException | SQLException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Runnable mainTask = new Runnable() {
        @Override
        public void run() {
            CriteriaService service = new CriteriaService();
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
            synchronized(dbConnection){
                //обновляем статус критерия на "просмотренный"
                criteriaService.updateCriteriaStatus(id, dbConnection);
            }
        }
    }
}
