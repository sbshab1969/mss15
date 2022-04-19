package acp.db.service.impl.hiber.critjpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.ConstClass;
import acp.db.service.IConstManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.forms.dto.ConstDto;
import acp.utils.DialogUtils;

public class ConstManagerEditCritJpa extends ManagerBaseHiber implements IConstManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerEditCritJpa.class);

  private ConstDto createDto(ConstClass objClass) {
    ConstDto objDto = new ConstDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    objDto.setValue(objClass.getValue());    
    // ----------------------------------
    return objDto;
  }
  
  private void fillClassByDto(ConstClass objClass, ConstDto objDto) {
    // ----------------------------------
    objClass.setName(objDto.getName());    
    objClass.setValue(objDto.getValue());    
    // ----------------------------------
  }

  @Override
  public ConstDto select(Long objId) {
    ConstDto objDto = null; 
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ----------------------------------------
      ConstClass objClass = entityManager.find(ConstClass.class, objId);
      objDto = createDto(objClass);
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
  public Long insert(ConstDto objDto) {
    Long objId = null;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ---------------------------
      ConstClass objClass = new ConstClass();
      fillClassByDto(objClass, objDto);        
      // Insert описан в XML -------------
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
  public boolean update(ConstDto objDto) {
    boolean res = false;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ---------------------------
      ConstClass objClass = entityManager.find(ConstClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);
      // --- Update описан в XML ---
      entityManager.persist(objClass);
      // ---------------------------
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
      ConstClass objClass = entityManager.getReference(ConstClass.class, objId);
      entityManager.remove(objClass);
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

}
