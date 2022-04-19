package acp.db.service.factory;

import acp.db.service.IVarManagerEdit;
import acp.db.service.impl.hiber.crit.VarManagerEditCrit;
import acp.db.service.impl.hiber.crit.VarManagerListCrit;
import acp.db.service.impl.hiber.critjpa.VarManagerEditCritJpa;
import acp.db.service.impl.hiber.critjpa.VarManagerListCritJpa;
import acp.db.service.impl.hiber.hql.VarManagerEditHql;
import acp.db.service.impl.hiber.hql.VarManagerListHql;
import acp.db.service.impl.hiber.hqljpa.VarManagerEditHqlJpa;
import acp.db.service.impl.hiber.hqljpa.VarManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.VarManagerEditSql;
import acp.db.service.impl.hiber.sql.VarManagerListSql;
import acp.db.service.impl.hiber.sqljpa.VarManagerEditSqlJpa;
import acp.db.service.impl.hiber.sqljpa.VarManagerListSqlJpa;
import acp.db.service.impl.jdbc.VarManagerEditJdbc;
import acp.db.service.impl.jdbc.VarManagerListJdbc;
import acp.Main;
import acp.db.service.IManagerList;
import acp.forms.dto.VarDto;

public class VarManagerFactory {

  public static IManagerList<VarDto> getManagerList() {
    switch (Main.dbWork) {
    case JDBC:
      return new VarManagerListJdbc();
    case HB_HQL:
      return new VarManagerListHql();
    case HB_HQL_JPA:
      return new VarManagerListHqlJpa();
    case HB_SQL:
      return new VarManagerListSql();
    case HB_SQL_JPA:
      return new VarManagerListSqlJpa();
    case HB_CRIT:
      return new VarManagerListCrit();
    case HB_CRIT_JPA:
      return new VarManagerListCritJpa();
    }
    return null;
  }

  public static IVarManagerEdit getManagerEdit() {
    switch (Main.dbWork) {
    case JDBC:
      return new VarManagerEditJdbc();
    case HB_HQL:
      return new VarManagerEditHql();
    case HB_HQL_JPA:
      return new VarManagerEditHqlJpa();
    case HB_SQL:
      return new VarManagerEditSql();
    case HB_SQL_JPA:
      return new VarManagerEditSqlJpa();
    case HB_CRIT:
      return new VarManagerEditCrit();
    case HB_CRIT_JPA:
      return new VarManagerEditCritJpa();
    }
    return null;
  }

}
