import java.util.concurrent.Semaphore;

public class BusStop {
    private static final int BUS_CAPACITY = 50; // Maximum capacity of the bus
    private int waitingRiders = 0; // Riders currently waiting for the bus
    private int ridersBoarded = 0; // Riders who have boarded the bus
    private Semaphore ridersSemaphore = new Semaphore(0); // Controls when riders can board
    private Semaphore busSemaphore = new Semaphore(0);  // Bus will signal riders to board
    private Semaphore mutex = new Semaphore(1);  // Protect shared resources (waitingRiders count)


    // Called when a rider arrives at the bus stop
    public void riderArrives() throws InterruptedException {
        mutex.acquire();  // Enter critical section to update rider count
        waitingRiders++;
        System.out.println("Rider arrived. Waiting riders: " + waitingRiders);
        mutex.release();  // Exit critical section

        busSemaphore.acquire();  // Wait for the bus to arrive and signal boarding
        boardBus();
    }

    // Called when a rider is allowed to board the bus
    private void boardBus() throws InterruptedException {
        System.out.println("Rider boarding the bus.");
        mutex.acquire();
        if (waitingRiders > 0) {
            waitingRiders--;
            System.out.println("Rider boards the bus. Remaining waiting riders: " + waitingRiders);
            ridersSemaphore.release();  // Signal the bus that a rider has boarded
        }
        mutex.release();
    }

    // Called when a bus arrives at the bus stop
    public void busArrives() throws InterruptedException {
        mutex.acquire();
        int ridersToBoard = Math.min(waitingRiders, BUS_CAPACITY);
        System.out.println("Bus arrives and can board up to " + ridersToBoard + " riders.");
        busSemaphore.release(ridersToBoard);  // Allow up to BUS_CAPACITY or waitingRiders to board
        mutex.release();

        if (ridersToBoard > 0) {
            // Wait for all boarding riders to signal that they have boarded
            ridersSemaphore.acquire(ridersToBoard);
        }
        depart(ridersToBoard);  // The bus departs after boarding
    }


    // Method to simulate the bus departure
    public void depart(int ridersBoarded) {
        System.out.println("Bus departs with " + ridersBoarded + " riders. Riders remaining: " + waitingRiders);
    }
}
