
package su.vistar.multithreadingtest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class ApplicationCore {
    public static void main(String[] args){
        Job job = new Job();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleWithFixedDelay(job.mainTask, 0, 1, TimeUnit.MINUTES);
        executor.scheduleWithFixedDelay(job.messagingTask, 0, 2, TimeUnit.MINUTES);
    }
}
