package acp.db.service.impl.dbcp.all;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IToptionManagerEdit;
import acp.forms.dto.ToptionDto;
import acp.utils.*;

public class ToptionManagerEditDbcp extends ManagerBaseDbcp implements IToptionManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ToptionManagerEditDbcp.class);

  private String path;
  private ArrayList<String> attrs;
  private int attrSize;
  private int attrMax = 5;
  private String attrPrefix;

  public ToptionManagerEditDbcp(String path, ArrayList<String> attrs) {
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
    int j = 0;
    try {
      Connection conn = dbConnect.getConnection();
      PreparedStatement ps = conn.prepareStatement(strQuery);
      // ----------------------------------
      j++;
      ps.setLong(j, objId);
      j++;
      ps.setString(j, path);
      // ----------------------------------
      for (int i = 0; i < attrSize; i++) {
        j++;
        ps.setString(j, attrs.get(i));
      }
      for (int i = attrSize; i < attrMax; i++) {
        j++;
        ps.setString(j, "");
      }
      // ----------------------------------
      ResultSet rs = ps.executeQuery();
      // ----------------------------------
      if (rs.next()) {
        toptObj = getObject(rs);
      }
      rs.close();
      ps.close();
      dbConnect.close(conn);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      toptObj = null;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      toptObj = null;
    }
    // ------------------------------------------------------
    return toptObj;
  }

  private ToptionDto getObject(ResultSet rs) throws SQLException {
    // ---------------------------------------
    Long rsId = rs.getLong("config_id");
    // ----------------------
    int j = 0;
    ArrayList<String> pArr = new ArrayList<>();
    for (int i = 0; i < attrSize; i++) {
      j = i + 1;
      String pj = rs.getString("p" + j);
      pArr.add(pj);
    }
    // ----------------------
    Date rsDateBegin = rs.getTimestamp("date_begin");
    Date rsDateEnd = rs.getTimestamp("date_end");
    // ---------------------------------------
    ToptionDto obj = new ToptionDto();
    obj.setId(rsId);
    obj.setArrayP(pArr);
    obj.setDateBegin(rsDateBegin);
    obj.setDateEnd(rsDateEnd);
    // ---------------------------------------
    return obj;
  }

  @Override
  public boolean update(ToptionDto objOld, ToptionDto objNew) {
    boolean res = false;
    Long objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
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
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        String param = path + where + "/@" + attrs.get(i);
        sbQuery.append(",'" + param + "',?");
      }
    }
    sbQuery.append(") where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    try {
      Connection conn = dbConnect.getConnection();
      PreparedStatement ps = conn.prepareStatement(strQuery);
      int j = 0;
      for (int i = 0; i < attrSize; i++) {
        if (!recNewValue.get(i).equals("")) {
          ps.setString(++j, recNewValue.get(i));
        }
      }
      ps.setLong(++j, objId);
      // --------------------------
      ps.executeUpdate();
      // --------------------------
      ps.close();
      dbConnect.close(conn);
      res = true;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      res = false;
    }
    // -----------------------------------------------------
    return res;
  }

}
