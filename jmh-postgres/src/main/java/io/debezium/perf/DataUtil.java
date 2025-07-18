package io.debezium.perf;

import io.debezium.pipeline.DataChangeEvent;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Collections;

public class DataUtil {

    public static DataChangeEvent createTestEvent(int i) {
        Schema keySchema = SchemaBuilder.struct()
                .field("id", Schema.INT32_SCHEMA)
                .build();

        Schema valueSchema = SchemaBuilder.struct()
                .field("id", Schema.INT32_SCHEMA)
                .field("name", Schema.STRING_SCHEMA)
                .field("email", Schema.STRING_SCHEMA)
                .build();

        Struct key = new Struct(keySchema)
                .put("id", i);

        Struct value = new Struct(valueSchema)
                .put("id", i)
                .put("name", "User " + i)
                .put("email", "user" + i + "@example.com");

        SourceRecord record = new SourceRecord(
                Collections.singletonMap("server", "test-server"),
                Collections.singletonMap("file", "test-file"),
                "test.topic",
                keySchema,
                key,
                valueSchema,
                value
        );

        return new DataChangeEvent(record);
    }
}
