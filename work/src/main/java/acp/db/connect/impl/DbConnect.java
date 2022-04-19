package acp.db.connect.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.connect.IDbConnect;

public abstract class DbConnect implements IDbConnect {
  private static Logger logger = LoggerFactory.getLogger(DbConnect.class);
  
  protected String dbPath;
  protected String dbExt;
  protected String dbDefaultName;

  protected String dbKeyIndex;
  protected String dbKeyName;
  protected String dbKeyFullName;

  protected String dbKeyUser;
  protected String dbKeyPassword;
  protected String dbKeyConnString;
  protected String dbKeyDriver;

  protected Properties dbProp;

  @Override
  public String getDbPath() {
    return dbPath;
  }

  @Override
  public String getDbExt() {
    return dbExt;
  }

  @Override
  public String getDbKeyIndex() {
    return dbKeyIndex;
  }

  @Override
  public String getDbKeyName() {
    return dbKeyName;
  }

  @Override
  public String getDbKeyFullName() {
    return dbKeyFullName;
  }

  @Override
  public String getDbKeyUser() {
    return dbKeyUser;
  }

  @Override
  public String getDbKeyPassword() {
    return dbKeyPassword;
  }

  @Override
  public String getDbKeyConnString() {
    return dbKeyConnString;
  }

  @Override
  public String getDbKeyDriver() {
    return dbKeyDriver;
  }

  @Override
  public Properties getDbProp() {
    return dbProp;
  }

  @Override
  public void setDbProp(Properties props) {
    dbProp = props;
  }

  @Override
  public String[] getFileList() {
    String[] list = {};
    URL url = getClass().getResource(dbPath);
    if (url == null) {
      return list;
    }
    String urlProtocol = url.getProtocol();
    System.out.println("getFileList:");
    System.out.println("url: " + url);
    System.out.println("urlProtocol: " + urlProtocol);
    
    if (urlProtocol.equalsIgnoreCase("jar")) {
      list = fileListJar(url,dbExt);
    } else {
      list = fileListFile(url,dbExt);
    }
    // System.out.println(Arrays.deepToString(list));
    Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
    return list;
  }

  public String[] fileListFile(URL url, String ext) {
    URI uri = null;
    try {
      uri = url.toURI();
    } catch (URISyntaxException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    // ------------
    String[] list = {};
    if (uri != null) {
      File path = new File(uri);
      // list = path.list();
      list = path.list(filter(".*\\." + ext));
    }
    // ------------
    return list;
  }

  private FilenameFilter filter(final String regex) {
    return new FilenameFilter() {
      private Pattern pattern = Pattern.compile(regex);

      public boolean accept(File dir, String name) {
        return pattern.matcher(name).matches();
      }
    };
  }

  public String[] fileListJar(URL url, String ext) {
    String[] list = {};
    List<String> arrList = new ArrayList<>();
    JarURLConnection conn;
    try {
      conn = (JarURLConnection) url.openConnection();
      Enumeration<JarEntry> en = conn.getJarFile().entries();
      String mainEntryName = conn.getEntryName();
      while (en.hasMoreElements()) {
        JarEntry entry = en.nextElement();
        String entryName = entry.getName();
        if (entryName.startsWith(mainEntryName)) {
          if (!entry.isDirectory()) {
            String filter = "." + ext;
            if (entryName.endsWith(filter)) {
              int pos = entryName.lastIndexOf("/");
              String entryName2 = entryName.substring(pos+1, entryName.length());
              arrList.add(entryName2);
            }  
          }
        }
      }
      list = new String[arrList.size()];
      arrList.toArray(list);
    } catch (IOException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return list;
  }
  
}
