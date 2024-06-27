import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

public class QueueSimulator {
    private final int simulationTime;
    private final GroceryQueues groceryQueues;
    private int totalCustomersGroceryQueues;
    private int customersServedGroceryQueues;
    private int customersLeftGroceryQueues;
    private int totalServiceTimeGroceryQueues;

    private final Random random;
    private final int scaleFactor = 10;

    private final Semaphore customerCountSemaphore = new Semaphore(1);
    private final Semaphore serviceTimeSemaphore = new Semaphore(1);

    public QueueSimulator(int simulationTime, int numberOfCashiers, int groceryQueueMaxLength) {
        this.simulationTime = simulationTime;
        this.groceryQueues = new GroceryQueues(numberOfCashiers, groceryQueueMaxLength);
        this.totalCustomersGroceryQueues = 0;
        this.customersServedGroceryQueues = 0;
        this.customersLeftGroceryQueues = 0;
        this.totalServiceTimeGroceryQueues = 0;
        this.random = new Random();
    }

    public void startSimulation() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(groceryQueues.getQueues().length + 2);

        // Customer arrival generator
        executor.scheduleAtFixedRate(() -> {
            int arrivalTime = (int) (System.currentTimeMillis() / 1000);
            int serviceTime = random.nextInt(241) + 60; // 60 to 300 simulated seconds
            Customer customer = new Customer(arrivalTime, serviceTime);
            try {
                customerCountSemaphore.acquire();
                totalCustomersGroceryQueues++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                customerCountSemaphore.release();
            }
            if (!groceryQueues.addCustomer(customer)) {
                // Simulate waiting for up to 10 simulated minutes (10 * scaleFactor seconds)
                try {
                    TimeUnit.MILLISECONDS.sleep(600 * scaleFactor);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!groceryQueues.addCustomer(customer)) {
                    try {
                        customerCountSemaphore.acquire();
                        customersLeftGroceryQueues++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        customerCountSemaphore.release();
                    }
                }
            }
        }, 0, (random.nextInt(41) + 20) * scaleFactor, TimeUnit.MILLISECONDS); // Scaled to 20 to 60 milliseconds

        // Cashiers serving customers
        for (GroceryQueue queue : groceryQueues.getQueues()) {
            executor.scheduleAtFixedRate(() -> {
                Customer customer = queue.getNextCustomer();
                if (customer != null) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(customer.getServiceTime() * scaleFactor); // Scale service time
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        serviceTimeSemaphore.acquire();
                        totalServiceTimeGroceryQueues += customer.getServiceTime();
                        customersServedGroceryQueues++;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        serviceTimeSemaphore.release();
                    }
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        // Stop the simulation after the specified scaled time
        executor.schedule(() -> {
            executor.shutdown();
            printResults();
        }, simulationTime * 60 * scaleFactor, TimeUnit.MILLISECONDS); // Convert minutes to scaled milliseconds
    }

    private void printResults() {
        System.out.println("GroceryQueues Simulation Results:");
        System.out.println("Total customers arrived: " + totalCustomersGroceryQueues);
        System.out.println("Total customers served: " + customersServedGroceryQueues);
        System.out.println("Total customers left without being served: " + customersLeftGroceryQueues);
        System.out.println("Average service time: " + (customersServedGroceryQueues > 0 ? (totalServiceTimeGroceryQueues / customersServedGroceryQueues) / 60 : 0) + " minutes");
        System.out.println("Total simulation time: " + simulationTime + " minutes");

        // Validate results
        int calculatedLeftCustomers = totalCustomersGroceryQueues - customersServedGroceryQueues;
        System.out.println("Calculated customers left without being served : " + calculatedLeftCustomers);
    }

    public static void main(String[] args) {
        int simulationTime = 120; // in minutes
        int numberOfCashiers = 3;
        int groceryQueueMaxLength = 2;
        QueueSimulator simulator = new QueueSimulator(simulationTime, numberOfCashiers, groceryQueueMaxLength);
        simulator.startSimulation();
    }
}
