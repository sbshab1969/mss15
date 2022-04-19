package acp.db.service.impl.dbcp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.impl.dbcp.all.ManagerListDbcp;
import acp.forms.dto.FileLoadDto;
import acp.utils.*;

public class FileLoadManagerListDbcp extends ManagerListDbcp<FileLoadDto> {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerListDbcp.class);

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

  private List<FileLoadDto> cacheObj = new ArrayList<>();

  public FileLoadManagerListDbcp() {
    tableName = "mss_files";
    
    fields = new String[] { "mssf_id", "mssf_name", "mssf_md5", "mssf_owner",
        "mssf_dt_work", "mssf_rec_all"
//        ,"extract(mssf_statistic,'statistic/records/all/text()').getStringval() rec_count"
      };

    headers = new String[] { 
        "ID"
      , Messages.getString("Column.FileName")
      , "MD5"
      , Messages.getString("Column.Owner")
      , Messages.getString("Column.DateWork")
      , Messages.getString("Column.RecordCount") 
    };
    
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , String.class
      , Timestamp.class
      , int.class
    };

    pkColumn = fields[0];
    seqId = 1000L;

    strFields = QueryUtils.buildSelectFields(fields, null);
    strFrom = tableName;
    strAwhere = null;
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
    String vDateWorkBeg = mapFilter.get("dateWorkBeg");
    String vDateWorkEnd = mapFilter.get("dateWorkEnd");
    String vRecAllBeg = mapFilter.get("recAllBeg");
    String vRecAllEnd = mapFilter.get("recAllEnd");
    // ----------------------------------
    String phWhere = null;
    String str = null;
    String vField = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(mssf_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      str = "upper(mssf_owner) like upper('" + vOwner + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    //---
    vField = "mssf_dt_work";
    String dtBeg = null;
    String dtEnd = null;
    if (!QueryUtils.emptyString(vDateWorkBeg)) {
      dtBeg = "to_date('" + vDateWorkBeg + "','dd.mm.yyyy')";
    }
    if (!QueryUtils.emptyString(vDateWorkEnd)) {
      dtEnd = "to_date('" + vDateWorkEnd + " 23:59:59" + "','dd.mm.yyyy hh24:mi:ss')";
    }
    str = null;
    if (!QueryUtils.emptyString(dtBeg) && !QueryUtils.emptyString(dtEnd)) {
      str = vField + " between " + dtBeg + " and " + dtEnd;
    } else if (!QueryUtils.emptyString(dtBeg) && QueryUtils.emptyString(dtEnd)) {
      str = vField + " >= " + dtBeg;
    } else if (QueryUtils.emptyString(dtBeg) && !QueryUtils.emptyString(dtEnd)) {
      str = vField + " <= " + dtEnd;
    }
    if (!QueryUtils.emptyString(str)) {
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    //---
    vField = "mssf_rec_all";
    String intBeg = null;
    String intEnd = null;
    if (!QueryUtils.emptyString(vRecAllBeg)) {
      intBeg = vRecAllBeg;
    }
    if (!QueryUtils.emptyString(vRecAllEnd)) {
      intEnd = vRecAllEnd;
    }
    str = null;
    if (!QueryUtils.emptyString(intBeg) && !QueryUtils.emptyString(intEnd)) {
      str = vField + " between " + intBeg + " and " + intEnd;
    } else if (!QueryUtils.emptyString(intBeg) && QueryUtils.emptyString(intEnd)) {
      str = vField + " >= " + intBeg;
    } else if (QueryUtils.emptyString(intBeg) && !QueryUtils.emptyString(intEnd)) {
      str = vField + " <= " + intEnd;
    }
    if (!QueryUtils.emptyString(str)) {
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = QueryUtils.strAddAnd(strAwhere, phWhere);
  }

  @Override
  public List<FileLoadDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<FileLoadDto> fetchPage(int startPos, int cntRows) {
    String queryPage = prepareQueryPage(strQuery,startPos,cntRows);
    try {
      Connection conn = dbConnect.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(queryPage);
      // ==============
      fillCache(rs);
      // ==============
      rs.close();
      stmt.close();
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
    }
    return cacheObj;
  }  
 
  private void fillCache(ResultSet rs) {
    cacheObj = new ArrayList<>();
    try {
      //------------------------------------------
      while (rs.next()) {
        FileLoadDto record = getObject(rs);
        cacheObj.add(record);
      }
      //------------------------------------------
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      cacheObj = new ArrayList<>();
    }
  }

  private FileLoadDto getObject(ResultSet rs) throws SQLException {
    //---------------------------------------
    Long rsId = rs.getLong("mssf_id");
    String rsName = rs.getString("mssf_name");
    String rsMd5 = rs.getString("mssf_md5");
    String rsOwner = rs.getString("mssf_owner");
    Timestamp rsDateWork = rs.getTimestamp("mssf_dt_work");
    int rsRecAll = rs.getInt("mssf_rec_all");
    //---------------------------------------
    FileLoadDto obj = new FileLoadDto();
    obj.setId(rsId);
    obj.setName(rsName);
    obj.setMd5(rsMd5);
    obj.setOwner(rsOwner);
    obj.setDateWork(rsDateWork);
    obj.setRecAll(rsRecAll);
    //---------------------------------------
    return obj;
  }

  @Override
  public long countRecords() {
    long cntRecords = 0; 
    try {
      Connection conn = dbConnect.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(strQueryCnt);
      // -------
      rs.next();
      cntRecords = rs.getLong(1);
      // -------
      rs.close();
      stmt.close();
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e,logger);
      cntRecords = 0; 
    }
    return cntRecords;    
  }

}
