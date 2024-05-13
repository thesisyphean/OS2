//M. M. Kuttel 2024 mkuttel@gmail.com
// the main class, starts all threads

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SchedulingSimulation {
	static int noPatrons = 100; // number of customers - default value if not provided on command line
	static int sched = 0; // which scheduling algorithm, 0= FCFS

	static CountDownLatch startSignal;

	static Patron[] patrons; // array for customer threads
	static Barman Andre;
	static FileWriter writer;

	public void writeToFile(String data) throws IOException {
		synchronized (writer) {
			writer.write(data);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// deal with commandline arguments if provided
		if (args.length >= 1) {
			noPatrons = Integer.parseInt(args[0]); // total people to enter room
		}

		if (args.length >= 2) {
			sched = Integer.parseInt(args[1]);
		}

		// Get the file to output to from the commandline if possible
		if (args.length >= 3) {
			writer = new FileWriter("dat/" + args[2] + ".csv", false);
		} else {
			writer = new FileWriter("dat/default.csv", false);
		}
		writer.write("ID,ArrivalTime,TotalTime,TurnaroundAvg,WaitingTime,ResponseTime\n");
		Patron.fileW = writer;

		startSignal = new CountDownLatch(noPatrons + 2);// Barman and patrons and main method must be raeady

		// create barman
		Andre = new Barman(startSignal, sched);
		Andre.start();

		// create all the patrons, who all need access to Andre
		patrons = new Patron[noPatrons];
		for (int i = 0; i < noPatrons; i++) {
			patrons[i] = new Patron(i, startSignal, Andre);
			patrons[i].start();
		}

		System.out.println("------Andre the Barman Scheduling Simulation------");
		System.out.println("-------------- with " + Integer.toString(noPatrons) + " patrons---------------");

		startSignal.countDown(); // main method ready

		// wait till all patrons done, otherwise race condition on the file closing!
		for (int i = 0; i < noPatrons; i++) {
			patrons[i].join();
		}

		System.out.println("------Waiting for Andre------");
		Andre.interrupt(); // tell Andre to close up
		Andre.join(); // wait till he has
		writer.close(); // all done, can close file
		System.out.println("------Bar closed------");
	}

}
