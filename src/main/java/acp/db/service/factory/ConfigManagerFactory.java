package acp.db.service.factory;

import acp.Main;
import acp.db.service.IConfigManagerEdit;
import acp.db.service.IManagerList;
import acp.db.service.impl.dbcp.ConfigManagerEditDbcp;
import acp.db.service.impl.dbcp.ConfigManagerListDbcp;
import acp.db.service.impl.dbutil.ConfigManagerEditDbutil;
import acp.db.service.impl.dbutil.ConfigManagerListDbutil;
import acp.db.service.impl.hiber.crit.ConfigManagerEditCrit;
import acp.db.service.impl.hiber.crit.ConfigManagerListCrit;
import acp.db.service.impl.hiber.critjpa.ConfigManagerEditCritJpa;
import acp.db.service.impl.hiber.critjpa.ConfigManagerListCritJpa;
import acp.db.service.impl.hiber.hql.ConfigManagerEditHql;
import acp.db.service.impl.hiber.hql.ConfigManagerListHql;
import acp.db.service.impl.hiber.hqljpa.ConfigManagerEditHqlJpa;
import acp.db.service.impl.hiber.hqljpa.ConfigManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.ConfigManagerEditSql;
import acp.db.service.impl.hiber.sql.ConfigManagerListSql;
import acp.db.service.impl.hiber.sqljpa.ConfigManagerEditSqlJpa;
import acp.db.service.impl.hiber.sqljpa.ConfigManagerListSqlJpa;
import acp.db.service.impl.jdbc.ConfigManagerEditJdbc;
import acp.db.service.impl.jdbc.ConfigManagerListJdbc;
import acp.forms.dto.ConfigDto;

public class ConfigManagerFactory {

  public static IManagerList<ConfigDto> getManagerList() {
    switch (Main.dbWork) {
    case JDBC:
      return new ConfigManagerListJdbc();
    case DBCP:
      return new ConfigManagerListDbcp();
    case DBUTIL:
      return new ConfigManagerListDbutil();
    case HB_HQL:
      return new ConfigManagerListHql();
    case HB_HQL_JPA:
      return new ConfigManagerListHqlJpa();
    case HB_SQL:
      return new ConfigManagerListSql();
    case HB_SQL_JPA:
      return new ConfigManagerListSqlJpa();
    case HB_CRIT:
      return new ConfigManagerListCrit();
    case HB_CRIT_JPA:
      return new ConfigManagerListCritJpa();
    }
    return null;
  }

  public static IConfigManagerEdit getManagerEdit() {
    switch (Main.dbWork) {
    case JDBC:
      return new ConfigManagerEditJdbc();
    case DBCP:
      return new ConfigManagerEditDbcp();
    case DBUTIL:
      return new ConfigManagerEditDbutil();
    case HB_HQL:
      return new ConfigManagerEditHql();
    case HB_HQL_JPA:
      return new ConfigManagerEditHqlJpa();
    case HB_SQL:
      return new ConfigManagerEditSql();
    case HB_SQL_JPA:
      return new ConfigManagerEditSqlJpa();
    case HB_CRIT:
      return new ConfigManagerEditCrit();
    case HB_CRIT_JPA:
      return new ConfigManagerEditCritJpa();
    }
    return null;
  }

}
