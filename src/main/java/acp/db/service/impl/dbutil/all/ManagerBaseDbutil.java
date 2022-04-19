package acp.db.service.impl.dbutil.all;

import org.apache.commons.dbutils.QueryRunner;

import acp.Main;
import acp.db.connect.IDbConnectDbcp;

public class ManagerBaseDbutil {
  protected IDbConnectDbcp dbConnect = (IDbConnectDbcp) Main.dbConnect;
  protected QueryRunner queryRunner = new QueryRunner();

}
