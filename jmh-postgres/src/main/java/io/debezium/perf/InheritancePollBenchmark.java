package io.debezium.perf;

import io.debezium.connector.base.ChangeEventQueue;
import io.debezium.connector.base.ChangeEventQueueConfig;
import io.debezium.connector.base.DefaultChangeEventQueue;
import io.debezium.pipeline.DataChangeEvent;
import io.debezium.util.LoggingContext;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.debezium.perf.Constants.POLL_BATCH_SIZE;
import static io.debezium.perf.Constants.POLL_POLL_INTERVAL;
import static io.debezium.perf.Constants.POLL_QUEUE_SIZE;

/**
 * JMH Benchmark for Debezium ChangeEventQueue performance testing.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class InheritancePollBenchmark {

    private ChangeEventQueue<DataChangeEvent> queue;
    private DataChangeEvent testEvent;
    // Configuration parameters
    @Setup(Level.Iteration)
    public void setup() throws InterruptedException {
        // Initialize the queue
        ChangeEventQueueConfig changeEventQueueConfig = ChangeEventQueueConfig.builder()
                .pollInterval(Duration.ofMillis(POLL_POLL_INTERVAL))
                .maxBatchSize(POLL_BATCH_SIZE)
                .maxQueueSize(POLL_QUEUE_SIZE)
                .loggingContextSupplier(() -> LoggingContext.forConnector("a", "b", "c"))
                .build();
        queue = new DefaultChangeEventQueue<>(changeEventQueueConfig);

        // Create test events
        testEvent = DataUtil.createTestEvent(0);

        for (int i = 0; i < POLL_QUEUE_SIZE; i++) {
            queue.enqueue(testEvent);
        }

        System.out.println("Setup Completed");
    }

    /**
     * Benchmark single-threaded poll operations
     */
    @Benchmark
    public void benchmarkPoll(Blackhole blackhole) throws InterruptedException {
        blackhole.consume(queue.poll());
    }
}
