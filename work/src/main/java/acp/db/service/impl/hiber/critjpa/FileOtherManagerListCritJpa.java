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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.FileOtherClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.FileOtherDto;
import acp.utils.*;

public class FileOtherManagerListCritJpa extends ManagerListHiber<FileOtherDto> {
  private static Logger logger = LoggerFactory.getLogger(FileOtherManagerListCritJpa.class);

  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  Long fileId;
  
  @SuppressWarnings("unused")
  private Map<String,String> mapFilter;

  private List<FileOtherDto> cacheObj = new ArrayList<>();

  public FileOtherManagerListCritJpa(Long file_id) {
    fields = new String[] { "id", "dateEvent", "descr" };

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

    fileId = file_id;
    
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
  public List<FileOtherDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<FileOtherDto> fetchPage(int startPos, int cntRows) {
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
    Root<FileOtherClass> root = criteria.from(FileOtherClass.class);
    // ------------------
    List<Selection<?>> selectionList = new ArrayList<>();
    for (int i=0; i<fields.length; i++) {
      selectionList.add(root.get(fields[i]));
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
  
  private Predicate buildWhere(CriteriaBuilder builder, Root<FileOtherClass> root) {
    Path<Long> fRefId = root.get("refId");
    Predicate pred = builder.equal(fRefId, fileId);
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

  private FileOtherDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
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
    Root<FileOtherClass> root = criteria.from(FileOtherClass.class);
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
