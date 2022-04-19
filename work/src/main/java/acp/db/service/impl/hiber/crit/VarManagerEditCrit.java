package acp.db.service.impl.hiber.crit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.Main;
import acp.db.domain.VarClass;
import acp.db.service.IVarManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.db.service.impl.hiber.all.ManagerUtilSql;
import acp.forms.dto.VarDto;
import acp.utils.*;

public class VarManagerEditCrit extends ManagerBaseHiber implements IVarManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(VarManagerEditCrit.class);

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

  private void fillClassInfo(VarClass objClass, Session session) {
    Timestamp sysdt = mngUtil.getSysdate(session);
    String usr = mngUtil.getUser(session);
    // ----------------------------------
    objClass.setLen(120);
    objClass.setDateModify(sysdt);
    objClass.setOwner(usr);
    // ----------------------------------
  }

  @Override
  public VarDto select(Long objId) {
    VarDto objDto = null; 
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ----------------------------------------
      VarClass objClass = session.get(VarClass.class, objId);
      objDto= createDto(objClass);
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
  public Long insert(VarDto objDto) {
    Long objId = null;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // --------------------
      VarClass objClass = new VarClass();
      fillClassByDto(objClass, objDto);        
      fillClassInfo(objClass, session);        
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
  public boolean update(VarDto objDto) {
    boolean res = false;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // --------------------
      VarClass objClass = session.get(VarClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);        
      fillClassInfo(objClass, session);        
      session.update(objClass);
      // --------------------
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
      session.delete(session.load(VarClass.class, objId));
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

  @Override
  @SuppressWarnings("unchecked")
  public void fillVars(Map<String, String> varMap) {
    List<VarClass> objList = null;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      //----------------------------------------
      Criteria query = createQueryVars(session);
      objList = query.list();
      //----------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(session);
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

  private Criteria createQueryVars(Session session) {
    @SuppressWarnings("deprecation")
    Criteria query = session.createCriteria(VarClass.class);
    setQueryWhere(query);
    query.addOrder(Order.asc("name"));
    return query;
  }
  
  private void setQueryWhere(Criteria query) {
    Criterion crit = null;
    switch (Main.hbCrit) {
    case SQL_RESTR:
      crit = buildSqlWhere();
      break; 
    case ALL_RESTR:
      crit = buildWhere();
      break;
    }  
    if (crit != null) {
      query.add(crit);
    }  
  }

  private Criterion buildSqlWhere() {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("upper(mssv_name) like 'CERT%'");
    sbQuery.append(" or upper(mssv_name) = 'VERSION_MSS'");
    String strWhere = sbQuery.toString();
    // ------------------------------------------------------
    Criterion crit = Restrictions.sqlRestriction(strWhere);
    // ------------------------------------------------------
    return crit;
  }

  private Criterion buildWhere() {
//    Criterion crit = Restrictions.disjunction()
//        .add(Restrictions.like("name", "CERT", MatchMode.START).ignoreCase())
//        .add(Restrictions.eq("name","VERSION_MSS").ignoreCase());

    Criterion crit = Restrictions.or(
        Restrictions.like("name", "CERT", MatchMode.START).ignoreCase(),
        Restrictions.eq("name","VERSION_MSS").ignoreCase()
        );
    return crit;
  }

}
