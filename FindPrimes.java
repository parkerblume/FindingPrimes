import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

class Primes extends Thread {
    private final Tracker tracker;
    private final long endNumber;

    public Primes(Tracker tracker, long val)
    {
        this.tracker = tracker;
        this.endNumber = val;
    }

    private boolean isPrime(long val)
    {
        // Sieve of Eratosthenes without keeping track of previously found primes
        if (val <= 1 || (val > 3 & val % 3 == 0)) { return false; }
        for (long i = 3; i * i <= val; i += 2)
        {
            if (val % i == 0) { return false; }
        }
        return true;
    }

    @Override
    public void run() {
        long j;
        while ((j = tracker.getAndIncrement()) <= endNumber) {
            if (isPrime(j)) {
                tracker.updatePrimes(j);
            }
        }
    }
}


class Tracker {
    private long count;
    private long primesFound;
    private long sum;
    private PriorityQueue<Long> topTenPrimes;

    public Tracker(long startVal)
    {
        this.count = startVal;
        this.sum = 2;
        this.primesFound = 1;
        topTenPrimes = new PriorityQueue<>();
    }

    public synchronized long getAndIncrement() {
        long temp = count;
        count = temp % 2 == 0 ? temp + 1 : temp + 2;
        return temp; 
    }

    public synchronized void updatePrimes(long prime) {
        long temp = sum;
        sum = temp + prime;
        temp = primesFound;
        primesFound = temp + 1;
        updateTopTenPrimes(prime);
    }

    public synchronized void updateTopTenPrimes(long prime)
    {
        // PriorityQueue syntax is funny. I offer you a prime my lord.
        // keeps size to 10, allowing for easier sorting later
        if (topTenPrimes.size() < 10) 
        {
            topTenPrimes.offer(prime);
        } 
        else if (prime > topTenPrimes.peek()) 
        {
            topTenPrimes.poll();
            topTenPrimes.offer(prime);
        }
    }

    public long getSum()
    {
        return this.sum;
    }

    public long getPrimesFound()
    {
        return this.primesFound;
    }

    public PriorityQueue<Long> getPrimeList()
    {
        return this.topTenPrimes;
    }
}

public class FindPrimes {
    public static Long[] getSortedPrimes(PriorityQueue<Long> list)
    {
        Long [] retval = new Long[10];
        int i = 0;
        for (Long prime : list)
        {
            retval[i++] = prime;
        }

        Arrays.sort(retval);
        return retval;
    }

    public static void printMaxPrimes(Long[] primes, PrintWriter out)
    {
        out.print("[");
        for (int i = 0; i < primes.length; i++)
        {
            if (i == primes.length - 1)
            {
                out.print(primes[i] + "]");
                break;
            }

            out.print(primes[i] + ", ");
        }
    }
    public static void main(String[] args) throws IOException {
        long startTime, endTime, totalTime = 0;
        int numOfThreads = 6;
        long primesToFind = (long) 1e8;
        Primes[] threads = new Primes[numOfThreads];
        Tracker tracker = new Tracker(2); // 1 is not prime, therefore start with 2.

        // allow someone to choose any number they want to calculate, otherwise defaults to 10^8.
        if (args.length >= 1)
        {
            try {
                long val = Integer.parseInt(args[0]);
                primesToFind = val;
            } catch (Exception e)
            {
                System.err.println("Not a valid value, make sure to input a number!");
                System.err.println(e.toString());
            }
        }

        startTime = System.currentTimeMillis();
        for (int i = 0; i < numOfThreads; i++) {
            threads[i] = new Primes(tracker, primesToFind);
            threads[i].start();
        }

        for (Primes thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        endTime = System.currentTimeMillis();
        totalTime = (endTime - startTime);
        long timeInSeconds = TimeUnit.SECONDS.convert(totalTime, TimeUnit.MILLISECONDS);

        // print everything to primes.txt
        FileWriter out = new FileWriter("primes.txt");
        PrintWriter print = new PrintWriter(out);
        print.println(totalTime + "ms (" + timeInSeconds +"s)\t" + tracker.getPrimesFound() + " \t" + tracker.getSum());
        Long[] sortedPrimes = getSortedPrimes(tracker.getPrimeList());
        printMaxPrimes(sortedPrimes, print);
        print.close();
        out.close();
    }
}