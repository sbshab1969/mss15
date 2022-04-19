package acp.db.service.impl.jdbc.all;

import acp.Main;
import acp.db.connect.IDbConnectJdbc;

public class ManagerBaseJdbc {
  protected IDbConnectJdbc dbConnect = (IDbConnectJdbc) Main.dbConnect;
}
