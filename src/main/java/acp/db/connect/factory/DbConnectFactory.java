package acp.db.connect.factory;

import acp.Main;
import acp.db.connect.IDbConnect;
import acp.db.connect.impl.DbConnectDbcp;
import acp.db.connect.impl.DbConnectHiber;
import acp.db.connect.impl.DbConnectJdbc;

public class DbConnectFactory {

  public static IDbConnect getDbConnect() {
    switch (Main.dbConn) {
    case JDBC:
      return new DbConnectJdbc();
    case DBCP:
      return new DbConnectDbcp();
    case HIBER:
      return new DbConnectHiber();
    }
    return null;
  }

}
