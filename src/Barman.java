import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/*
 Barman Thread class.
 */

public class Barman extends Thread {
	// How long each drink is made for before another drink is continued
	public static final int roundRobinQuantum = 50;

	private int schedAlg;
	private CountDownLatch startSignal;
	private BlockingQueue<DrinkOrder> orderQueue;

	// This comparator will allow us to order drinks in the priority queue by
	// execution time
	class DrinkOrderComparator implements Comparator<DrinkOrder> {
		@Override
		public int compare(DrinkOrder drinkA, DrinkOrder drinkB) {
			// We simply defer to the execution times
			return Integer.compare(drinkA.getExecutionTime(), drinkB.getExecutionTime());
		}
	}

	Barman(CountDownLatch startSignal, int schedAlg) throws IllegalArgumentException {
		this.schedAlg = schedAlg;
		if (schedAlg == 0 || schedAlg == 2)
			this.orderQueue = new LinkedBlockingQueue<DrinkOrder>();
		else if (schedAlg == 1) {
			// Allows us to order the drinks by execution time and perform quicker drinks
			// first
			this.orderQueue = new PriorityBlockingQueue<DrinkOrder>(100, new DrinkOrderComparator());
		} else {
			throw new IllegalArgumentException("Scheduling algorithm must be within 0, 2 inclusive");
		}

		this.startSignal = startSignal;
	}

	public void placeDrinkOrder(DrinkOrder order) throws InterruptedException {
		if (schedAlg == 2)
			// Set the remaining prep time so later we can decrease it for the round robin
			// alg.
			order.initRemainingPrepTime();
		orderQueue.put(order);
	}

	public void run() {
		try {
			DrinkOrder nextOrder;

			startSignal.countDown(); // barman ready
			startSignal.await(); // check latch - don't start until told to do so

			while (true) {
				nextOrder = orderQueue.take();
				System.out.println("---Barman preparing order for patron " + nextOrder.toString());
				if (schedAlg != 2) {
					// Previous code
					sleep(nextOrder.getExecutionTime()); // processing order
					System.out.println("---Barman has made order for patron " + nextOrder.toString());
					nextOrder.orderDone();
				} else {
					// Check if we can complete the drink in a single quantum
					int remainder = nextOrder.getRemainingPrepTime();
					if (remainder <= roundRobinQuantum) {
						sleep(remainder);
						System.out.println("---Barman has made order for patron " + nextOrder.toString());
						nextOrder.orderDone();
					} else {
						// Otherwise we execute for a quantum and move on
						sleep(roundRobinQuantum);
						System.out.println(
								"---Barman continued " + nextOrder.toString() + " and has moved onto another drink");
						nextOrder.reduceRemainingPrepTime(roundRobinQuantum);
						// We add it back to the queue so we can retrieve it in order later
						orderQueue.add(nextOrder);
					}
				}
			}

		} catch (InterruptedException e1) {
			System.out.println("---Barman is packing up ");
		}
	}
}
