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
    private Semaphore busSemaphore = new Semaphore(0);  // Bus will signal riders to board
    private Semaphore mutex = new Semaphore(1);  // Protect shared resources (waitingRiders count)
    private Semaphore allAboard = new Semaphore(0);  // Bus waits for riders to finish boarding
    private int ridersToBoard = 0;  // Riders boarding the current bus
    private boolean isBoarding = false; // Flag to indicate if the bus is currently boarding

    // Called when a rider arrives at the bus stop
    public void riderArrives() throws InterruptedException {
        mutex.acquire();  // Enter critical section to update rider count
        waitingRiders++;
        System.out.println("Rider arrived. Waiting riders: " + waitingRiders);
        mutex.release();  // Exit critical section

        busSemaphore.acquire();  // Wait for the bus to arrive and signal boarding
        if (isBoarding) {
            boardBus();  // Board the bus when allowed
        } else {
            // If bus is not boarding, decrement waitingRiders
//            mutex.acquire();
//            waitingRiders--; // Remove the rider from waiting count if they miss this bus
            System.out.println("Rider missed the bus and will wait for the next one.");
//            mutex.release();
        }
    }

    // Called when a rider is allowed to board the bus
    private void boardBus() throws InterruptedException {
        System.out.println("Rider boarding the bus.");
        mutex.acquire();
//        ridersToBoard--;  // Decrease the count of riders boarding
        ridersBoarded++;
        waitingRiders--;  // Update waitingRiders as soon as the rider boards
        System.out.println("Rider boarding the bus.");
        System.out.println("Remaining riders to board: " + (BUS_CAPACITY - ridersBoarded) + ", waitingRiders: " + waitingRiders);
//        if (ridersBoarded == 0) {
//            allAboard.release();  // Signal the bus that all riders have boarded
//        }
        if (ridersBoarded == BUS_CAPACITY) {
            allAboard.release();  // Signal the bus that all riders have boarded
        }
        mutex.release();
    }

    // Called when a bus arrives at the bus stop
    public void busArrives() throws InterruptedException {
        mutex.acquire();  // Enter critical section to check the number of waiting riders
        if (waitingRiders == 0) {
            System.out.println("Bus arrived. No riders waiting, departing immediately.");
            mutex.release();  // Exit critical section
            return;
        }

        // Allow riders to board, but not more than the bus capacity
        ridersToBoard = Math.min(waitingRiders, BUS_CAPACITY);
//        ridersBoarded = ridersToBoard;  // Store how many riders are actually boarding
        System.out.println("Bus arrived. Boarding " + ridersToBoard+ " riders.");

        isBoarding = true;
        // Release permits for riders to board
        for (int i = 0; i < ridersBoarded; i++) {
            busSemaphore.release();  // Allow each rider to board
        }
        mutex.release();  // Exit critical section

        allAboard.acquire();  // Wait until all riders finish boarding

        depart();

        // Reset boarding state
        isBoarding = false;

        mutex.acquire();
        waitingRiders -= ridersBoarded;  // Update the remaining riders at the bus stop
        ridersToBoard = 0;  // Reset riders to board for the next bus
        ridersBoarded = 0;  // Reset for the next bus
        mutex.release();
    }


    // Method to simulate the bus departure
    public void depart() {
        System.out.println("Bus departs with " + ridersBoarded + " riders.");
    }
}
