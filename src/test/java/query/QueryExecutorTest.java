package query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;

import sqlite.database.Database;
import sqlite.query.QueryCount;
import sqlite.query.QueryExecutor;
import sqlite.query.SelectQuery;

public class QueryExecutorTest {

    @Test
    void executorTest() throws IOException {
        var channel = Files.newByteChannel(Path.of("sample.db"));
        var db = new Database(channel);
        var executor = new QueryExecutor(db);
        var countQuery = executor.get("select count(*) from companies");
        var selectQuery = executor.get("select name, company from companies where country = 'eritrea'");

        assertInstanceOf(QueryCount.class, countQuery);
        assertInstanceOf(SelectQuery.class, selectQuery);
    }
}
