package acp.db.service.factory;

import acp.Main;
import acp.db.service.IFileLoadManagerEdit;
import acp.db.service.IManagerList;
import acp.db.service.impl.dbcp.FileLoadManagerEditDbcp;
import acp.db.service.impl.dbcp.FileLoadManagerListDbcp;
import acp.db.service.impl.dbutil.FileLoadManagerEditDbutil;
import acp.db.service.impl.dbutil.FileLoadManagerListDbutil;
import acp.db.service.impl.hiber.crit.FileLoadManagerEditCrit;
import acp.db.service.impl.hiber.crit.FileLoadManagerListCrit;
import acp.db.service.impl.hiber.critjpa.FileLoadManagerEditCritJpa;
import acp.db.service.impl.hiber.critjpa.FileLoadManagerListCritJpa;
import acp.db.service.impl.hiber.hql.FileLoadManagerEditHql;
import acp.db.service.impl.hiber.hql.FileLoadManagerListHql;
import acp.db.service.impl.hiber.hqljpa.FileLoadManagerEditHqlJpa;
import acp.db.service.impl.hiber.hqljpa.FileLoadManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.FileLoadManagerEditSql;
import acp.db.service.impl.hiber.sql.FileLoadManagerListSql;
import acp.db.service.impl.hiber.sqljpa.FileLoadManagerEditSqlJpa;
import acp.db.service.impl.hiber.sqljpa.FileLoadManagerListSqlJpa;
import acp.db.service.impl.jdbc.FileLoadManagerEditJdbc;
import acp.db.service.impl.jdbc.FileLoadManagerListJdbc;
import acp.forms.dto.FileLoadDto;

public class FileLoadManagerFactory {

  public static IManagerList<FileLoadDto> getManagerList() {
    switch (Main.dbWork) {
    case JDBC:
      return new FileLoadManagerListJdbc();
    case DBCP:
      return new FileLoadManagerListDbcp();
    case DBUTIL:
      return new FileLoadManagerListDbutil();
    case HB_HQL:
      return new FileLoadManagerListHql();
    case HB_HQL_JPA:
      return new FileLoadManagerListHqlJpa();
    case HB_SQL:
      return new FileLoadManagerListSql();
    case HB_SQL_JPA:
      return new FileLoadManagerListSqlJpa();
    case HB_CRIT:
      return new FileLoadManagerListCrit();
    case HB_CRIT_JPA:
      return new FileLoadManagerListCritJpa();
    }
    return null;
  }

  public static IFileLoadManagerEdit getManagerEdit() {
    switch (Main.dbWork) {
    case JDBC:
      return new FileLoadManagerEditJdbc();
    case DBCP:
      return new FileLoadManagerEditDbcp();
    case DBUTIL:
      return new FileLoadManagerEditDbutil();
    case HB_HQL:
      return new FileLoadManagerEditHql();
    case HB_HQL_JPA:
      return new FileLoadManagerEditHqlJpa();
    case HB_SQL:
      return new FileLoadManagerEditSql();
    case HB_SQL_JPA:
      return new FileLoadManagerEditSqlJpa();
    case HB_CRIT:
      return new FileLoadManagerEditCrit();
    case HB_CRIT_JPA:
      return new FileLoadManagerEditCritJpa();
    }
    return null;
  }

}
