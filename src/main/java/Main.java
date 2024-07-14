import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import sqlite.database.Database;
import sqlite.domain.Table;
import sqlite.domain.TableRow;
import sqlite.query.QueryExecutor;
import sqlite.query.QueryCount;
import sqlite.query.SelectQuery;

public class Main {

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("Missing <database path> and <command>");
      return;
    }

    String databaseFilePath = args[0];
    String command = args[1];

    var data = Files.readAllBytes(Path.of(databaseFilePath));
    Database db = new Database(ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN));
    switch (command) {
      case ".dbinfo" -> {
        System.out.println("database page size: " + db.getHeader().pageSize());
        System.out.println("number of tables: " +
            db.
                getSchema().
                tables().
                stream().
                count());
      }
      case ".tables" -> {
        System.out.println(
            db.getSchema()
                .tables()
                .stream()
                .map(Table::name)
                .collect(joining(" ")
                ));
      }
      default -> {
        var executor = new QueryExecutor(db);
        var query = executor.get(command);
        var result = query.execute(command);

        if (query instanceof QueryCount) {
          result.stream().findFirst().ifPresent(row -> {
            System.out.println(row.values().get(0));
          });

        }

        if (query instanceof SelectQuery) {
          var list = result.stream().flatMap(r -> r.values().stream()).toList();
          for (var v : list) {
            System.out.println(v.toString());
          }
        }
      }
    }
  }
}
