import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GroceryQueue {
    private final int maxLength;
    private final Queue<Customer> queue;
    private final Semaphore queueSemaphore = new Semaphore(1);

    public GroceryQueue(int maxLength) {
        this.maxLength = maxLength;
        this.queue = new LinkedList<>();
    }

    public boolean addCustomer(Customer customer) {
        try {
            queueSemaphore.acquire();
            if (queue.size() < maxLength) {
                queue.offer(customer);
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            queueSemaphore.release();
        }
    }

    public Customer getNextCustomer() {
        try {
            queueSemaphore.acquire();
            return queue.poll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            queueSemaphore.release();
        }
    }

    public int getSize() {
        return queue.size();
    }
}
