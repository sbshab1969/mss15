package acp.db.service.impl.hiber.all;

import acp.Main;
import acp.db.connect.IDbConnectHiber;

public class ManagerBaseHiber {
  protected IDbConnectHiber dbConnect = (IDbConnectHiber) Main.dbConnect;
}
