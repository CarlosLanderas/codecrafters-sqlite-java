import java.util.List;

record Table(String name, long rootPage, String sql) {

  public static Table fromRecord(Record record) {
    var name = record.values().get(2);
    var rootPage = Long.parseLong(record.values().get(3).toString());
    var sql = record.values().get(4);

    return new Table(name.toString(), rootPage, sql.toString());
  }
}

record TableRow(List<Object> values) {

  public Object get(int index) {
    return values.get(index);
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    for (var v : values) {
      sb.append(" " + v.toString());
    }

    return sb.toString();
  }
}