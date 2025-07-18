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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.debezium.perf.Constants.ENQUEUE_BATCH_SIZE;
import static io.debezium.perf.Constants.ENQUEUE_POLL_INTERVAL;
import static io.debezium.perf.Constants.ENQUEUE_QUEUE_SIZE;

/**
 * JMH Benchmark for Debezium ChangeEventQueue performance testing.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class InheritanceEnqueueBenchmark {

    private ChangeEventQueue<DataChangeEvent> queue;
    private DataChangeEvent testEvent;

    @Setup(Level.Iteration)
    public void setup() {
        // Initialize the queue
        ChangeEventQueueConfig changeEventQueueConfig = ChangeEventQueueConfig.builder()
                .pollInterval(Duration.ofMillis(ENQUEUE_POLL_INTERVAL))
                .maxBatchSize(ENQUEUE_BATCH_SIZE)
                .maxQueueSize(ENQUEUE_QUEUE_SIZE)
                .loggingContextSupplier(() -> LoggingContext.forConnector("a", "b", "c"))
                .build();
        queue = new DefaultChangeEventQueue<>(changeEventQueueConfig);

        // Create test events
        testEvent = DataUtil.createTestEvent(0);
    }

    /**
     * Benchmark single-threaded enqueue operations
     */
    @Benchmark
    public void benchmarkEnqueue() throws InterruptedException {
        queue.enqueue(testEvent);
    }

}
