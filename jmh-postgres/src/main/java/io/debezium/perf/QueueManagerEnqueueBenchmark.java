package io.debezium.perf;

import io.debezium.connector.base.ChangeEventQueueManager;
import io.debezium.connector.base.DefaultQueueProvider;
import io.debezium.connector.base.QueueProvider;
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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.debezium.perf.Constants.ENQUEUE_BATCH_SIZE;
import static io.debezium.perf.Constants.ENQUEUE_NUM_TEST_EVENTS;
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
public class QueueManagerEnqueueBenchmark {

    private ChangeEventQueueManager<DataChangeEvent> queue;
    private DataChangeEvent testEvent;
    // Configuration parameters

    @Setup(Level.Iteration)
    public void setup() {
        // Initialize the queue
        QueueProvider<DataChangeEvent> queueProvider = new DefaultQueueProvider<>(ENQUEUE_QUEUE_SIZE);
        queue = new ChangeEventQueueManager.Builder<DataChangeEvent>()
                .pollInterval(Duration.ofMillis(ENQUEUE_POLL_INTERVAL))
                .maxBatchSize(ENQUEUE_BATCH_SIZE)
                .maxQueueSize(ENQUEUE_QUEUE_SIZE)
                .queueProvider(queueProvider)
                .loggingContextSupplier(() -> LoggingContext.forConnector("a", "b", "c"))
                .build();

        // Create test events
        testEvent = DataUtil.createTestEvent(0);
    }

    /**
     * Benchmark single-threaded enqueue operations
     */
    @Benchmark
    public void benchmarkEnqueue() throws InterruptedException {
//        System.out.println("adding");
        queue.enqueue(testEvent);
    }
}
