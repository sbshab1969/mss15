package acp.db.service.impl.hiber.crit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
//import org.hibernate.sql.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.Main;
import acp.db.domain.ConfigClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerListCrit extends ManagerListHiber<ConfigDto> {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerListCrit.class);

  private String[] fields;
  private String[] fieldsSrc;
  
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;
  private String strAwhere;
  private String strWhere;
  
  private List<ConfigDto> cacheObj = new ArrayList<>();

  public ConfigManagerListCrit() {
    fields = new String[] { 
        "id"
      , "name"
      , "dateBegin"
      , "dateEnd"
      , "comment"
      , "owner"
    };
    
    fieldsSrc = new String[] { 
        "name"
    };
    
    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.SourceName")
      , Messages.getString("Column.DateBegin")
      , Messages.getString("Column.DateEnd")
      , Messages.getString("Column.Comment")
      , Messages.getString("Column.Owner") 
    };

    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Date.class
      , Date.class
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
  public List<ConfigDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConfigDto> fetchPage(int startPos, int cntRows) {
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
//    String aliasCfg = "cfg";
    String aliasSrc = "src";
    // -------------------------------------
    @SuppressWarnings("deprecation")
//    Criteria query = session.createCriteria(ConfigClass.class,aliasCfg);
    Criteria query = session.createCriteria(ConfigClass.class);
    query.createCriteria("source",aliasSrc); 
//    query.createCriteria("source",aliasSrc,JoinType.LEFT_OUTER_JOIN); 
    // -----------
    ProjectionList pl = Projections.projectionList();
    for (int i=0; i<fields.length; i++) {
//      pl.add(Property.forName(aliasCfg + "." + fields[i]));
      pl.add(Property.forName(fields[i]));
    }
    for (int i=0; i<fieldsSrc.length; i++) {
      pl.add(Property.forName(aliasSrc + "." + fieldsSrc[i]));
    }
    query.setProjection(pl);
    // -----------
    setQueryWhere(query);
//    query.addOrder(Order.asc(aliasCfg + "." + pkColumn));
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
    String vOwner = mapFilter.get("owner"); 
    String vSourceId = mapFilter.get("sourceId"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(msso_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      str = "upper(msso_owner) like upper('" + vOwner + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vSourceId)) {
      str = "msso_msss_id=" + vSourceId;
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
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
    String vOwner = mapFilter.get("owner"); 
    String vSourceId = mapFilter.get("sourceId"); 
    // ----------------------------------
    Conjunction conj = Restrictions.conjunction();
    if (!QueryUtils.emptyString(vName)) {
      conj.add(Restrictions.like("name", vName, MatchMode.START).ignoreCase());
    }  
    if (!QueryUtils.emptyString(vOwner)) {
      conj.add(Restrictions.like("owner", vOwner, MatchMode.START).ignoreCase());
    }  
    if (!QueryUtils.emptyString(vSourceId)) {
      Long longSourceId = Long.parseLong(vSourceId);
      conj.add(Restrictions.eq("sourceId", longSourceId));
    }
    // ----------------------------------
    List<Criterion> conditions = (List<Criterion>) conj.conditions();
    if (conditions.size() != 0) {
      crit = conj;
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
  
  private ConfigDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    Timestamp rsDateBegin = (Timestamp) obj[2];
    Timestamp rsDateEnd = (Timestamp) obj[3];
    String rsComment = (String) obj[4];
    String rsOwner = (String) obj[5];
    String rsSourceName = (String) obj[6];
    //---------------------------------------
    ConfigDto objDto = new ConfigDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    objDto.setComment(rsComment);
    objDto.setOwner(rsOwner);
    objDto.setSourceName(rsSourceName);
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
    Criteria query = session.createCriteria(ConfigClass.class);
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
