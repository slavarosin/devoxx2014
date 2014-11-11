package eu.javaspecialists.deadlock.lab1;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * At the symposium, we create a bunch of thinkers and place cups of wine
 * between them.  We then run them in a cached thread pool.  Unfortunately when
 * all the Thinker instances try to drink at the same time, they cause a
 * deadlock.
 *
 * DO NOT CHANGE THIS CODE!
 *
 * @author Heinz Kabutz
 */
public class Symposium {
    private final Krasi[] cups;
    private final Thinker[] thinkers;

    public Symposium(int delegates) {
        cups = new Krasi[delegates];
        thinkers = new Thinker[delegates];
        for (int i = 0; i < cups.length; i++) {
            cups[i] = new Krasi(i);
        }
        for (int i = 0; i < delegates; i++) {
            Krasi left = cups[i];
            Krasi right = cups[(i + 1) % delegates];
            thinkers[i] = new Thinker(i, left, right);
        }
    }

    public ThinkerStatus run() throws InterruptedException {
        // do this after we created the symposium, so that we do not
        // let the reference to the Symposium escape.
        ExecutorService exec = Executors.newCachedThreadPool();
        CompletionService<ThinkerStatus> results =
                new ExecutorCompletionService<ThinkerStatus>(exec);
        for (Thinker thinker : thinkers) {
            results.submit(thinker);
        }
        ThinkerStatus result = ThinkerStatus.HAPPY_THINKER;
        System.out.println("Waiting for results");
        for (Thinker thinker : thinkers) {
            try {
                ThinkerStatus status = results.take().get();
                System.out.println(status);
                if (status == ThinkerStatus.UNHAPPY_THINKER) {
                    result = ThinkerStatus.UNHAPPY_THINKER;
                }
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            }
        }
        exec.shutdown();
        return result;
    }
}
