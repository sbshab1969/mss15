package acp.db.service.impl.dbutil.all;

import java.math.BigDecimal;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IToptionManagerEdit;
import acp.forms.dto.ToptionDto;
import acp.utils.*;

public class ToptionManagerEditDbutil extends ManagerBaseDbutil implements IToptionManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ToptionManagerEditDbutil.class);

  private String path;
  private ArrayList<String> attrs;
  private int attrSize;
  private int attrMax = 5;
  private String attrPrefix;

  public ToptionManagerEditDbutil(String path, ArrayList<String> attrs) {
    this.path = path;
    this.attrs = attrs;
    this.attrSize = attrs.size();
    String[] pathArray = path.split("/");
    this.attrPrefix = pathArray[pathArray.length - 1];
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public ArrayList<String> getAttrs() {
    return attrs;
  }

  @Override
  public String getAttrPrefix() {
    return attrPrefix;
  }

  @Override
  public ToptionDto select(Long objId) {
    ToptionDto toptObj = null;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.* from table(mss.spr_option_id(?,?,?,?,?,?,?)) t");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    Object[] sqlParams = new Object[7];  // 7 параметров в запросе.
    int j = 0;
    sqlParams[j++] = objId;
    sqlParams[j++] = path;
    for (int i = 0; i < attrSize; i++) {
      sqlParams[j++] =  attrs.get(i);
    }
    for (int i = attrSize; i < attrMax; i++) {
      sqlParams[j++] =  "";
    }
    System.out.println("select (edit): " + Arrays.toString(sqlParams));
    // ------------------------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      // ----------------------------------
      ResultSetHandler<Object[]> handler = new ArrayHandler();
      Object[] result = queryRunner.query(conn,strQuery,handler,sqlParams);
      toptObj = getObject(result);
      // ---------------------
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      toptObj = null;
    }
    // ------------------------------------------------------
    return toptObj;
  }

  private ToptionDto getObject(Object[] obj) {
    //---------------------------------------
    BigDecimal idBigDec = (BigDecimal) obj[0];
    Long rsId = idBigDec.longValue();
    // ----------------------
    int j = 0;
    ArrayList<String> pArr = new ArrayList<>();
    for (int i = 0; i < attrSize; i++) {
      j = i + 1;
      String pj = (String) obj[j];
      pArr.add(pj);
    }
    // ----------------------
    Date rsDateBegin = (Date) obj[++j];
    Date rsDateEnd = (Date) obj[++j];
    //---------------------------------------
    ToptionDto objDto = new ToptionDto();
    objDto.setId(rsId);
    objDto.setArrayP(pArr);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    //---------------------------------------
    return objDto;
  }

  @Override
  public boolean update(ToptionDto objOld, ToptionDto objNew) {
    boolean res = false;
    Long objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
//    System.out.println(recOldValue);
//    System.out.println(recNewValue);
    // -----------------------------------------
    String where = "";
    for (int i = 0; i < attrSize; i++) {
      if (recOldValue.get(i) != null) {
        where += "[@" + attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]";
      }
    }
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options set ");
    sbQuery.append("msso_config=updateXml(msso_config");
    int cnt = 0;
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        String param = path + where + "/@" + attrs.get(i);
        sbQuery.append(",'" + param + "',?");
        cnt++;
      }
    }
    sbQuery.append(") where msso_id=?");
    String strQuery = sbQuery.toString();
    System.out.println(strQuery);
    // -----------------------------------------
    Object[] sqlParams = new Object[cnt+1];
    int j = 0;
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        sqlParams[j++] = recNewValue.get(i);
      }
    }
    sqlParams[j] = objId;
    System.out.println("update: " + Arrays.toString(sqlParams));
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      // --------------------------
      queryRunner.update(conn,strQuery,sqlParams);
      // --------------------------
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
