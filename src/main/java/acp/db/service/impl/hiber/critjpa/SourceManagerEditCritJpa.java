package acp.db.service.impl.hiber.critjpa;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
//import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.SourceClass;
import acp.db.service.ISourceManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.db.service.impl.hiber.all.ManagerUtilSql;
import acp.forms.dto.SourceDto;
import acp.utils.DialogUtils;
import acp.utils.QueryUtils;

public class SourceManagerEditCritJpa extends ManagerBaseHiber implements ISourceManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(SourceManagerEditCritJpa.class);

  private ManagerUtilSql mngUtil = new ManagerUtilSql();

  private SourceDto createDto(SourceClass objClass) {
    SourceDto objDto = new SourceDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
//    objDto.setOwner(objClass.getOwner());    
    // ----------------------------------
    return objDto;
  }
  
  private void fillClassByDto(SourceClass objClass, SourceDto objDto) {
    // ----------------------------------
    objClass.setName(objDto.getName());
    // ----------------------------------
  }

  private void fillClassInfo(SourceClass objClass, EntityManager entityManager) {
    Timestamp sysdt = mngUtil.getSysdate(entityManager);
    String usr = mngUtil.getUser(entityManager);
    // ----------------------------------
    if (objClass.getId() == null) {
      objClass.setDateCreate(sysdt);
    }
    objClass.setDateModify(sysdt);
    objClass.setOwner(usr);
    // ----------------------------------
  }

  @Override
  public SourceDto select(Long objId) {
    SourceDto objDto = null; 
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ----------------------------------------
      SourceClass objClass = entityManager.find(SourceClass.class, objId);
      objDto= createDto(objClass);
      // ----------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objDto = null;
    } finally {
      dbConnect.close(entityManager);
    }  
    return objDto;
  }

  @Override
  public Long insert(SourceDto objDto) {
    Long objId = null;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ---------------------------------
      SourceClass objClass = new SourceClass();
      fillClassByDto(objClass, objDto);        
      fillClassInfo(objClass, entityManager);        
      entityManager.persist(objClass);
      objId = objClass.getId();
      // ---------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objId = null;
    } finally {
      dbConnect.close(entityManager);
    }  
    return objId;
  }

  @Override
  public boolean update(SourceDto objDto) {
    boolean res = false;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // --------------------
      SourceClass objClass = entityManager.find(SourceClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);        
      fillClassInfo(objClass, entityManager);        
      entityManager.persist(objClass);
      // --------------------
      tx.commit();
      res = true;
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      dbConnect.close(entityManager);
    }  
    return res;
  }

  @Override
  public boolean delete(Long objId) {
    boolean res = false;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ---------------------------------------------------
      entityManager.remove(entityManager.getReference(SourceClass.class, objId));
      // ---------------------------------------------------
      tx.commit();
      res = true;
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      dbConnect.close(entityManager);
    }  
    return res;
  }

  private List<?> getListSources() {
    List<?> objList =  null;
    // --------------------------------------------
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // -----------------------------------------------------------------
      CriteriaBuilder builder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
      Root<SourceClass> root = criteria.from(SourceClass.class);
      // -----------
      Path<Long> pId = root.get("id"); 
      Path<String> pName = root.get("name"); 
      // -----------
      criteria.multiselect(pId, pName);
      criteria.orderBy(builder.asc(pName));
      // -----------------------------------------------------------------
      objList = entityManager.createQuery(criteria).getResultList();
      // -----------------------------------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(entityManager);
    }  
    // -------------------------------------------
    return objList;
  }

  @Override
  public List<String[]> getSources() {
    List<?> objList =  getListSources();
    List<String[]> arrayString = QueryUtils.getListString(objList);
    return arrayString;
  }

}
