package acp.db.service.impl.dbutil;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IConstManagerEdit;
import acp.db.service.impl.dbutil.all.ManagerBaseDbutil;
import acp.db.service.impl.dbutil.all.ManagerUtilDbutil;
import acp.forms.dto.ConstDto;
import acp.utils.*;

public class ConstManagerEditDbutil extends ManagerBaseDbutil implements IConstManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerEditDbutil.class);

  private ManagerUtilDbutil mngUtil = new ManagerUtilDbutil();

  @Override
  public ConstDto select(Long objId) {
    ConstDto constObj = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select mssc_id, mssc_name, mssc_value");
    sbQuery.append("  from mss_const");
    sbQuery.append(" where mssc_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("select (edit): " + strQuery);
    // ------------------------------------------------------
    Map<String,String> colToProp = new HashMap<>();
    colToProp.put("MSSC_ID", "id");
    colToProp.put("MSSC_NAME", "name");
    colToProp.put("MSSC_VALUE", "value");
    // ----------------------------------
    BeanProcessor beanProc = new BeanProcessor(colToProp);
    RowProcessor rowProc = new BasicRowProcessor(beanProc);
    ResultSetHandler<ConstDto> handler = new BeanHandler<>(ConstDto.class, rowProc);
    // ----------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      constObj = queryRunner.query(conn,strQuery,handler,objId);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      constObj = null;
    }
    // ------------------------------------------------------
    return constObj;
  }

  @Override
  public Long insert(ConstDto newObj) {
    Long objId = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_const");
    sbQuery.append(" (mssc_id, mssc_name, mssc_value)");
    sbQuery.append(" values (?, upper(?), ?)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      objId = mngUtil.getValueL("select mssc_seq.nextval from dual", conn);
//      int cnt = queryRunner.update(conn, strQuery, objId, newObj.getName(), newObj.getValue());
      queryRunner.update(conn, strQuery, objId, newObj.getName(), newObj.getValue());
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      objId = null;
    }
    // -----------------------------------------------------
    return objId;
  }

  @Override
  public boolean update(ConstDto newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_const");
    sbQuery.append("   set mssc_name=upper(?)");
    sbQuery.append("      ,mssc_value=?");
    sbQuery.append(" where mssc_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      queryRunner.update(conn, strQuery, newObj.getName(), newObj.getValue(), newObj.getId());
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
    sbQuery.append("delete from mss_const where mssc_id=?");
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
}
