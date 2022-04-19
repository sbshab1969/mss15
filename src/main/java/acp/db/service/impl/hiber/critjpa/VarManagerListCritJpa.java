package acp.db.service.impl.hiber.critjpa;

import java.util.Date;

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

import acp.db.domain.VarClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.VarDto;
import acp.utils.*;

public class VarManagerListCritJpa extends ManagerListHiber<VarDto> {
  private static Logger logger = LoggerFactory.getLogger(VarManagerListCritJpa.class);

  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;

  private List<VarDto> cacheObj = new ArrayList<>();

  public VarManagerListCritJpa() {
    fields = new String[] { "id", "name", "type", "valuen", "valuev", "valued" };

    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Type")
      , Messages.getString("Column.Number")
      , Messages.getString("Column.Varchar")
      , Messages.getString("Column.Date") };
    
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Double.class
      , String.class
      , Date.class
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
  public List<VarDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<VarDto> fetchPage(int startPos, int cntRows) {
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try { 
      // -------------------------------------
      TypedQuery<Object[]> query = createQuery(entityManager);
      // -----------
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
    Root<VarClass> root = criteria.from(VarClass.class);
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
  
  private Predicate buildWhere(CriteriaBuilder builder, Root<VarClass> root) {
    Predicate pred = null;
    // ----------------------------------
    Path<String> fName = root.get("name");
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    // ----------------------------------
    if (!QueryUtils.emptyString(vName)) {
      pred = builder.like(builder.upper(fName), vName.toUpperCase() + "%");
    }
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
  
  private VarDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    String rsType = (String) obj[2];
    Double rsValuen = (Double) obj[3];
    String rsValuev = (String) obj[4];
    Date rsValued = (Date) obj[5];
    //---------------------------------------
    VarDto objDto = new VarDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setType(rsType);
    objDto.setValuen(rsValuen);
    objDto.setValuev(rsValuev);
    objDto.setValued(rsValued);
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
    Root<VarClass> root = criteria.from(VarClass.class);
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
