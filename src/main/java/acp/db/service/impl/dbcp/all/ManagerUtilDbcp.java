package acp.db.service.impl.dbcp.all;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.utils.*;

public class ManagerUtilDbcp extends ManagerBaseDbcp {
  private static Logger logger = LoggerFactory.getLogger(ManagerUtilDbcp.class);

// Connection
  
  public Long getValueL(String strQuery, Connection conn) {
    Long val = null;
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(strQuery);
      // -----------------
      rs.next();
      val = rs.getLong(1);
      // -----------------
      rs.close();
      stmt.close();
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      val = null;
    }
    return val;
  }

  public String getValueV(String strQuery, Connection conn) {
    String val = null;
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(strQuery);
      // -----------------
      rs.next();
      val = rs.getString(1);
      // -----------------
      rs.close();
      stmt.close();
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      val = null;
    }
    return val;
  }

  public Timestamp getValueT(String strQuery, Connection conn) {
    Timestamp val = null;
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(strQuery);
      // -----------------
      rs.next();
      val = rs.getTimestamp(1);
      // -----------------
      rs.close();
      stmt.close();
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
