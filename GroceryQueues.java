import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroceryQueues {
    private final GroceryQueue[] queues;
    private final Random random;

    public GroceryQueues(int numberOfQueues, int maxLength) {
        queues = new GroceryQueue[numberOfQueues];
        for (int i = 0; i < numberOfQueues; i++) {
            queues[i] = new GroceryQueue(maxLength);
        }
        this.random = new Random();
    }

    public boolean addCustomer(Customer customer) {
        GroceryQueue shortestQueue = queues[0];
        for (GroceryQueue queue : queues) {
            if (queue.getSize() < shortestQueue.getSize()) {
                shortestQueue = queue;
            }
        }
        return shortestQueue.addCustomer(customer);
    }

    public GroceryQueue[] getQueues() {
        return queues;
    }
}
