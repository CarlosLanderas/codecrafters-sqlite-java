package sqlite.domain;

import java.util.Objects;

public record Table(String type, String name, long rootPage, String sql) {

  public static Table fromRecord(Record record) {
    var type = record.values().get(0);
    var name = record.values().get(2);
    var rootPage = Long.parseLong(record.values().get(3).toString());
    var sql = record.values().get(4);

    return new Table(type.toString(), name.toString(), rootPage, sql.toString());
  }

  public boolean isIndex() {
    return Objects.equals(type, "index");
  }

  public boolean isTable() {
    return Objects.equals(type, "table");
  }
}
