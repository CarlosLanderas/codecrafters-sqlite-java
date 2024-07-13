import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

record Record (
  List<Integer> columnTypes,
  List<String> values
) {
  public static Record parse(Cell cell) {
    var buf = ByteBuffer.wrap(cell.payload()).order(ByteOrder.BIG_ENDIAN);
    var columnTypes = getColumns(buf);
    var values = getValues(buf, columnTypes);

    return new Record(columnTypes, values);
  }

  private static List<Integer> getColumns(ByteBuffer buff) {
    var length = VarInt.parse(buff);

    long remaining = length.value() - length.size();

    List<Integer> columnsTypes = new ArrayList<>();

    while (remaining > 0) {
      var varInt = VarInt.parse(buff);
      columnsTypes.add((int)varInt.value());
      remaining -= varInt.size();
    }

    return columnsTypes;
  }

  private static List<String> getValues(ByteBuffer buf, List<Integer> columnsTypes) {
    List<String> values =  new ArrayList<>();
    for (var colType : columnsTypes) {
      switch (colType) {
        case 0 -> values.add("NULL");
        case 1 -> values.add(String.valueOf(buf.get()));
        case 2 -> values.add(String.valueOf(buf.getShort()));
        case 3 -> throw new RuntimeException("not implemented");
        case 4 -> values.add(String.valueOf(buf.getInt()));
        case 5 -> throw new RuntimeException("not implemented");
        case 6 -> values.add(String.valueOf(buf.getLong()));
        case 7 -> values.add(String.valueOf(buf.getFloat()));
        case 8 -> values.add("0");
        case 9 -> values.add("1");
        default -> {
          int contentSize = 0;
          if (colType >= 12 && colType % 2 == 0) {
            contentSize = (colType - 12) / 2;
          }
          if (colType >= 13 && colType % 2 == 1) {
            contentSize = (colType - 13) / 2;
          }
          if (contentSize > 0) {
            byte[] contents = new byte[contentSize];
            buf.get(contents);
            values.add(new String(contents));
          } else {
            values.add("");
          }
        }
      }
    }
    return values;
  }

  public boolean isTable() {
    return this.values.getFirst().equals("table");
  }

  public boolean isIndex() {
    return this.values.getFirst().equals("index");
  }
}

record Table(String name, long rootPage, String sql) {
    public static Table fromRecord(Record record) {
      var name = record.values().get(2);
      var rootPage =  Long.parseLong(record.values().get(3));
      var sql = record.values().get(4);

      return new Table(name,rootPage, sql);
    }
}
