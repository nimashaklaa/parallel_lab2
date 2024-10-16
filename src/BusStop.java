import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class BusStop {
    private static final int BUS_CAPACITY = 50; // Maximum capacity of the bus
    private int waitingRiders = 0; // Riders currently waiting for the bus
    private int ridersBoarded = 0; // Riders who have boarded the bus
    private Semaphore ridersSemaphore = new Semaphore(0); // Controls when riders can board
    private Lock lock = new ReentrantLock(); // Lock for managing access to shared resources
    private Condition allRidersBoarded = lock.newCondition(); // Condition to signal when all riders have boarded

    // Method called when a rider arrives at the bus stop
    public void riderArrives() throws InterruptedException {
        lock.lock();
        try {
            waitingRiders++; // Increment the number of waiting riders
            System.out.println("Rider arrives" + waitingRiders);
            if (waitingRiders <= BUS_CAPACITY) {
                // If there's space on the bus, allow a rider to board
                ridersSemaphore.release();
            }
        } finally {
            lock.unlock();
        }

        // Rider waits for the signal to board the bus
        ridersSemaphore.acquire();
    }

    // Method called when a rider boards the bus
    public void boardBus() throws InterruptedException {
        lock.lock();
        try {
            ridersBoarded++; // Increment the count of riders who have boarded
            waitingRiders--; // Decrease the number of waiting riders
            System.out.println("Rider boards. Riders boarded: " + ridersBoarded);

            // If all waiting riders (up to the capacity) have boarded, signal the bus to depart
//            if (ridersBoarded == Math.min(BUS_CAPACITY, ridersBoarded + waitingRiders)) {
//                System.out.println("All riders have boarded. Signaling bus to depart.");
//                allRidersBoarded.signal(); // Signal that all riders have boarded
//            }
            if (ridersBoarded == BUS_CAPACITY || waitingRiders == 0) {
                System.out.println("All riders have boarded. Signaling bus to depart.");
                allRidersBoarded.signal(); // Signal that all riders have boarded
            }
        } finally {
            lock.unlock();
        }
    }

    // Method called when the bus arrives at the stop
    public void busArrives() throws InterruptedException {
        lock.lock();
        try {
            System.out.println("Bus arrives at the stop.");
            if (waitingRiders == 0) {
                // If no riders are waiting, the bus departs immediately
                System.out.println("No riders waiting.");
                depart();
                return;
            }

            ridersBoarded = 0; // Reset the count for this bus ride

            // Allow up to 50 riders to board or all waiting riders if fewer than 50
            int ridersToBoard = Math.min(BUS_CAPACITY, waitingRiders);

            for (int i = 0; i < ridersToBoard; i++) {
                ridersSemaphore.release(); // Allow riders to board up to the bus capacity
            }

            // Wait until all riders have boarded the bus
            allRidersBoarded.await();

//            for (int i = 0; i < ridersToBoard; i++) {
//                boardBus(); // Ensure that each rider boards
//            }

            // Depart after all riders have boarded
            depart();
        } finally {
            lock.unlock();
        }
    }

    // Method to simulate the bus departure
    public void depart() {
        System.out.println("Bus departs with " + ridersBoarded + " riders.");
    }
}
