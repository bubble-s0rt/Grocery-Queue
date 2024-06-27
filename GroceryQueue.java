import java.util.LinkedList;
import java.util.Queue;

public class GroceryQueue {
    private final int maxLength;
    private final Queue<Customer> queue;

    public GroceryQueue(int maxLength) {
        this.maxLength = maxLength;
        this.queue = new LinkedList<>();
    }

    public boolean addCustomer(Customer customer) {
        if (queue.size() < maxLength) {
            queue.offer(customer);
            return true;
        }
        return false;
    }

    public Customer getNextCustomer() {
        return queue.poll();
    }

    public int getSize() {
        return queue.size();
    }
}
