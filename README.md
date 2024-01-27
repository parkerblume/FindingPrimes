# How to compile
In a terminal use:
`javac FindPrimes.java && java FindPrimes <num>`
- Where the *num* is an optional argument that defines the program finding all primes up to *num* and keeps track of a multitude of things and prints all its findings in a *primes.txt*.
- If *num* is omitted, it will default to 10^8 (100,000,000)

# Informal Explanation
In my multi-threaded approach to finding prime numbers up to a given limit, I knew that each thread 
needed to have a reference to the same *Tracker* object. Inside this *Tracker* object, it holds all the 
crucial data in solving this problem: the sum of primes, the count of the current prime being checked, 
the count of primes found, and a list containing the top ten maximum primes.

The importance of the *Tracker* is making sure that everything is synchronized among the threads and
is atomic. The *Tracker* has 3 important methods (`getAndIncrement()`, `updatePrimes()`, and
`updateTopTenPrimes()`) controlling all the data that is returned and/or changed among each
thread that is executed. The synchronous keyword in Java is Java’s way of being able to control when
each thread can access and execute the specific method call. There are no post/pre-operations
being made inside the methods, making it atomic.

# Experimental Evaluation
There were three key factors in maximizing efficiency. The first is understanding that no even
number (except for 2) can be prime so we can skip checking for those. Since 1 is not prime, 
we start our count from 2. After 2, we increment by 1 and then increment by 2 each time a thread 
grabs a number because this will keep each number it is checking to be odd. 

The next is the `isPrime()` method, there are many efficient methods to go about this. Throughout my courses, I’ve learned about *Sieve of Eratosthenes* and took that method but developed a quicker way for our purposes. I first checked if a number was less than or equal to one or if it was divisible by 3 – otherwise, we started at 1 and skipped every two numbers as we know from skipping even numbers to not check for divisibility by 2. 

The last key factor was using a *PriorityQueue* data structure for efficient retrieval and replacement which helped speed up runtime tremendously.

These realizations were through a lot of trial and error from first having an extremely
underwhelming time of 43 seconds, to now getting each run in between 10 – 20 seconds depending on
the processors’ performance. Using 4 threads takes about 5 seconds longer than 8. However, using between 6-8
threads take roughly the same amount of time. Any more than 8, you are just using more
resources for not enough of a performance boost. The big plus about this program is the space efficiency
as the data structure that holds the maximum primes never has more than 10 elements in it, which
greatly reduces memory usage as well as speeds up runtime.
