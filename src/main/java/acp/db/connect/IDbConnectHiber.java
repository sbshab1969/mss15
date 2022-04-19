package acp.db.connect;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public interface IDbConnectHiber extends IDbConnect {
  Session getCurrentSession();
  Session openSession();
  Session getSession();
  Transaction getTransaction(Session sess);
  void close(Session sess);

  EntityManager getEntityManager();
  EntityTransaction getEntityTransaction(EntityManager em);
  void close(EntityManager em);
  
  Connection getConnectionBySF(SessionFactory sFactory);
  Connection getConnectionBySession(Session sess);
  Connection getConnectionDoWork(Session sess);
  void printConnectionDoWork(Session sess);
}