package acp.db.connect;

import java.sql.Connection;

public interface IDbConnectDbcp extends IDbConnect {

  Connection getConnection();
  void close(Connection conn);

}