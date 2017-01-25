
package su.vistar.multithreadingtest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ApplicationCore {
    public static void main(String[] args){
        Job job = new Job();
        System.out.println("Ядро приложения запущено");
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(job.mainTask, 0, 1, TimeUnit.MINUTES);
    }
}
