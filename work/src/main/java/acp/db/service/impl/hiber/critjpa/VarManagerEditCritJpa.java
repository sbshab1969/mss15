package acp.db.service.impl.hiber.critjpa;

import java.sql.Timestamp;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.VarClass;
import acp.db.service.IVarManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.db.service.impl.hiber.all.ManagerUtilSql;
import acp.forms.dto.VarDto;
import acp.utils.*;

public class VarManagerEditCritJpa extends ManagerBaseHiber implements IVarManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(VarManagerEditCritJpa.class);

  private ManagerUtilSql mngUtil = new ManagerUtilSql();

  private VarDto createDto(VarClass objClass) {
    VarDto objDto = new VarDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    objDto.setType(objClass.getType());    
    objDto.setValuen(objClass.getValuen());    
    objDto.setValuev(objClass.getValuev());    
    objDto.setValued(objClass.getValued());    
    // ----------------------------------
    return objDto;
  }
  
  private void fillClassByDto(VarClass objClass, VarDto objDto) {
    // ----------------------------------
    objClass.setName(objDto.getName().toUpperCase());
    objClass.setType(objDto.getType());
    objClass.setValuen(objDto.getValuen());
    objClass.setValuev(objDto.getValuev());
    objClass.setValued(objDto.getValued());
    // ----------------------------------
  }

  private void fillClassInfo(VarClass objClass, EntityManager entityManager) {
    Timestamp sysdt = mngUtil.getSysdate(entityManager);
    String usr = mngUtil.getUser(entityManager);
    // ----------------------------------
    objClass.setLen(120);
    objClass.setDateModify(sysdt);
    objClass.setOwner(usr);
    // ----------------------------------
  }

  @Override
  public VarDto select(Long objId) {
    VarDto objDto = null; 
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ----------------------------------------
      VarClass objClass = entityManager.find(VarClass.class, objId);
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
  public Long insert(VarDto objDto) {
    Long objId = null;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // --------------------
      VarClass objClass = new VarClass();
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
  public boolean update(VarDto objDto) {
    boolean res = false;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // --------------------
      VarClass objClass = entityManager.find(VarClass.class, objDto.getId());
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
      VarClass objClass = entityManager.getReference(VarClass.class, objId);
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

  @Override
  public void fillVars(Map<String, String> varMap) {
    // ------------------------------------------------------
    List<VarClass> objList = null;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // -----------------------------------------------------------------
      TypedQuery<VarClass> query = createQueryVars(entityManager);
      objList = query.getResultList();
      // -----------------------------------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(entityManager);
    }  
    // ---------------------------------------------
    for (VarClass vcls : objList) {
      String rsqName = vcls.getName().toUpperCase();
      String valuev = null;
      Date valued = null;
      if (rsqName.startsWith("CERT")) {
        valuev = vcls.getValuev();
        varMap.put(rsqName, valuev);
      } else if (rsqName.equals("VERSION_MSS")) {
        valuev = vcls.getValuev();
        valued = vcls.getValued();
        varMap.put("VERSION", valuev);
        varMap.put("VERSION_DATE", DateUtils.date2Str(valued));
      }
    }
    // ---------------------------------------------
  }

  private TypedQuery<VarClass> createQueryVars(EntityManager entityManager) {
    // -------------------------------------
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<VarClass> criteria = builder.createQuery(VarClass.class);
    Root<VarClass> root = criteria.from(VarClass.class);
    criteria.select(root);
    // ------
    Predicate pred = buildWhere(builder, root);
    criteria.where(pred);
    // ------
    Path<String> fName = root.get("name");
    criteria.orderBy(builder.asc(fName));
    // -------------------------------------
    TypedQuery<VarClass> query = entityManager.createQuery(criteria);
    // -------------------------------------
    return query;
  }

  private Predicate buildWhere(CriteriaBuilder builder, Root<VarClass> root) {
    // ----------------------------------
    Path<String> fName = root.get("name");
    Predicate pred = builder.or(
        builder.like(builder.upper(fName), "CERT%"), 
        builder.equal(builder.upper(fName), "VERSION_MSS"));
    // ----------------------------------
    return pred;
  }

}
