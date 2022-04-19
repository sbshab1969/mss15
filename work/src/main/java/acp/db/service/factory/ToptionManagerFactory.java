package acp.db.service.factory;

import java.util.ArrayList;

import acp.Main;
import acp.db.service.IToptionManagerEdit;
import acp.db.service.IToptionManagerList;
import acp.db.service.impl.hiber.all.ToptionManagerEditSql;
import acp.db.service.impl.hiber.all.ToptionManagerEditSqlJpa;
import acp.db.service.impl.hiber.all.ToptionManagerListSql;
import acp.db.service.impl.hiber.all.ToptionManagerListSqlJpa;
import acp.db.service.impl.jdbc.all.ToptionManagerEditJdbc;
import acp.db.service.impl.jdbc.all.ToptionManagerListJdbc;

public class ToptionManagerFactory {

  public static IToptionManagerList getManagerList(String path, ArrayList<String> attrs) {
    switch (Main.dbWork) {
    case JDBC:
      return new ToptionManagerListJdbc(path, attrs);
    case HB_HQL:
      return new ToptionManagerListSql(path, attrs);
    case HB_HQL_JPA:
      return new ToptionManagerListSqlJpa(path, attrs);
    case HB_SQL:
      return new ToptionManagerListSql(path, attrs);
    case HB_SQL_JPA:
      return new ToptionManagerListSqlJpa(path, attrs);
    case HB_CRIT_JPA:
      return new ToptionManagerListSqlJpa(path, attrs);
    case HB_CRIT:
      return new ToptionManagerListSqlJpa(path, attrs);
    }
    return null;
  }

  public static IToptionManagerEdit getManagerEdit(String path, ArrayList<String> attrs) {
    switch (Main.dbWork) {
    case JDBC:
      return new ToptionManagerEditJdbc(path, attrs);
    case HB_HQL:
      return new ToptionManagerEditSql(path, attrs);
    case HB_HQL_JPA:
      return new ToptionManagerEditSqlJpa(path, attrs);
    case HB_SQL:
      return new ToptionManagerEditSql(path, attrs);
    case HB_SQL_JPA:
      return new ToptionManagerEditSqlJpa(path, attrs);
    case HB_CRIT_JPA:
      return new ToptionManagerEditSqlJpa(path, attrs);
    case HB_CRIT:
      return new ToptionManagerEditSqlJpa(path, attrs);
    }
    return null;
  }

}
