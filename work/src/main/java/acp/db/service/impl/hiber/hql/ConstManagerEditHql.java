package acp.db.service.impl.hiber.hql;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.ConstClass;
import acp.db.service.IConstManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.forms.dto.ConstDto;
import acp.utils.DialogUtils;

public class ConstManagerEditHql extends ManagerBaseHiber implements IConstManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerEditHql.class);

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
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ----------------------------------------
      ConstClass objClass = session.get(ConstClass.class, objId);
      objDto = createDto(objClass);
      // ----------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objDto = null;
    } finally {
      dbConnect.close(session);
    }  
    return objDto;
  }

  @Override
  public Long insert(ConstDto objDto) {
    Long objId = null;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ---------------------------
      ConstClass objClass = new ConstClass();
      fillClassByDto(objClass, objDto);        
      // Insert описан в XML -------------
      objId = (Long) session.save(objClass);
      // ---------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objId = null;
    } finally {
      dbConnect.close(session);
    }  
    return objId;
  }
 
  @Override
  public boolean update(ConstDto objDto) {
    boolean res = false;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ---------------------------
      ConstClass objClass = session.get(ConstClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);
      // --- Update описан в XML ---
      session.update(objClass);
      // ---------------------------
      tx.commit();
      res = true;
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      dbConnect.close(session);
    }  
    return res;
  }
 
  @Override
  public boolean delete(Long objId) {
    boolean res = false;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ---------------------------------------------------
      session.delete(session.load(ConstClass.class, objId));
      // ---------------------------------------------------
      tx.commit();
      res = true;
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      dbConnect.close(session);
    }  
    return res;
  }

}
