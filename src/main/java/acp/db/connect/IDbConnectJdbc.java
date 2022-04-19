package acp.db.connect;

import java.sql.Connection;

public interface IDbConnectJdbc extends IDbConnect {

  Connection getConnection();

}