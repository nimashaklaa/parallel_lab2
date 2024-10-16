import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BusSimulation {

    private static final double MEAN_BUS_ARRIVAL = 12.0;  // mean arrival time in seconds
    private static final double MEAN_RIDER_ARRIVAL = 3.0;  // mean arrival time in seconds

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        BusStop busStop = new BusStop(); // Create a shared bus stop object

        // Start bus and rider threads
        // Schedule bus arrivals
        scheduleEvent(() -> new Thread(new Bus(busStop)).start(), MEAN_BUS_ARRIVAL);

        // Schedule rider arrivals
        scheduleEvent(() -> new Thread(new Rider(busStop)).start(), MEAN_RIDER_ARRIVAL);

        // Create and start the bus thread
//        Thread busThread = new Thread(new Bus(busStop));
//        busThread.start();
//
//        // Create and start multiple rider threads
//        for (int i = 0; i < 100; i++) {
//            Thread riderThread = new Thread(new Rider(busStop));
//            riderThread.start();
//        }
//
//        busThread.join(); // Wait for the bus thread to finish
    }
    private static void scheduleEvent(Runnable task, double meanTime) {
        long delay = (long) getExponentialTime(meanTime);
        scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.SECONDS);
    }

    private static double getExponentialTime(double mean) {
        double lambda = 1.0 / mean;
        return -Math.log(1 - Math.random()) / lambda;
    }
}
