package acp.db.service.impl.jdbc.all;

import java.sql.Connection;

import acp.Main;
import acp.db.connect.IDbConnectJdbc;

public class ManagerBaseJdbc {
  protected IDbConnectJdbc dbConnect = (IDbConnectJdbc) Main.dbConnect;
  protected Connection dbConn = dbConnect.getConnection();
}
