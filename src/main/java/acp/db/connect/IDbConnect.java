package acp.db.connect;

import java.util.Properties;

public interface IDbConnect {

  public boolean testConnect();
  public boolean connect();
  public boolean connectDefault();
  public void disconnect();
  public boolean reconnect();

  public String getDbPath();
  public String getDbExt();

  public String getDbKeyIndex();
  public String getDbKeyName();
  public String getDbKeyFullName();

  public String getDbKeyUser();
  public String getDbKeyPassword();
  public String getDbKeyConnString();
  public String getDbKeyDriver();

  public Properties getDbProp();
  public void setDbProp(Properties props);
  
  public String[] getFileList();
  
  public void readCfg(Properties props);
}