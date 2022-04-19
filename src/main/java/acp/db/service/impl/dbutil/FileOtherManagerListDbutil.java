package acp.db.service.impl.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.impl.dbutil.all.ManagerListDbutil;
import acp.forms.dto.FileOtherDto;
import acp.utils.*;

public class FileOtherManagerListDbutil extends ManagerListDbutil<FileOtherDto> {
  private static Logger logger = LoggerFactory.getLogger(FileOtherManagerListDbutil.class);

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

  private List<FileOtherDto> cacheObj = new ArrayList<>();

  public FileOtherManagerListDbutil(Long file_id) {
    tableName = "mss_logs";

    fields = new String[] { "mssl_id", "mssl_dt_event", "mssl_desc" };
    
    headers = new String[] { "ID"
      , Messages.getString("Column.Time")
      , Messages.getString("Column.Desc") 
    };
    
    types = new Class<?>[] { 
        Long.class
      , Timestamp.class
      , String.class
    };
    
    pkColumn = fields[0];
    seqId = 1000L;

    strFields = QueryUtils.buildSelectFields(fields, null);
    strFrom = tableName;
    strAwhere = "mssl_ref_id=" + file_id;
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
    strWhere = strAwhere;
  }

  @Override
  public List<FileOtherDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<FileOtherDto> fetchPage(int startPos, int cntRows) {
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
  
  private FileOtherDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = ((BigDecimal) obj[0]).longValue();
    Timestamp rsDateEvent = (Timestamp) obj[1];
    String rsDescr = (String) obj[2];
    //---------------------------------------
    FileOtherDto objDto = new FileOtherDto();
    objDto.setId(rsId);
    objDto.setDateEvent(rsDateEvent);
    objDto.setDescr(rsDescr);
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
