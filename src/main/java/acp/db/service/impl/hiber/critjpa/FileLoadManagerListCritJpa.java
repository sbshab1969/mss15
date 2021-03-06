package acp.db.service.impl.hiber.critjpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

import acp.db.domain.FileLoadClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.FileLoadDto;
import acp.utils.*;

public class FileLoadManagerListCritJpa extends ManagerListHiber<FileLoadDto> {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerListCritJpa.class);

  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;

  private List<FileLoadDto> cacheObj = new ArrayList<>();

  public FileLoadManagerListCritJpa() {
    fields = new String[] { "id", "name", "md5", "owner", "dateWork", "recAll" };

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
  public List<FileLoadDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<FileLoadDto> fetchPage(int startPos, int cntRows) {
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try { 
      // -----------------------
      TypedQuery<Object[]> query = createQuery(entityManager);
      // -----------------------
      if (startPos>0) {
        query.setFirstResult(startPos-1);  // Hibernate ???????????????? ?? 0
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
    Root<FileLoadClass> root = criteria.from(FileLoadClass.class);
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
  
  private Predicate buildWhere(CriteriaBuilder builder, Root<FileLoadClass> root) {
    Predicate pred = null;
    // ----------------------------------
    Path<String> fName = root.get("name");
    Path<String> fOwner = root.get("owner");
    Path<Timestamp> fDateWork = root.get("dateWork");
    Path<Integer> fRecAll = root.get("recAll");
    // ----------------------------------
    String vName = mapFilter.get("name");
    String vOwner = mapFilter.get("owner");
    String vDateWorkBeg = mapFilter.get("dateWorkBeg");
    String vDateWorkEnd = mapFilter.get("dateWorkEnd");
    String vRecAllBeg = mapFilter.get("recAllBeg");
    String vRecAllEnd = mapFilter.get("recAllEnd");
    // ----------------------------------
    Predicate conj = builder.conjunction();
    // ---
    if (!QueryUtils.emptyString(vName)) {
      conj.getExpressions().add(builder.like(builder.upper(fName), vName.toUpperCase() + "%"));
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      conj.getExpressions().add(builder.like(builder.upper(fOwner), vOwner.toUpperCase() + "%"));
    }
    //---
    Date dtBeg = null;
    Date dtEnd = null;
    if (!QueryUtils.emptyString(vDateWorkBeg)) {
      dtBeg = DateUtils.str2Date(vDateWorkBeg);
    }
    if (!QueryUtils.emptyString(vDateWorkEnd)) {
      dtEnd = DateUtils.str2DateTime(vDateWorkEnd + " 23:59:59");
    }
    if (dtBeg != null && dtEnd != null) {
      conj.getExpressions().add(builder.between(fDateWork,dtBeg,dtEnd));
    } else if (dtBeg != null && dtEnd == null) {
      conj.getExpressions().add(builder.greaterThanOrEqualTo(fDateWork, dtBeg));
    } else if (dtBeg == null && dtEnd != null) {
      conj.getExpressions().add(builder.lessThanOrEqualTo(fDateWork, dtEnd));
    }
    //---
    Integer intBeg = null;
    Integer intEnd = null;
    if (!QueryUtils.emptyString(vRecAllBeg)) {
      intBeg = Integer.valueOf(vRecAllBeg);
    }
    if (!QueryUtils.emptyString(vRecAllEnd)) {
      intEnd = Integer.valueOf(vRecAllEnd);
    }
    if (intBeg != null && intEnd != null) {
      conj.getExpressions().add(builder.between(fRecAll,intBeg,intEnd));
    } else if (intBeg != null && intEnd == null) {
//      conj.getExpressions().add(builder.greaterThanOrEqualTo(fRecAll, intBeg));
      conj.getExpressions().add(builder.ge(fRecAll, intBeg));
    } else if (intBeg == null && intEnd != null) {
//      conj.getExpressions().add(builder.lessThanOrEqualTo(fRecAll, intEnd));
      conj.getExpressions().add(builder.le(fRecAll, intEnd));
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
  
  private FileLoadDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    String rsMd5 = (String) obj[2];
    String rsOwner = (String) obj[3];
    Timestamp rsDateWork = (Timestamp) obj[4];
    Integer rsRecAll = (Integer) obj[5];
    //---------------------------------------
    FileLoadDto objDto = new FileLoadDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setMd5(rsMd5);
    objDto.setOwner(rsOwner);
    objDto.setDateWork(rsDateWork);
    objDto.setRecAll(rsRecAll);
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
    Root<FileLoadClass> root = criteria.from(FileLoadClass.class);
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