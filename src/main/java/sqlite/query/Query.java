package sqlite.query;

import java.io.IOException;
import java.util.Collection;
import sqlite.domain.TableRow;

public interface Query {
  Collection<TableRow> execute(String sqlQuery) throws IOException;
}
