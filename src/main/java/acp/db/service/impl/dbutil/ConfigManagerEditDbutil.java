package acp.db.service.impl.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IConfigManagerEdit;
import acp.db.service.impl.dbutil.all.ManagerBaseDbutil;
import acp.db.service.impl.dbutil.all.ManagerUtilDbutil;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerEditDbutil extends ManagerBaseDbutil implements IConfigManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerEditDbutil.class);

  private ManagerUtilDbutil mngUtil = new ManagerUtilDbutil();

  @Override
  public ConfigDto select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msso_id,msso_name,msso_dt_begin,msso_dt_end,msso_comment,msso_msss_id");
    sbQuery.append("  from mss_options");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("select (edit): " + strQuery);
    // ------------------------------------------------------
    ConfigDto configObj = null;
    try {
      Connection conn = dbConnect.getConnection();
      ResultSetHandler<Object[]> handler = new ArrayHandler();
      // --------------------------------------------------------------
      Object[] result = queryRunner.query(conn,strQuery,handler,objId);
      // --------------------------------------------------------------
      configObj = getObject(result);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      configObj = null;
    }
    // ------------------------------------------------------
    return configObj;
  }
  
  private ConfigDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = ((BigDecimal) obj[0]).longValue();
    String rsName = (String) obj[1];
    Timestamp rsDateBegin = (Timestamp) obj[2];
    Timestamp rsDateEnd = (Timestamp) obj[3];
    String rsComment = (String) obj[4];
    Long rsSourceId = ((BigDecimal) obj[5]).longValue();
    //---------------------------------------
    ConfigDto objDto = new ConfigDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    objDto.setComment(rsComment);
    objDto.setSourceId(rsSourceId);
    //---------------------------------------
    return objDto;
  }

  @Override
  public String getCfgName(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msso_name from mss_options t where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("selectCfgName: " + strQuery);
    // ------------------------------------------------------
    String configName = "";
    try {
      Connection conn = dbConnect.getConnection();
      ScalarHandler<String> scalarHandler = new ScalarHandler<>("msso_name");
      // --------------------------------------------------------------
      configName = queryRunner.query(conn, strQuery, scalarHandler,objId);
      // --------------------------------------------------------------
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      configName = "";
    }
    // ------------------------------------------------------
    return configName;
  }

  @Override
  public String getCfgStr(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.msso_config.getStringVal() msso_conf");
    sbQuery.append("  from mss_options t where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("selectCfgStr: " + strQuery);
    // ------------------------------------------------------
    String configStr = null;
    try {
      Connection conn = dbConnect.getConnection();
      ScalarHandler<String> scalarHandler = new ScalarHandler<>("msso_conf");
      // --------------------------------------------------------------
      configStr = queryRunner.query(conn, strQuery, scalarHandler,objId);
      // --------------------------------------------------------------
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      configStr = null;
    }
    // ------------------------------------------------------
    return configStr;
  }

  @Override
  public Long insert(ConfigDto newObj) {
    // ------------------------------------------------------
    Long objId = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options (");
    sbQuery.append("msso_id, msso_name, msso_config");
    sbQuery.append(",msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(",msso_dt_create, msso_dt_modify, msso_owner, msso_msss_id)");
    sbQuery.append(" values (?, ?, XMLType(?), ?, ?, ?");
    sbQuery.append(", sysdate, sysdate, user, ?)");
    String strQuery = sbQuery.toString();
    System.out.println("insert: " + strQuery);
    // ------------------------------------------------------
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    Timestamp tsBegin = DateUtils.util2ts(newObj.getDateBegin());
    Timestamp tsEnd = DateUtils.util2ts(newObj.getDateEnd());
    try {
      Connection conn = dbConnect.getConnection();
      objId = mngUtil.getValueL("select msso_seq.nextval from dual", conn);
      // --------------------------------
      Object[] sqlParams = new Object[7];  
      sqlParams[0] = objId;
      sqlParams[1] = newObj.getName();
      sqlParams[2] = emptyXml;
      sqlParams[3] = tsBegin;
      sqlParams[4] = tsEnd;
      sqlParams[5] = newObj.getComment();
      sqlParams[6] = newObj.getSourceId();
      System.out.println("params: " + Arrays.toString(sqlParams));
      // --------------------------------
      queryRunner.update(conn, strQuery, sqlParams);
      // --------------------------------
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      objId = null;
    }
    // -----------------------------------------------------
    return objId;
  }

  @Override
  public boolean update(ConfigDto newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    sbQuery.append("   set msso_name=?");
    sbQuery.append("      ,msso_dt_begin=?");
    sbQuery.append("      ,msso_dt_end=?");
    sbQuery.append("      ,msso_comment=?");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append("      ,msso_msss_id=?");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("update: " + strQuery);
    // -----------------------------------------
    Timestamp tsBegin = DateUtils.util2ts(newObj.getDateBegin());
    Timestamp tsEnd = DateUtils.util2ts(newObj.getDateEnd());
    try {
      Connection conn = dbConnect.getConnection();
      // --------------------------------
      Object[] sqlParams = new Object[6];  
      sqlParams[0] = newObj.getName();
      sqlParams[1] = tsBegin;
      sqlParams[2] = tsEnd;
      sqlParams[3] = newObj.getComment();
      sqlParams[4] = newObj.getSourceId();
      sqlParams[5] = newObj.getId();
      System.out.println("params: " + Arrays.toString(sqlParams));
      // --------------------------------
      queryRunner.update(conn, strQuery, sqlParams);
      // --------------------------------
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

  @Override
  public boolean updateCfgStr(Long objId, String txtConf) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    // sbQuery.append("   set msso_config=?"); // OK
    sbQuery.append("   set msso_config=XMLType(?)");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append(" where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("updateCfg: " + strQuery);
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      // --------------------------------
      queryRunner.update(conn, strQuery, txtConf, objId);
      // --------------------------------
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

  @Override
  public boolean delete(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("delete from mss_options where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("delete: " + strQuery);
    // -----------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      // --------------------------------
      queryRunner.update(conn, strQuery, objId);
      // --------------------------------
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

  @Override
  public boolean copy(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery.append(" (select msso_seq.nextval, msso_name || '_copy'");
    sbQuery.append(", msso_config");
    sbQuery.append(", msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(", sysdate, sysdate, user, msso_msss_id");
    sbQuery.append(" from mss_options where msso_id=?)");
    String strQuery = sbQuery.toString();
    System.out.println("copy: " + strQuery);
    // -----------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      // --------------------------------
      queryRunner.update(conn, strQuery, objId);
      // --------------------------------
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

}
