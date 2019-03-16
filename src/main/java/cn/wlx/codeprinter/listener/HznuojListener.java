package cn.wlx.codeprinter.listener;

import java.sql.*;

public class HznuojListener implements Listener {
  // JDBC 驱动名及数据库 URL
  private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  private String DB_URL      = "jdbc:mysql://localhost:3306/jol";

  // 数据库的用户名与密码，需要根据自己的设置
  static final String USER = "root";
  static final String PASS = "root";

  private void setCodeAsPrinted(Connection conn, int id) throws Exception {
    Statement stmt = conn.createStatement();
    String sql = "UPDATE printer_code set status=1 WHERE id=" + id;
    stmt.executeUpdate(sql);
    stmt.close();
  }

  private String genHeaderBlock(String header) {
    return "********************************************************\n"
        + header
        + "\n********************************************************\n";

  }

  public void run(ListenerCallback callback) {
    // 注册 JDBC 驱动
    try {
      Class.forName(JDBC_DRIVER);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
      while (true) {
        Statement stmt = conn.createStatement();
        String sql = "SELECT printer_code.id, code, team.user_id, team.nick FROM printer_code "
            + "LEFT JOIN team ON printer_code.contest_id=team.contest_id AND printer_code.user_id=team.user_id"
            + " WHERE status=0";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
          int id = rs.getInt("id");
          String code = rs.getString("code");
          String user_id = rs.getString("user_id");
          String nick = rs.getString("nick");
          System.out.println(String.format("got code, user_id=%s, nick=%s, id=%d",
              user_id, nick, id));
          String textToBePrinted = genHeaderBlock(nick) + "\n" + code;
          callback.print(textToBePrinted);
          setCodeAsPrinted(conn, id);
        }
        rs.close();
        stmt.close();
        Thread.sleep(3000);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setDbUrl(String dbUrl) {
    this.DB_URL = dbUrl;
  }
}
