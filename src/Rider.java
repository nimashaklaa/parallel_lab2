

public class Rider implements Runnable {
    private BusStop busStop; // Shared bus stop object

    // Constructor to associate the rider with a bus stop
    public Rider(BusStop busStop) {
        this.busStop = busStop;
    }

    // Simulates the rider arriving at the bus stop and boarding the bus
    @Override
    public void run() {
        try {
            System.out.println("Rider thread started.");
            busStop.riderArrives(); // The rider arrives at the bus stop
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
        }
    }
}
