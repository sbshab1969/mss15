package acp.db.service.impl.dbcp.all;

import acp.Main;
import acp.db.connect.IDbConnectJdbc;

public class ManagerBaseJdbc {
  protected IDbConnectJdbc dbConnect = (IDbConnectJdbc) Main.dbConnect;
}
