import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryCount {

  private final Pattern selectPattern = Pattern.compile("SELECT COUNT\\((.*)\\) FROM (.*)", Pattern.CASE_INSENSITIVE);
  private final Database database;

  public QueryCount(Database database) {
    this.database = database;
  }

  public int count(String query) throws IOException {
    Matcher m = selectPattern.matcher(query);
    int counter = 0;
    if (m.matches()) {
      var matchTable = m.group(2);
      var table =  database.getSchema().findTable(matchTable);
      if (table.isEmpty()) {
        throw new RuntimeException("Table not found:" + query);
      }

      var reader = new TableRowReader(database, table.get());
      while(reader.hasNext()) {
        var row = reader.next();
        counter++;
      }
    }

    return counter;
  }

  public boolean isCountQuery(String query) { //Temporal
    return selectPattern.matcher(query).matches();
  }
}
