import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroceryQueues {
    private final List<GroceryQueue> queues;
    private final int maxQueueLength;
    private final Random random;

    public GroceryQueues(int numberOfQueues, int maxQueueLength) {
        this.queues = new ArrayList<>(numberOfQueues);
        for (int i = 0; i < numberOfQueues; i++) {
            queues.add(new GroceryQueue(maxQueueLength));
        }
        this.maxQueueLength = maxQueueLength;
        this.random = new Random();
    }

    public boolean addCustomer(Customer customer) {
        GroceryQueue shortestQueue = queues.get(0);
        for (GroceryQueue queue : queues) {
            if (queue.getQueueSize() < shortestQueue.getQueueSize()) {
                shortestQueue = queue;
            }
        }

        if (shortestQueue.getQueueSize() < maxQueueLength) {
            return shortestQueue.addCustomer(customer);
        } else {
            customer.setServed(false);
            return false;
        }
    }

    public Customer getNextCustomer(int queueIndex) {
        return queues.get(queueIndex).getNextCustomer();
    }

    public int getNumberOfQueues() {
        return queues.size();
    }
}
