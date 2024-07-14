import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

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
        // Temporally process queries here
        var queryCount = new QueryCount(db);
        if (queryCount.isCountQuery(command)) {
          var total = queryCount.count(command);
          System.out.println(total);
        }
      }
    }
  }
}
