package sqlite.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParser {
  public static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("CREATE TABLE .+?\\s*\\((.+?)\\)", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
  public static final Pattern COLUMN_PATTERN = Pattern.compile("(?:(\\w+)|\"(.*?)\").*?(?:,|$)", Pattern.MULTILINE | Pattern.DOTALL);


  public static List<String> parseColumnNames(String sqlTable) {
    List<String> columnNames = new ArrayList<>();
    Matcher matcher = CREATE_TABLE_PATTERN.matcher(sqlTable);

    if (matcher.find()) {
      String columnsPart = matcher.group(1);
      Matcher columnNameMatcher = COLUMN_PATTERN.matcher(columnsPart);

      while(columnNameMatcher.find()) {
        columnNames.add(columnNameMatcher.group(1));
      }
    }

    return columnNames;
  }
}
