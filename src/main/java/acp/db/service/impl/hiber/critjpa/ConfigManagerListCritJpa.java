package acp.db.service.impl.hiber.critjpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.ConfigClass;
import acp.db.domain.SourceClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerListCritJpa extends ManagerListHiber<ConfigDto> {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerListCritJpa.class);

  private String[] fields;
  private String[] fieldsSrc;
  
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;
  
  private List<ConfigDto> cacheObj = new ArrayList<>();

  public ConfigManagerListCritJpa() {
    fields = new String[] { 
        "id"
      , "name"
      , "dateBegin"
      , "dateEnd"
      , "comment"
      , "owner"
    };
    
    fieldsSrc = new String[] { 
        "name"
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
    this.mapFilter = mapFilter;
  }
 
  @Override
  public List<ConfigDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConfigDto> fetchPage(int startPos, int cntRows) {
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try { 
      // -----------------------
      TypedQuery<Object[]> query = createQuery(entityManager);
      // -----------------------
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
      dbConnect.close(entityManager);
    }  
    return cacheObj;    
  }  

  private TypedQuery<Object[]> createQuery(EntityManager entityManager) {
    // -------------------------------------
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
    Root<ConfigClass> root = criteria.from(ConfigClass.class);
//    root.alias("a");
    Join<ConfigClass, SourceClass> source = root.join("source");
//    source.alias("b");
    // ------------------
    List<Selection<?>> selectionList = new ArrayList<>();
    for (int i=0; i<fields.length; i++) {
      selectionList.add(root.get(fields[i]));
    }
    for (int i=0; i<fieldsSrc.length; i++) {
      selectionList.add(source.get(fieldsSrc[i]));
    }
    criteria.multiselect(selectionList);
    // ------------------
    Predicate pred = buildWhere(builder, root);
    if (pred != null) {
      criteria.where(pred);
    }  
    criteria.orderBy(builder.asc(root.get(pkColumn)));
    // -------------------------------------
    TypedQuery<Object[]> query = entityManager.createQuery(criteria);
    // -------------------------------------
    return query;
  }
  
  private Predicate buildWhere(CriteriaBuilder builder, Root<ConfigClass> root) {
    Predicate pred = null;
    // ----------------------------------
    Path<String> fName = root.get("name");
    Path<String> fOwner = root.get("owner");
    Path<Long> fSourceId = root.get("sourceId");
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    String vOwner = mapFilter.get("owner"); 
    String vSourceId = mapFilter.get("sourceId"); 
    // ----------------------------------
    Predicate conj = builder.conjunction();
    if (!QueryUtils.emptyString(vName)) {
      conj.getExpressions().add(builder.like(builder.upper(fName), vName.toUpperCase() + "%"));
//      conj = builder.and(conj, builder.like(builder.upper(fName), vName.toUpperCase() + "%"));
    }
    if (!QueryUtils.emptyString(vOwner)) {
      conj.getExpressions().add(builder.like(builder.upper(fOwner), vOwner.toUpperCase() + "%"));
//      conj = builder.and(conj, builder.like(builder.upper(fOwner), vOwner.toUpperCase() + "%"));
    }
    if (!QueryUtils.emptyString(vSourceId)) {
      Long longSourceId = Long.parseLong(vSourceId);
      conj.getExpressions().add(builder.equal(fSourceId, longSourceId));
//      conj = builder.and(conj, builder.equal(fSourceId, longSourceId));
    }
    // ----------------------------------
    if (conj.getExpressions().size() != 0) {
      pred = conj;
    }
    // ----------------------------------
    return pred;
  }

  private void fillCache(TypedQuery<Object[]> query) {
    // ============================
    List<?> objList = query.getResultList();
    // ============================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private ConfigDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    Timestamp rsDateBegin = (Timestamp) obj[2];
    Timestamp rsDateEnd = (Timestamp) obj[3];
    String rsComment = (String) obj[4];
    String rsOwner = (String) obj[5];
    String rsSourceName = (String) obj[6];
    //---------------------------------------
    ConfigDto objDto = new ConfigDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    objDto.setComment(rsComment);
    objDto.setOwner(rsOwner);
    objDto.setSourceName(rsSourceName);
    //---------------------------------------
    return objDto;
  }

  @Override
  public long countRecords() {
    long cntRecords = 0; 
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      //-----------------------------------------------------------
      TypedQuery<Long> query = createQueryCnt(entityManager);
      cntRecords = (Long) query.getSingleResult();
      //-----------------------------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(entityManager);
    }  
    return cntRecords;    
  }
  
  private TypedQuery<Long> createQueryCnt(EntityManager entityManager) {
    // ------------------------------------------------------------
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
    Root<ConfigClass> root = criteria.from(ConfigClass.class);
    criteria.select(builder.count(root));
    //------------
    Predicate pred = buildWhere(builder, root);
    if (pred != null) {
      criteria.where(pred);
    }  
    // ------------------------------------------------------------
    TypedQuery<Long> query = entityManager.createQuery(criteria);
    // ------------------------------------------------------------
    return query;
  }

}
