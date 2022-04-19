package acp.db.service.impl.hiber.crit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.Main;
import acp.db.domain.ConstClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.ConstDto;
import acp.utils.DialogUtils;
import acp.utils.Messages;
import acp.utils.QueryUtils;

public class ConstManagerListCrit extends ManagerListHiber<ConstDto> {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerListCrit.class);

  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;
  private String strAwhere;
  private String strWhere;

  private List<ConstDto> cacheObj = new ArrayList<>();

  public ConstManagerListCrit() {
    fields = new String[] { "id", "name", "value" };

    headers = new String[] {
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Value") 
    };

    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
    };
    
    pkColumn = fields[0];
    seqId = 1000L;

    strAwhere = null;
    strWhere = strAwhere;

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
  public List<ConstDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConstDto> fetchPage(int startPos, int cntRows) {
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try { 
      Criteria query = createQuery(session);
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
      dbConnect.close(session);
    }  
    return cacheObj;    
  }  

  private Criteria createQuery(Session session) {
    // -------------------------------------
    @SuppressWarnings("deprecation")
    Criteria query = session.createCriteria(ConstClass.class);
    // -------------------------------------
    ProjectionList pl = Projections.projectionList();
    for (int i=0; i<fields.length; i++) {
      pl.add(Property.forName(fields[i]));
    }
    query.setProjection(pl);
    // -------------------------------------
    setQueryWhere(query);
    query.addOrder(Order.asc(pkColumn));
    // -------------------------------------
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
    Criterion crit = null;
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(mssc_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    strWhere = QueryUtils.strAddAnd(strAwhere, phWhere);
    // ---------------------------------------------------
    if (!QueryUtils.emptyString(strWhere)) {
      crit = Restrictions.sqlRestriction(strWhere);
    }  
    // ---------------------------------------------------
    return crit;
  }

  private Criterion buildWhere() {
    Criterion crit = null;
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    // ----------------------------------
    if (!QueryUtils.emptyString(vName)) {
      crit = Restrictions.like("name", vName, MatchMode.START).ignoreCase();
//      crit = Property.forName("name").like(vName, MatchMode.START).ignoreCase();
    }  
    // ----------------------------------
    return crit;
  }
  
  private void fillCache(Criteria query) {
    // ============================
    List<?> objList = query.list();
    // ============================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private ConstDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    String rsValue = (String) obj[2];
    //---------------------------------------
    ConstDto objDto = new ConstDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setValue(rsValue);
    //---------------------------------------
    return objDto;
  }

  @Override
  public long countRecords() {
    long cntRecords = 0; 
    Session session = dbConnect.getSession();
    Transaction tx = dbConnect.getTransaction(session);
    try {
      // -------------------------------------
      Criteria query = createQueryCnt(session);
      cntRecords = (Long) query.uniqueResult();
      // -------------------------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(session);
    }  
    return cntRecords;    
  }

  private Criteria createQueryCnt(Session session) {
    //--------------------------------------------------
    @SuppressWarnings("deprecation")
    Criteria query = session.createCriteria(ConstClass.class);
    //--------------------------------------------------
    ProjectionList pl = Projections.projectionList();
    pl.add(Projections.rowCount());
    query.setProjection(pl);
    // ------------------
    setQueryWhere(query);
    //--------------------------------------------------
    return query;
  }

}
