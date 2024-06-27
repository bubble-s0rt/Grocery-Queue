import java.util.Random;
import java.util.concurrent.Semaphore;

public class GroceryQueues {
    private final GroceryQueue[] queues;
    private final Random random;
    private final Semaphore[] queueSemaphores;

    public GroceryQueues(int numberOfQueues, int maxLength) {
        queues = new GroceryQueue[numberOfQueues];
        queueSemaphores = new Semaphore[numberOfQueues];
        for (int i = 0; i < numberOfQueues; i++) {
            queues[i] = new GroceryQueue(maxLength);
            queueSemaphores[i] = new Semaphore(1);
        }
        this.random = new Random();
    }

    public boolean addCustomer(Customer customer) {
        GroceryQueue shortestQueue = queues[0];
        Semaphore shortestQueueSemaphore = queueSemaphores[0];
        
        for (int i = 1; i < queues.length; i++) {
            if (queues[i].getSize() < shortestQueue.getSize()) {
                shortestQueue = queues[i];
                shortestQueueSemaphore = queueSemaphores[i];
            }
        }
        
        try {
            shortestQueueSemaphore.acquire();
            return shortestQueue.addCustomer(customer);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            shortestQueueSemaphore.release();
        }
    }

    public GroceryQueue[] getQueues() {
        return queues;
    }
}
