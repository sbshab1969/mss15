package acp.db.service.factory;

import acp.Main;
import acp.db.service.IManagerList;
import acp.db.service.ISourceManagerEdit;
import acp.db.service.impl.dbcp.SourceManagerEditDbcp;
import acp.db.service.impl.dbcp.SourceManagerListDbcp;
import acp.db.service.impl.dbutil.SourceManagerEditDbutil;
import acp.db.service.impl.dbutil.SourceManagerListDbutil;
import acp.db.service.impl.hiber.crit.SourceManagerEditCrit;
import acp.db.service.impl.hiber.crit.SourceManagerListCrit;
import acp.db.service.impl.hiber.critjpa.SourceManagerEditCritJpa;
import acp.db.service.impl.hiber.critjpa.SourceManagerListCritJpa;
import acp.db.service.impl.hiber.hql.SourceManagerEditHql;
import acp.db.service.impl.hiber.hql.SourceManagerListHql;
import acp.db.service.impl.hiber.hqljpa.SourceManagerEditHqlJpa;
import acp.db.service.impl.hiber.hqljpa.SourceManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.SourceManagerEditSql;
import acp.db.service.impl.hiber.sql.SourceManagerListSql;
import acp.db.service.impl.hiber.sqljpa.SourceManagerEditSqlJpa;
import acp.db.service.impl.hiber.sqljpa.SourceManagerListSqlJpa;
import acp.db.service.impl.jdbc.SourceManagerEditJdbc;
import acp.db.service.impl.jdbc.SourceManagerListJdbc;
import acp.forms.dto.SourceDto;

public class SourceManagerFactory {

  public static IManagerList<SourceDto> getManagerList() {
    switch (Main.dbWork) {
    case JDBC:
      return new SourceManagerListJdbc();
    case DBCP:
      return new SourceManagerListDbcp();
    case DBUTIL:
      return new SourceManagerListDbutil();
    case HB_HQL:
      return new SourceManagerListHql();
    case HB_HQL_JPA:
      return new SourceManagerListHqlJpa();
    case HB_SQL:
      return new SourceManagerListSql();
    case HB_SQL_JPA:
      return new SourceManagerListSqlJpa();
    case HB_CRIT:
      return new SourceManagerListCrit();
    case HB_CRIT_JPA:
      return new SourceManagerListCritJpa();
    }
    return null;
  }

  public static ISourceManagerEdit getManagerEdit() {
    switch (Main.dbWork) {
    case JDBC:
      return new SourceManagerEditJdbc();
    case DBCP:
      return new SourceManagerEditDbcp();
    case DBUTIL:
      return new SourceManagerEditDbutil();
    case HB_HQL:
      return new SourceManagerEditHql();
    case HB_HQL_JPA:
      return new SourceManagerEditHqlJpa();
    case HB_SQL:
      return new SourceManagerEditSql();
    case HB_SQL_JPA:
      return new SourceManagerEditSqlJpa();
    case HB_CRIT:
      return new SourceManagerEditCrit();
    case HB_CRIT_JPA:
      return new SourceManagerEditCritJpa();
    }
    return null;
  }

}
