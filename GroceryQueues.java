import java.util.Random;
import java.util.concurrent.Semaphore;

public class GroceryQueues {
    private final GroceryQueue[] queues;
    private final Random random;
    private final Semaphore queuesSemaphore = new Semaphore(1);

    public GroceryQueues(int numberOfQueues, int maxLength) {
        queues = new GroceryQueue[numberOfQueues];
        for (int i = 0; i < numberOfQueues; i++) {
            queues[i] = new GroceryQueue(maxLength);
        }
        this.random = new Random();
    }

    public boolean addCustomer(Customer customer) {
        try {
            queuesSemaphore.acquire();
            GroceryQueue shortestQueue = queues[0];
            for (GroceryQueue queue : queues) {
                if (queue.getSize() < shortestQueue.getSize()) {
                    shortestQueue = queue;
                }
            }
            return shortestQueue.addCustomer(customer);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            queuesSemaphore.release();
        }
    }

    public GroceryQueue[] getQueues() {
        return queues;
    }
}
