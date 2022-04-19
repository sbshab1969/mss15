package acp.db.service.impl.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.impl.dbutil.all.ManagerListDbutil;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerListDbutil extends ManagerListDbutil<ConfigDto> {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerListDbutil.class);

  private String tableName;
  private String[] fields;
  private String[] headers;
  private Class<?>[] types;

  private String pkColumn;
  private Long seqId;

  private String strFields;
  private String strFrom;
  private String strAwhere;
  private String strWhere;
  private String strOrder;
  
  private String strQuery;
  private String strQueryCnt;

  private List<ConfigDto> cacheObj = new ArrayList<>();

  public ConfigManagerListDbutil() {
    tableName = "mss_options";
    
    fields = new String[] { 
        "msso_id"
      , "msso_name"
      , "msso_dt_begin"
      , "msso_dt_end"
      , "msso_comment"
      , "msso_owner"
      , "msso_msss_id"
      , "msss_name" 
    };

    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.SourceName")
      , Messages.getString("Column.DateBegin")
      , Messages.getString("Column.DateEnd")
      , Messages.getString("Column.Comment")
      , Messages.getString("Column.Owner") 
    };
    
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Date.class
      , Date.class
      , String.class
      , String.class 
    };

    pkColumn = fields[0];
    seqId = 1000L;

    strFields = QueryUtils.buildSelectFields(fields, null);
//    strFrom = "mss_options, mss_source";
    strFrom = tableName + ", mss_source";
    strAwhere = "msso_msss_id=msss_id";
    strWhere = strAwhere;
    strOrder = pkColumn;

    prepareQuery(null);
  }

  @Override
  public String[] getHeaders() {
    return headers;    
  }

  @Override
  public Class<?>[] getTypes() {
    return types;    
  }

  @Override
  public Long getSeqId() {
    return seqId;
  }

  @Override
  public void prepareQuery(Map<String,String> mapFilter) {
    if (mapFilter != null) {
      setWhere(mapFilter);
    } else {
      strWhere = strAwhere;
    }
    strQuery = QueryUtils.buildQuery(strFields, strFrom, strWhere, strOrder);
    strQueryCnt = QueryUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
  }

  private void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    String vOwner = mapFilter.get("owner"); 
    String vSourceId = mapFilter.get("sourceId"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(msso_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      str = "upper(msso_owner) like upper('" + vOwner + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vSourceId)) {
      str = "msso_msss_id=" + vSourceId;
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = QueryUtils.strAddAnd(strAwhere, phWhere);
  }
  
  @Override
  public List<ConfigDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConfigDto> fetchPage(int startPos, int cntRows) {
    String queryPage = prepareQueryPage(strQuery,startPos,cntRows);
    try {
      Connection conn = dbConnect.getConnection();
      // ==================
      fillCache(conn, queryPage);
      // ==================
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
    }
    return cacheObj;
  }  
 
  private void fillCache(Connection conn, String sqlQuery) throws SQLException {
    ResultSetHandler<List<Object[]>> handler = new ArrayListHandler();
    // ===============================================================
    List<Object[]> objList = queryRunner.query(conn,sqlQuery,handler);
    // ===============================================================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private ConfigDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = ((BigDecimal) obj[0]).longValue();
    String rsName = (String) obj[1];
    Timestamp rsDateBegin = (Timestamp) obj[2];
    Timestamp rsDateEnd = (Timestamp) obj[3];
    String rsComment = (String) obj[4];
    String rsOwner = (String) obj[5];
    Long rsSourceId = ((BigDecimal) obj[6]).longValue();
    String rsSourceName = (String) obj[7];
    //---------------------------------------
    ConfigDto objDto = new ConfigDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    objDto.setComment(rsComment);
    objDto.setOwner(rsOwner);
    objDto.setSourceId(rsSourceId);
    objDto.setSourceName(rsSourceName);
    //---------------------------------------
    return objDto;
  }

  @Override
  public long countRecords() {
    System.out.println("count: " + strQueryCnt);
    long cntRecords = 0; 
    ScalarHandler<BigDecimal> handler = new ScalarHandler<>();
    try {
      Connection conn = dbConnect.getConnection();
      cntRecords = queryRunner.query(conn, strQueryCnt, handler).longValue();
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      cntRecords = 0; 
    }
    return cntRecords;    
  }

}
