package acp.db.service.impl.hiber.all;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.utils.*;

public class ManagerUtilSql extends ManagerBaseHiber {
  private static Logger logger = LoggerFactory.getLogger(ManagerUtilSql.class);

// Session
  
  public Long getValueL(String strQuery, Session session) {
    NativeQuery<?> query = session.createNativeQuery(strQuery);
    BigDecimal idBigDec = (BigDecimal) query.uniqueResult();
    Long val = idBigDec.longValue();
    return val;
  }

  public String getValueV(String strQuery, Session session) {
    NativeQuery<?> query = session.createNativeQuery(strQuery);
    String val = (String) query.uniqueResult();
    return val;
  }

  public Timestamp getValueT(String strQuery, Session session) {
    NativeQuery<?> query = session.createNativeQuery(strQuery);
    Timestamp val = (Timestamp) query.uniqueResult();
    return val;
  }

// EntityManager

//  public Long getValueL(String strQuery, EntityManager entityManager) {
//    Session session = entityManager.unwrap(Session.class);
//    Long val = getValueL(strQuery, session);
//    return val;
//  }
//
//  public String getValueV(String strQuery, EntityManager entityManager) {
//    Session session = entityManager.unwrap(Session.class);
//    String val = getValueV(strQuery, session);
//    return val;
//  }
//
//  public Timestamp getValueT(String strQuery, EntityManager entityManager) {
//    Session session = entityManager.unwrap(Session.class);
//    Timestamp val = (Timestamp) getValueT(strQuery, session);
//    return val;
//  }

  public Long getValueL(String strQuery, EntityManager entityManager) {
    Query query = entityManager.createNativeQuery(strQuery);
    BigDecimal idBigDec = (BigDecimal) query.getSingleResult();
    Long val = idBigDec.longValue();
    return val;
  }

  public String getValueV(String strQuery, EntityManager entityManager) {
    Query query = entityManager.createNativeQuery(strQuery);
    String val = (String) query.getSingleResult();
    return val;
  }

  public Timestamp getValueT(String strQuery, EntityManager entityManager) {
    Query query = entityManager.createNativeQuery(strQuery);
    Timestamp val = (Timestamp) query.getSingleResult();
    return val;
  }
  
// NULL

  public Long getValueL(String strQuery) {
    Long val = null;
    Session session = dbConnect.openSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      val = getValueL(strQuery, session);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e, logger);
      val = null;
    }
    session.close();
    return val;
  }

  public String getValueV(String strQuery) {
    String val = null;
    Session session = dbConnect.openSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      val = getValueV(strQuery, session);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e, logger);
      val = null;
    }
    session.close();
    return val;
  }

  public Timestamp getValueT(String strQuery) {
    Timestamp val = null;
    Session session = dbConnect.openSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      val = getValueT(strQuery, session);
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e, logger);
      val = null;
    }
    session.close();
    return val;
  }

// getUser
  
  public String getUser(Session session) {
    String usr = getValueV("select user from dual", session);
    return usr;
  }

  public String getUser(EntityManager entityManager) {
    String usr = getValueV("select user from dual", entityManager);
    return usr;
  }

  public String getUser() {
    String usr = getValueV("select user from dual");
    return usr;
  }

// getSysdate

  public Timestamp getSysdate(Session session) {
    Timestamp tst = getValueT("select sysdate from dual", session);
    return tst;
  }

  public Timestamp getSysdate(EntityManager entityManager) {
    Timestamp tst = getValueT("select sysdate from dual", entityManager);
    return tst;
  }

  public Timestamp getSysdate() {
    Timestamp tst = getValueT("select sysdate from dual");
    return tst;
  }

}
