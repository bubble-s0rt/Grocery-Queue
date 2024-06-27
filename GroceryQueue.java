import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class GroceryQueue {
    private final int maxLength;
    private final Queue<Customer> queue;
    private final Semaphore queueSemaphore;

    public GroceryQueue(int maxLength) {
        this.maxLength = maxLength;
        this.queue = new LinkedList<>();
        this.queueSemaphore = new Semaphore(1);
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
        try {
            queueSemaphore.acquire();
            return queue.size();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } finally {
            queueSemaphore.release();
        }
    }
}
