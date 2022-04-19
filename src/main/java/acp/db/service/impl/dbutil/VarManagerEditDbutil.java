package acp.db.service.impl.dbutil;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.VarClass;
import acp.db.service.IVarManagerEdit;
import acp.db.service.impl.dbutil.all.ManagerBaseDbutil;
import acp.db.service.impl.dbutil.all.ManagerUtilDbutil;
import acp.forms.dto.VarDto;
import acp.utils.*;

public class VarManagerEditDbutil extends ManagerBaseDbutil implements IVarManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(VarManagerEditDbutil.class);
  
  private ManagerUtilDbutil mngUtil = new ManagerUtilDbutil();

  @Override
  public VarDto select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select mssv_id, mssv_name, mssv_type, mssv_valuen, mssv_valuev, mssv_valued");
    sbQuery.append("  from mss_vars");
    sbQuery.append(" where mssv_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("select (edit): " + strQuery);
    // ------------------------------------------------------
    Map<String,String> colToProp = new HashMap<>();
    colToProp.put("MSSV_ID", "id");
    colToProp.put("MSSV_NAME", "name");
    colToProp.put("MSSV_TYPE", "type");
    colToProp.put("MSSV_VALUEN", "valuen");
    colToProp.put("MSSV_VALUEV", "valuev");
    colToProp.put("MSSV_VALUED", "valued");
    // ----------------------------------
    BeanProcessor beanProc = new BeanProcessor(colToProp);
    RowProcessor rowProc = new BasicRowProcessor(beanProc);
    ResultSetHandler<VarDto> handler = new BeanHandler<>(VarDto.class, rowProc);
    // ----------------------------------
    VarDto varObj = null;
    try {
      Connection conn = dbConnect.getConnection();
      varObj = queryRunner.query(conn,strQuery,handler,objId);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      varObj = null;
    }
    // ------------------------------------------------------
    return varObj;
  }

  @Override
  public Long insert(VarDto newObj) {
    // ------------------------------------------------------
    Long objId = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_vars");
    sbQuery.append(" (mssv_id, mssv_name, mssv_type, mssv_len,");
    sbQuery.append(" mssv_valuen, mssv_valuev, mssv_valued,");
    sbQuery.append(" mssv_last_modify, mssv_owner)");
    sbQuery.append(" values (?, upper(?), ?, 120, ?, ?, ?, sysdate, user)");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      objId = mngUtil.getValueL("select mssv_seq.nextval from dual", conn);
      Timestamp tsValued = DateUtils.util2ts(newObj.getValued());
      queryRunner.update(conn, strQuery, objId, newObj.getName(), newObj.getType(),
          newObj.getValuen(), newObj.getValuev(),tsValued);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      objId = null;
    }
    // ------------------------------------------------------
    return objId;
  }

  @Override
  public boolean update(VarDto newObj) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_vars");
    sbQuery.append("   set mssv_name=upper(?)");
    sbQuery.append("      ,mssv_type=?");
    sbQuery.append("      ,mssv_valuen=?");
    sbQuery.append("      ,mssv_valuev=?");
    sbQuery.append("      ,mssv_valued=?");
    sbQuery.append("      ,mssv_last_modify=sysdate");
    sbQuery.append("      ,mssv_owner=user");
    sbQuery.append(" where mssv_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      Timestamp tsValued = DateUtils.util2ts(newObj.getValued());
      queryRunner.update(conn, strQuery, newObj.getName(), newObj.getType(), 
          newObj.getValuen(), newObj.getValuev(), tsValued, newObj.getId());
      dbConnect.close(conn);
      res = true;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------
    return res;
  }

  @Override
  public boolean delete(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("delete from mss_vars where mssv_id=?");
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

  @Override
  public void fillVars(Map<String, String> varMap) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.* from mss_vars t");
    sbQuery.append(" where upper(mssv_name) like 'CERT%'");
    sbQuery.append(" or upper(mssv_name) = 'VERSION_MSS' order by mssv_name");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    Map<String,String> colToProp = new HashMap<>();
    colToProp.put("MSSV_ID", "id");
    colToProp.put("MSSV_NAME", "name");
    colToProp.put("MSSV_TYPE", "type");
    colToProp.put("MSSV_LEN", "len");
    colToProp.put("MSSV_VALUEN", "valuen");
    colToProp.put("MSSV_VALUEV", "valuev");
    colToProp.put("MSSV_VALUED", "valued");
    colToProp.put("MSSV_LAST_MODIFY", "dateModify");
    colToProp.put("MSSV_OWNER", "owner");
    // ----------------------------------
    BeanProcessor beanProc = new BeanProcessor(colToProp);
    RowProcessor rowProc = new BasicRowProcessor(beanProc);
    ResultSetHandler<List<VarClass>> handler = new BeanListHandler<>(VarClass.class, rowProc);
    // ----------------------------------
    List<VarClass> objList = null;
    try {
      Connection conn = dbConnect.getConnection();
      objList = queryRunner.query(conn,strQuery,handler);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
    }
    // ---------------------------------------------
    for (VarClass vcls : objList) {
      String rsqName = vcls.getName().toUpperCase();
      String valuev = null;
      Date valued = null;
      if (rsqName.startsWith("CERT")) {
        valuev = vcls.getValuev();
        varMap.put(rsqName, valuev);
      } else if (rsqName.equals("VERSION_MSS")) {
        valuev = vcls.getValuev();
        valued = vcls.getValued();
        varMap.put("VERSION", valuev);
        varMap.put("VERSION_DATE", DateUtils.date2Str(valued));
      }
    }
    // ---------------------------------------------
  }

}
