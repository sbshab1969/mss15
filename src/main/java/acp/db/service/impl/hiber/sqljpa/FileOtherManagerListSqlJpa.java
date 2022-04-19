package acp.db.service.impl.hiber.sqljpa;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

//import org.hibernate.type.AbstractSingleColumnStandardBasicType;
//import org.hibernate.type.LongType;
//import org.hibernate.type.StringType;
//import org.hibernate.type.TimestampType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.FileOtherDto;
import acp.utils.*;

public class FileOtherManagerListSqlJpa extends ManagerListHiber<FileOtherDto> {
  private static Logger logger = LoggerFactory.getLogger(FileOtherManagerListSqlJpa.class);

  private String tableName;
  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
//  private AbstractSingleColumnStandardBasicType<?>[] hiberTypes;

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

  public FileOtherManagerListSqlJpa(Long file_id) {
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
    
//    hiberTypes = new AbstractSingleColumnStandardBasicType<?>[] { 
//      LongType.INSTANCE
//    , TimestampType.INSTANCE
//    , StringType.INSTANCE
//    };

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
    strQueryCnt = QueryUtils.buildQuery("select count(*) cnt from " + strFrom, strWhere, null);
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
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // -------------------------------------
      Query query = entityManager.createNativeQuery(strQuery);
//      for (int i = 0; i < hiberTypes.length; i++) {
//        query.addScalar(fields[i], hiberTypes[i]);
//      }
      // -----------------------------------------
      if (startPos > 0) {
        query.setFirstResult(startPos - 1); // Hibernate начинает с 0
      }
      if (cntRows > 0) {
        query.setMaxResults(cntRows);
      }
      // ==============
      fillCache(query);
      // ==============
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e, logger);
    } finally {
      dbConnect.close(entityManager);
    }
    return cacheObj;
  }

  private void fillCache(Query query) {
    // ============================
    List<?> objList = query.getResultList();
    // ============================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private FileOtherDto getObject(Object[] obj) {
    //---------------------------------------
//    Long rsId = (Long) obj[0];
    BigDecimal idBigDec = (BigDecimal) obj[0];
    Long rsId = idBigDec.longValue();
    
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
    long cntRecords = 0; 
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      Query query = entityManager.createNativeQuery(strQueryCnt);
//              .addScalar("cnt", LongType.INSTANCE);
      BigDecimal bdCntRecords = (BigDecimal) query.getSingleResult();
      cntRecords = bdCntRecords.longValue();
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(entityManager);
    }  
    return cntRecords;    
  }

}
