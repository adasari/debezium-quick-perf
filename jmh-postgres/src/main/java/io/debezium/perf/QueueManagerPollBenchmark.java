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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.debezium.perf.Constants.POLL_BATCH_SIZE;
import static io.debezium.perf.Constants.POLL_NUM_TEST_EVENTS;
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
public class QueueManagerPollBenchmark {

    private ChangeEventQueueManager<DataChangeEvent> queue;
    private DataChangeEvent testEvent;
    // Configuration parameters

    @Setup(Level.Iteration)
    public void setup() throws InterruptedException {
        // Initialize the queue
        QueueProvider<DataChangeEvent> queueProvider = new DefaultQueueProvider<>(POLL_QUEUE_SIZE);
        queue = new ChangeEventQueueManager.Builder<DataChangeEvent>()
                .pollInterval(Duration.ofMillis(POLL_POLL_INTERVAL))
                .maxBatchSize(POLL_BATCH_SIZE)
                .maxQueueSize(POLL_QUEUE_SIZE)
                .queueProvider(queueProvider)
                .loggingContextSupplier(() -> LoggingContext.forConnector("a", "b", "c"))
                .build();

        // Create test events
        testEvent = DataUtil.createTestEvent(0);

        for (int i = 0; i < POLL_QUEUE_SIZE; i++) {
            queue.enqueue(testEvent);
        }
    }

    /**
     * Benchmark single-threaded poll operations
     */
    @Benchmark
    public void benchmarkPoll(Blackhole blackhole) throws InterruptedException {
        blackhole.consume(queue.poll());
    }
}
