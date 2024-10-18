import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class BusSimulation {

    private static final double MEAN_BUS_ARRIVAL = 12.0;  // mean arrival time in seconds
    private static final double MEAN_RIDER_ARRIVAL = 3.0;  // mean arrival time in seconds
    private static final int MAX_RIDERS = 100;
    private static final int BUS_CAPACITY = 50;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static int riderCount = 0;

    public static void main(String[] args) throws InterruptedException {
        BusStop busStop = new BusStop(); // Create a shared bus stop object

        // Schedule bus arrivals
        scheduleBusEvent(() -> new Thread(new Bus(busStop)).start());

        // Schedule rider arrivals
        scheduleRiderEvent(() -> {
            if (riderCount < MAX_RIDERS) {
                new Thread(new Rider(busStop)).start();
                riderCount++;
                System.out.println("Rider " + riderCount + " created.");
            } else {
                scheduler.shutdown();  // Stop creating riders after 100
                System.out.println("Reached max rider limit of " + MAX_RIDERS + ". No more riders.");
            }
        });
    }
    private static void scheduleBusEvent(Runnable task) {
        long delay = (long) getExponentialTime(BusSimulation.MEAN_BUS_ARRIVAL);
        scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.SECONDS);
    }

    private static void scheduleRiderEvent(Runnable task) {
        long delay = (long) getExponentialTime(BusSimulation.MEAN_RIDER_ARRIVAL);
        scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.SECONDS);
    }

    private static double getExponentialTime(double mean) {
//        double lambda = 1.0 / mean;
//        return -Math.log(1 - Math.random()) / lambda;
        Random rand = new Random();
        return (long) (-Math.log(1 - rand.nextDouble()) * mean);
    }
}

//TODO: here there is an issue of calling board bus before bus arrives need to fix it