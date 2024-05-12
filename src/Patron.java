//M. M. Kuttel 2024 mkuttel@gmail.com
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.LongStream;

/*
 This is the basicclass, representing the patrons at the bar
 */

public class Patron extends Thread {

	private Random random = new Random();// for variation in Patron behaviour

	private CountDownLatch startSignal; // all start at once, actually shared
	private Barman theBarman; // the Barman is actually shared though

	private int ID; // thread ID
	private int lengthOfOrder;
	private long startTime, endTime; // for all the metrics
	private long[] turnaroundTimes;

	public static FileWriter fileW;

	private DrinkOrder[] drinksOrder;

	Patron(int ID, CountDownLatch startSignal, Barman aBarman) {
		this.ID = ID;
		this.startSignal = startSignal;
		this.theBarman = aBarman;
		this.lengthOfOrder = random.nextInt(5) + 1;// between 1 and 5 drinks

		turnaroundTimes = new long[lengthOfOrder];

		drinksOrder = new DrinkOrder[lengthOfOrder];
	}

	public void writeToFile(String data) throws IOException {
		synchronized (fileW) {
			fileW.write(data);
		}
	}

	public void run() {
		try {
			// Do NOT change the block of code below - this is the arrival times
			startSignal.countDown(); // this patron is ready
			startSignal.await(); // wait till everyone is ready
			int arrivalTime = random.nextInt(300) + ID * 100; // patrons arrive gradually later
			sleep(arrivalTime);// Patrons arrive at staggered times depending on ID
			System.out.println("thirsty Patron " + this.ID + " arrived");
			// END do not change

			// Used to calculate waiting time later
			long totalExecutionTime = 0;
			// create drinks order
			for (int i = 0; i < lengthOfOrder; i++) {
				drinksOrder[i] = new DrinkOrder(this.ID);
				totalExecutionTime += drinksOrder[i].getExecutionTime();
			}

			System.out.println("Patron " + this.ID + " submitting order of " + lengthOfOrder + " drinks");
			startTime = System.currentTimeMillis();// started placing orders
			for (int i = 0; i < lengthOfOrder; i++) {
				System.out.println("Order placed by " + drinksOrder[i].toString());
				theBarman.placeDrinkOrder(drinksOrder[i]);
				// We store when each drink was ordered for turnaround later
				turnaroundTimes[i] = System.currentTimeMillis();
			}

			long responseTime = -1;
			// lengthOfOrder >= 1 so responseTime != -1
			for (int i = 0; i < lengthOfOrder; i++) {
				drinksOrder[i].waitForOrder();
				if (i == 0) {
					responseTime = System.currentTimeMillis() - startTime;
				}
				turnaroundTimes[i] = System.currentTimeMillis() - turnaroundTimes[i];
			}

			endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			long turnaroundAvg = LongStream.of(turnaroundTimes).sum() / lengthOfOrder;
			// Any time that the patron wasn't having their drink made, they were waiting
			long waitingTime = totalTime - totalExecutionTime;

			writeToFile(String.format("%d,%d,%d,%d,%d,%d\n", ID, arrivalTime, totalTime, turnaroundAvg, waitingTime,
					responseTime));
			System.out.println("Patron " + this.ID + " got order in " + totalTime);

		} catch (InterruptedException e1) { // do nothing
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
