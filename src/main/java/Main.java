import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Missing <database path> and <command>");
      return;
    }

    String databaseFilePath = args[0];
    String command = args[1];

    switch (command) {
      case ".dbinfo" -> {
        try {

          var data = Files.readAllBytes(Path.of(databaseFilePath));
          //Files.newByteChannel(Path.of(databaseFilePath))
          Database db = new Database(ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN));

          db.getPage(0);

          System.out.println("database page size: " + db.getHeader().pageSize());
          System.out.println("number of tables: " + db.getPage(0).header.numberCells());


        } catch (IOException e) {
          System.out.println("Error reading file: " + e.getMessage());
        }
      }
      default -> System.out.println("Missing or invalid command passed: " + command);
    }
  }
}
