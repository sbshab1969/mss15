package acp.db.service.impl.hiber.hql;

import java.sql.Timestamp;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
//import org.hibernate.type.LongType;
//import org.hibernate.type.StringType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.domain.ConfigClass;
import acp.db.service.IConfigManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.db.service.impl.hiber.all.ManagerUtilSql;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerEditHql extends ManagerBaseHiber implements IConfigManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerEditHql.class);

  private ManagerUtilSql mngUtil = new ManagerUtilSql();
  
  private ConfigDto createDto(ConfigClass objClass) {
    ConfigDto objDto = new ConfigDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    // objDto.setConfig(objClass.getConfigR());  // R !!!!
    objDto.setDateBegin(objClass.getDateBegin());    
    objDto.setDateEnd(objClass.getDateEnd());    
    objDto.setComment(objClass.getComment());    
    // objDto.setOwner(objClass.getOwner());    
    objDto.setSourceId(objClass.getSourceId());    
    // objDto.setSourceName(null);    
    // ----------------------------------
    return objDto;
  }

  private void fillClassByDto(ConfigClass objClass, ConfigDto objDto) {
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    // ----------------------------------
    objClass.setName(objDto.getName());    
    if (objClass.getId() == null) {
      objClass.setConfigW(emptyXml);
    } else {
      objClass.setConfigW(objClass.getConfigR());   // !!! R -> W
    }
    objClass.setDateBegin(objDto.getDateBegin());    
    objClass.setDateEnd(objDto.getDateEnd());    
    objClass.setComment(objDto.getComment());    
    objClass.setSourceId(objDto.getSourceId());    
    // ----------------------------------
  }
  
  private void fillClassInfo(ConfigClass objClass, Session session) {
    Timestamp sysdt = mngUtil.getSysdate(session);
    String usr = mngUtil.getUser(session);
    // ----------------------------------
    if (objClass.getId() == null) {
      objClass.setDateCreate(sysdt);
    }
    objClass.setDateModify(sysdt);
    objClass.setOwner(usr);
    // ----------------------------------
  }

  @Override
  public ConfigDto select(Long objId) {
    ConfigDto objDto = null; 
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ----------------------------------------
      ConfigClass objClass = session.get(ConfigClass.class, objId);
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
  public String getCfgName(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select name from ConfigClass where id=:id");
    String strQuery = sbQuery.toString();
    String configName = "";
    // ------------------------------------------------------
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      Query<?> query = session.createQuery(strQuery);
//      query.setParameter("id", objId, LongType.INSTANCE);
      query.setParameter("id", objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      configName = (String) query.uniqueResult();
      // --------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      configName = "";
    } finally {
      dbConnect.close(session);
    }
    // ------------------------------------------------------
    return configName;
  }

  @Override
  public String getCfgStr(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select configR from ConfigClass where id=:id");
    String strQuery = sbQuery.toString();
    String configStr = null;
    // ------------------------------------------------------
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      Query<?> query = session.createQuery(strQuery);
//      query.setParameter("id", objId, LongType.INSTANCE);
      query.setParameter("id", objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      configStr = (String) query.uniqueResult();
      // --------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      configStr = null;
    } finally {
      dbConnect.close(session);
    }
    // ------------------------------------------------------
    return configStr;
  }
 
  @Override
  public Long insert(ConfigDto objDto) {
    Long objId = null;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ---------------------------------
      ConfigClass objClass = new ConfigClass();
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
  public boolean update(ConfigDto objDto) {
    boolean res = false;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // --------------------
      ConfigClass objClass = session.get(ConfigClass.class, objDto.getId());
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
  public boolean updateCfgStr(Long objId, String txtConf) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    //sbQuery.append(" set msso_config=:conf"); // OK
    sbQuery.append("   set msso_config=XMLType(:conf)");  // OK
    sbQuery.append(", msso_dt_modify=sysdate");
    sbQuery.append(", msso_owner=user");
    sbQuery.append(" where msso_id=:id");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      NativeQuery<?> query = session.createNativeQuery(strQuery);
//      query.setParameter("id", objId, LongType.INSTANCE);
//      query.setParameter("conf", txtConf, StringType.INSTANCE);
      query.setParameter("id", objId);
      query.setParameter("conf", txtConf);
     logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      query.executeUpdate();
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
    // -----------------------------------------------------
    return res;
  }

  @Override
  public boolean delete(Long objId) {
    boolean res = false;
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // ---------------------------------------------------
      session.delete(session.load(ConfigClass.class, objId));
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
  public boolean copy(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery.append(" (select msso_seq.nextval, msso_name || '_copy'");
    sbQuery.append(", msso_config");
    sbQuery.append(", msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(", sysdate, sysdate, user, msso_msss_id");
    sbQuery.append(" from mss_options where msso_id=:id)");
    String strQuery = sbQuery.toString();
    // -----------------------------------------------------
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      NativeQuery<?> query = session.createNativeQuery(strQuery);
//      query.setParameter("id", objId, LongType.INSTANCE);
      query.setParameter("id", objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      query.executeUpdate();
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
    // -----------------------------------------------------
    return res;
  }

}
