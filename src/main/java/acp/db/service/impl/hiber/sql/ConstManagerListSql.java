package acp.db.service.impl.hiber.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.ConstDto;
import acp.utils.*;

public class ConstManagerListSql extends ManagerListHiber<ConstDto> {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerListSql.class);

  private String tableName;
  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  private AbstractSingleColumnStandardBasicType<?>[] hiberTypes;

  private String pkColumn;
  private Long seqId;

  private String strFields;
  private String strFrom;
  private String strAwhere;
  private String strWhere;
  private String strOrder;
  
  private String strQuery;
  private String strQueryCnt;

  private List<ConstDto> cacheObj = new ArrayList<>();

  public ConstManagerListSql() {
    tableName = "mss_const";

    fields = new String[] { "mssc_id", "mssc_name", "mssc_value" };

    headers = new String[] {
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Value") 
    };
    
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
    };
    
    hiberTypes = new AbstractSingleColumnStandardBasicType<?>[] { 
        LongType.INSTANCE
      , StringType.INSTANCE
      , StringType.INSTANCE
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
    strQueryCnt = QueryUtils.buildQuery("select count(*) cnt from " + strFrom, strWhere, null);
  }

  private void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(mssc_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    strWhere = QueryUtils.strAddAnd(strAwhere, phWhere);
  }

  @Override
  public List<ConstDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConstDto> fetchPage(int startPos, int cntRows) {
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try { 
      // HQL -------------------------------------
      NativeQuery<?> query = session.createNativeQuery(strQuery);
      for (int i=0; i<hiberTypes.length; i++) {
        query.addScalar(fields[i], hiberTypes[i]);
      }
      // ------------------------------------------
      if (startPos>0) {
        query.setFirstResult(startPos-1);  // Hibernate начинает с 0
      }
      if (cntRows>0) {
        query.setMaxResults(cntRows);
      }  
      // ==============
      fillCache(query);
      // ==============
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(session);
    }  
    return cacheObj;    
  }  

  private void fillCache(NativeQuery<?> query) {
    logger.info("\nQuery string: " + query.getQueryString());
    // ============================
    List<?> objList = query.list();
    // ============================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private ConstDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    String rsValue = (String) obj[2];
    //---------------------------------------
    ConstDto objDto = new ConstDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setValue(rsValue);
    //---------------------------------------
    return objDto;
  }

  @Override
  public long countRecords() {
    long cntRecords = 0; 
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      NativeQuery<?> query = session.createNativeQuery(strQueryCnt)
              .addScalar("cnt", LongType.INSTANCE);
      cntRecords = (Long) query.uniqueResult();
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(session);
    }  
    return cntRecords;    
  }

}
