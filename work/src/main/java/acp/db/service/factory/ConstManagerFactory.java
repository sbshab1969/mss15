package acp.db.service.factory;

import acp.Main;
import acp.db.service.IConstManagerEdit;
import acp.db.service.IManagerList;
import acp.db.service.impl.hiber.crit.ConstManagerEditCrit;
import acp.db.service.impl.hiber.crit.ConstManagerListCrit;
import acp.db.service.impl.hiber.critjpa.ConstManagerEditCritJpa;
import acp.db.service.impl.hiber.critjpa.ConstManagerListCritJpa;
import acp.db.service.impl.hiber.hql.ConstManagerEditHql;
import acp.db.service.impl.hiber.hql.ConstManagerListHql;
import acp.db.service.impl.hiber.hqljpa.ConstManagerEditHqlJpa;
import acp.db.service.impl.hiber.hqljpa.ConstManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.ConstManagerEditSql;
import acp.db.service.impl.hiber.sql.ConstManagerListSql;
import acp.db.service.impl.hiber.sqljpa.ConstManagerEditSqlJpa;
import acp.db.service.impl.hiber.sqljpa.ConstManagerListSqlJpa;
import acp.db.service.impl.jdbc.ConstManagerEditJdbc;
import acp.db.service.impl.jdbc.ConstManagerListJdbc;
import acp.forms.dto.ConstDto;

public class ConstManagerFactory {

  public static IManagerList<ConstDto> getManagerList() {
    switch (Main.dbWork) {
    case JDBC:
      return new ConstManagerListJdbc();
    case HB_HQL:
      return new ConstManagerListHql();
    case HB_HQL_JPA:
      return new ConstManagerListHqlJpa();
    case HB_SQL:
      return new ConstManagerListSql();
    case HB_SQL_JPA:
      return new ConstManagerListSqlJpa();
    case HB_CRIT:
      return new ConstManagerListCrit();
    case HB_CRIT_JPA:
      return new ConstManagerListCritJpa();
    }
    return null;
  }

  public static IConstManagerEdit getManagerEdit() {
    switch (Main.dbWork) {
    case JDBC:
      return new ConstManagerEditJdbc();
    case HB_HQL:
      return new ConstManagerEditHql();
    case HB_HQL_JPA:
      return new ConstManagerEditHqlJpa();
    case HB_SQL:
      return new ConstManagerEditSql();
    case HB_SQL_JPA:
      return new ConstManagerEditSqlJpa();
    case HB_CRIT:
      return new ConstManagerEditCrit();
    case HB_CRIT_JPA:
      return new ConstManagerEditCritJpa();
    }
    return null;
  }

}
