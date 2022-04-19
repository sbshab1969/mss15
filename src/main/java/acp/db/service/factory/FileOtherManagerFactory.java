package acp.db.service.factory;

import acp.Main;
import acp.db.service.IManagerList;
import acp.db.service.impl.dbcp.FileOtherManagerListDbcp;
import acp.db.service.impl.dbutil.FileOtherManagerListDbutil;
import acp.db.service.impl.hiber.crit.FileOtherManagerListCrit;
import acp.db.service.impl.hiber.critjpa.FileOtherManagerListCritJpa;
import acp.db.service.impl.hiber.hql.FileOtherManagerListHql;
import acp.db.service.impl.hiber.hqljpa.FileOtherManagerListHqlJpa;
import acp.db.service.impl.hiber.sql.FileOtherManagerListSql;
import acp.db.service.impl.hiber.sqljpa.FileOtherManagerListSqlJpa;
import acp.db.service.impl.jdbc.FileOtherManagerListJdbc;
import acp.forms.dto.FileOtherDto;

public class FileOtherManagerFactory {

  public static IManagerList<FileOtherDto> getManagerList(Long file_id) {
    switch (Main.dbWork) {
    case JDBC:
      return new FileOtherManagerListJdbc(file_id);
    case DBCP:
      return new FileOtherManagerListDbcp(file_id);
    case DBUTIL:
      return new FileOtherManagerListDbutil(file_id);
    case HB_HQL:
      return new FileOtherManagerListHql(file_id);
    case HB_HQL_JPA:
      return new FileOtherManagerListHqlJpa(file_id);
    case HB_SQL:
      return new FileOtherManagerListSql(file_id);
    case HB_SQL_JPA:
      return new FileOtherManagerListSqlJpa(file_id);
    case HB_CRIT:
      return new FileOtherManagerListCrit(file_id);
    case HB_CRIT_JPA:
      return new FileOtherManagerListCritJpa(file_id);
    }
    return null;
  }

}
