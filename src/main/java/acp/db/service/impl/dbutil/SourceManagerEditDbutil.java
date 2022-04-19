package acp.db.service.impl.dbutil;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.ISourceManagerEdit;
import acp.db.service.impl.dbutil.all.ManagerBaseDbutil;
import acp.db.service.impl.dbutil.all.ManagerUtilDbutil;
import acp.forms.dto.SourceDto;
import acp.utils.*;

public class SourceManagerEditDbutil extends ManagerBaseDbutil implements ISourceManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(SourceManagerEditDbutil.class);

  private ManagerUtilDbutil mngUtil = new ManagerUtilDbutil();

  @Override
  public SourceDto select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msss_id, msss_name");
    sbQuery.append("  from mss_source");
    sbQuery.append(" where msss_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("select (edit): " + strQuery);
    // ------------------------------------------------------
    Map<String,String> colToProp = new HashMap<>();
    colToProp.put("MSSS_ID", "id");
    colToProp.put("MSSS_NAME", "name");
    colToProp.put("MSSS_OWNER", "owner");
    // ----------------------------------
    BeanProcessor beanProc = new BeanProcessor(colToProp);
    RowProcessor rowProc = new BasicRowProcessor(beanProc);
    ResultSetHandler<SourceDto> handler = new BeanHandler<>(SourceDto.class, rowProc);
    // ----------------------------------
    SourceDto sourceObj = null;
    try {
      Connection conn = dbConnect.getConnection();
      sourceObj = queryRunner.query(conn,strQuery,handler,objId);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      sourceObj = null;
    }
    // ------------------------------------------------------
    return sourceObj;
  }

  @Override
  public Long insert(SourceDto newObj) {
    // ------------------------------------------------------
    Long objId = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_source");
    sbQuery.append(" (msss_id, msss_name, msss_dt_create, msss_dt_modify, msss_owner)");
    sbQuery.append(" values (?, ?, sysdate, sysdate, user)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      objId = mngUtil.getValueL("select msss_seq.nextval from dual", conn);
      queryRunner.update(conn, strQuery, objId, newObj.getName());
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      objId = null;
    }
    // -----------------------------------------------------
    return objId;
  }

  @Override
  public boolean update(SourceDto newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_source");
    sbQuery.append("   set msss_name=?");
    sbQuery.append("      ,msss_dt_modify=sysdate");
    sbQuery.append("      ,msss_owner=user");
    sbQuery.append(" where msss_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      queryRunner.update(conn, strQuery, newObj.getName(), newObj.getId());
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
    sbQuery.append("delete from mss_source where msss_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      queryRunner.update(conn, strQuery, objId);
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

  private List<Object[]> getListObject(String strQuery) {
    List<Object[]> objList =  null;
    // --------------------------------------------
    ResultSetHandler<List<Object[]>> handler = new ArrayListHandler();
    try {
      Connection conn = dbConnect.getConnection();
      objList = queryRunner.query(conn,strQuery,handler);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
    }
    // -------------------------------------------
    return objList;
  }

  private List<String[]> getListStringByQuery(String strQuery) {
    List<Object[]> objList =  getListObject(strQuery);
    List<String[]> arrayString = QueryUtils.getListString(objList);
    return arrayString;
  }

  @Override
  public List<String[]> getSources() {
    String strQuery = "select msss_id, msss_name from mss_source order by msss_name";
    List<String[]> arrayString = getListStringByQuery(strQuery);
    return arrayString;
  }

}
