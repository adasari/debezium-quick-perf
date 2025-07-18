package io.debezium.perf;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunnerMain {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(QueueManagerEnqueueBenchmark.class.getSimpleName())
                .include(InheritanceEnqueueBenchmark.class.getName())

//                .include(PollBenchmark.class.getSimpleName())
//                .include(QueueManagerPollBenchmark.class.getSimpleName())
                .jvmArgs("-Xmx16g", "-Xms16g")
                .build();
        new Runner(opt).run();
    }
}
