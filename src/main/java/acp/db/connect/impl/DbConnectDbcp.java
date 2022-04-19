package acp.db.connect.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.Main;
import acp.db.DbcpMode;
import acp.db.connect.IDbConnectDbcp;

public class DbConnectDbcp extends DbConnect implements IDbConnectDbcp {
  private static Logger logger = LoggerFactory.getLogger(DbConnectDbcp.class);

  private static DataSource dataSource;
  private static Connection dbConn;

  private static int MAX_TOTAl = 1; 
  
  public DbConnectDbcp() {
    dbPath = "/config/jdbc";
    dbExt = "xml";
    dbDefaultName = "oracle.xml";

    dbKeyIndex = "index";
    dbKeyName  = "name";
    dbKeyFullName  = "fullName";
    
    dbKeyUser = "user";
    dbKeyPassword = "password";
    dbKeyConnString = "connectionString";
    dbKeyDriver = "driver";
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public Connection getDbConn() {
    return dbConn;
  }
  
// --------------------------------
  
  @Override
  public boolean testConnect() {
    boolean ret = false;
    if (Main.dbcpMode == DbcpMode.SINGLE) {
      ret = testConn();
    } else {
      ret = testDS();
    }
    return ret;
  }

  private boolean testConn() {
    if (dbConn != null) {
      return true;
    } else {
      return false;
    }  
  }

  private boolean testDS() {
    if (dataSource != null) {
      return true;
    } else {
      return false;
    }  
  }

  @Override
  public boolean connect() {
    boolean ret = false;
    System.out.println("connecting ...");
    switch (Main.dbcpMode) {
    case SINGLE:
      ret = connectConn();
      break;
    case BASE_DS:
      ret = connectBDS();
      break;
    case SPOOL_DS:
      ret = connectSPDS();
      break;
    default:
      ret = false;
    }  
    System.out.println("Datasource:    " + dataSource);
    System.out.println("Connection:    " + dbConn);
    return ret;      
  }
  
  private boolean connectConn() {
    try {
      String user = dbProp.getProperty(dbKeyUser);
      String passwd = dbProp.getProperty(dbKeyPassword);
      String connString = dbProp.getProperty(dbKeyConnString);
      String driver = dbProp.getProperty(dbKeyDriver);
      // ---------------------------------
//      Class.forName(driver).newInstance();
      Class.forName(driver).getDeclaredConstructor().newInstance();
      dbConn = DriverManager.getConnection(connString, user, passwd);
      // ---------------------------------
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }

  private boolean connectBDS() {
    try {
      String user = dbProp.getProperty(dbKeyUser);
      String passwd = dbProp.getProperty(dbKeyPassword);
      String connString = dbProp.getProperty(dbKeyConnString);
      String driver = dbProp.getProperty(dbKeyDriver);
      // ---------------------------------
      BasicDataSource bds = new BasicDataSource();
      bds.setDriverClassName(driver);
      bds.setUrl(connString);
      bds.setUsername(user);
      bds.setPassword(passwd);
      bds.setMaxTotal(MAX_TOTAl);
      // ----------------
      dataSource = (DataSource) bds;
      // ---------------------------------
      Connection conn = dataSource.getConnection();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }
  
  private boolean connectSPDS() {
    try {
      String user = dbProp.getProperty(dbKeyUser);
      String passwd = dbProp.getProperty(dbKeyPassword);
      String connString = dbProp.getProperty(dbKeyConnString);
      String driver = dbProp.getProperty(dbKeyDriver);
      // ---------------------------------
      DriverAdapterCPDS adapter = new DriverAdapterCPDS();
      adapter.setDriver(driver);
      adapter.setUrl(connString);
      adapter.setUser(user);
      adapter.setPassword(passwd);
      // ----------------
      SharedPoolDataSource spds = new SharedPoolDataSource();
      spds.setConnectionPoolDataSource(adapter);
      spds.setMaxTotal(MAX_TOTAl);
      // ---------------------------------
      dataSource = (DataSource) spds;
      // ---------------------------------
      Connection conn = dataSource.getConnection();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }
  
  @Override
  public boolean connectDefault() {
    String dbIndex = null;
    String dbName = null;
    // ----------------------
    String[] list = getFileList();
    if (list.length == 0) {
      return false;
    }
    List<String> arrList = Arrays.asList(list);
    int ind = arrList.indexOf(dbDefaultName);
    if (ind >= 0) {
      dbIndex = String.valueOf(ind);
      dbName = dbDefaultName;
    } else {
      // return false;
      logger.info("Конфигурация " + dbDefaultName + " не найдена. Выбирается первая из списка.");
      dbIndex = "0";
      dbName = list[0];
    }
    // ----------------------
    Properties props = new Properties();
    props.setProperty(dbKeyIndex, dbIndex);
    props.setProperty(dbKeyName, dbName);
    // ----------------------
    readCfg(props);  // user, password
    setDbProp(props);
    // ----------------------
    boolean res = connect();
    // ----------------------
    return res;
  }

  @Override
  public boolean reconnect() { 
    disconnect();
    boolean res = connect();
    return res;
  }

  @Override
  public void disconnect() {
    if (Main.dbcpMode == DbcpMode.SINGLE) {
      disconnectConn();
    } else {
      disconnectDS();
    }
  }

  private void disconnectConn() {
    if (dbConn == null) {
      return;
    }
    System.out.println("disconnect:   " + dbConn);
    try {
      dbConn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    dbConn = null;
  }
  
  private void disconnectDS() {
    if (dataSource == null) {
      return;
    }
    System.out.println("disconnect: " + dataSource);
    dataSource = null;
  }

  @Override
  public void readCfg(Properties props) {
    // ---------------------------
    String fullFileName = dbPath;
    if (dbPath != "") {
      fullFileName += "/";
    }
    fullFileName += props.getProperty(dbKeyName);
    props.setProperty(dbKeyFullName, fullFileName);
    // System.out.println("Файл конфигурации: " + fullFileName);
    // ---------------------------
    Properties fileProps = new Properties();
    try {
      // InputStream fis = new FileInputStream(fullFileName);
      InputStream fis = getClass().getResourceAsStream(fullFileName);
      fileProps.loadFromXML(fis);
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    // ---------------------------
    props.setProperty(dbKeyUser, fileProps.getProperty(dbKeyUser));
    props.setProperty(dbKeyPassword, fileProps.getProperty(dbKeyPassword));
    props.setProperty(dbKeyConnString, fileProps.getProperty(dbKeyConnString));
    props.setProperty(dbKeyDriver, fileProps.getProperty(dbKeyDriver));
    // ---------------------------
    printProps(props);
  }

  private void printProps(Properties props) {
    System.out.println("");

    System.out.println("Configuration file = " + props.getProperty(dbKeyFullName));
    System.out.println(dbKeyConnString + " = " + props.getProperty(dbKeyConnString));
    System.out.println(dbKeyDriver + " = " + props.getProperty(dbKeyDriver));
    System.out.println(dbKeyUser + " = " + props.getProperty(dbKeyUser));
    // System.out.println(dbKeyPassword + " = " + props.getProperty(dbKeyPassword));
    System.out.println(dbKeyPassword + " = " + "****");

    System.out.println("");
  }

//------------------------------------------------------
// Connection  
//------------------------------------------------------

  @Override
  public Connection getConnection() {
    Connection conn = null;
    if (Main.dbcpMode == DbcpMode.SINGLE) {
      conn = dbConn;
    } else {
      conn = getConnectionDS(); 
    }
    System.out.println("getConnection: " + conn);
    return conn;
  }

  private Connection getConnectionDS() {
    Connection conn = null;
    try {
      conn = dataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return conn;
  }
  
  @Override
  public void close(Connection conn) {
    if (Main.dbcpMode != DbcpMode.SINGLE) {
      System.out.println("close: " + conn);
      closeDS(conn); 
    } else {
      System.out.println("close: (not close)");
    }
    System.out.println();
  }
  
  private void closeDS(Connection conn) {
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
  }
  
}
