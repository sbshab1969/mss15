package acp.db.service.impl.dbutil.all;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.utils.*;

public class ManagerUtilDbutil extends ManagerBaseDbutil {
  private static Logger logger = LoggerFactory.getLogger(ManagerUtilDbutil.class);
  
// Connection
  
  public Long getValueL(String strQuery, Connection conn) {
    Long val = null;
    ScalarHandler<BigDecimal> scalarHandler = new ScalarHandler<>();
    try {
      val = queryRunner.query(conn, strQuery, scalarHandler).longValue();
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      val = null;
    }
    return val;
  }

  public String getValueV(String strQuery, Connection conn) {
    String val = null;
    ScalarHandler<String> scalarHandler = new ScalarHandler<>();
    try {
      val = queryRunner.query(conn, strQuery, scalarHandler);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      val = null;
    }
    return val;
  }

  public Timestamp getValueT(String strQuery, Connection conn) {
    Timestamp val = null;
    ScalarHandler<Timestamp> scalarHandler = new ScalarHandler<>();
    try {
      val = queryRunner.query(conn, strQuery, scalarHandler);
     } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      val = null;
    }
    return val;
  }
  
// NULL 
  
  public Long getValueL(String strQuery) {
    Connection conn = dbConnect.getConnection();
    // ----------------------------------
    Long val = getValueL(strQuery, conn);
    // ----------------------------------
    dbConnect.close(conn);
    return val;
  }

  public String getValueV(String strQuery) {
    Connection conn = dbConnect.getConnection();
    // ----------------------------------
    String val = getValueV(strQuery, conn);
    // ----------------------------------
    dbConnect.close(conn);
    return val;
  }

  public Timestamp getValueT(String strQuery) {
    Connection conn = dbConnect.getConnection();
    // ----------------------------------
    Timestamp val = getValueT(strQuery, conn);
    // ----------------------------------
    dbConnect.close(conn);
    return val;
  }

// getUser

  public String getUser(Connection conn) {
    String usr = getValueV("select user from dual", conn);
    return usr;
  }

  public String getUser() {
    String usr = getValueV("select user from dual");
    return usr;
  }

// getSysdate

  public Timestamp getSysdate(Connection conn) {
    Timestamp tst = getValueT("select sysdate from dual", conn);
    return tst;
  }

  public Timestamp getSysdate() {
    Timestamp tst = getValueT("select sysdate from dual");
    return tst;
  }

}
