package acp.db.service.impl.dbcp.all;

import acp.Main;
import acp.db.connect.IDbConnectDbcp;

public class ManagerBaseDbcp {
  protected IDbConnectDbcp dbConnect = (IDbConnectDbcp) Main.dbConnect;
}
