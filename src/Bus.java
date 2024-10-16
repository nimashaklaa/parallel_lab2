// No external imports needed

public class Bus implements Runnable {
    private BusStop busStop; // Shared bus stop object

    // Constructor to associate the bus with a bus stop
    public Bus(BusStop busStop) {
        this.busStop = busStop;
    }

    // Simulates the bus arriving at the bus stop
    @Override
    public void run() {
        try {
            System.out.println("Bus thread started.");
            busStop.busArrives(); // The bus arrives at the stop and waits for riders to board
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
        }
    }
}
