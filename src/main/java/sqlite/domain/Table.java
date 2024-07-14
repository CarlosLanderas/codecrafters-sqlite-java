package sqlite.domain;

import java.util.List;

public record Table(String name, long rootPage, String sql) {

  public static Table fromRecord(Record record) {
    var name = record.values().get(2);
    var rootPage = Long.parseLong(record.values().get(3).toString());
    var sql = record.values().get(4);

    return new Table(name.toString(), rootPage, sql.toString());
  }
}
