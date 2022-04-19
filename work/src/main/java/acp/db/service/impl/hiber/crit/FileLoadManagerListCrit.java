package acp.db.service.impl.hiber.crit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.Main;
import acp.db.domain.FileLoadClass;
import acp.db.service.impl.hiber.all.ManagerListHiber;
import acp.forms.dto.FileLoadDto;
import acp.utils.*;

public class FileLoadManagerListCrit extends ManagerListHiber<FileLoadDto> {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerListCrit.class);

  private String[] fields;
  private String[] headers;
  private Class<?>[] types;
  
  private String pkColumn;
  private Long seqId;

  private Map<String,String> mapFilter;
  private String strAwhere;
  private String strWhere;

  private List<FileLoadDto> cacheObj = new ArrayList<>();

  public FileLoadManagerListCrit() {
    fields = new String[] { "id", "name", "md5", "owner", "dateWork", "recAll" };

    headers = new String[] { 
        "ID"
      , Messages.getString("Column.FileName")
      , "MD5"
      , Messages.getString("Column.Owner")
      , Messages.getString("Column.DateWork")
      , Messages.getString("Column.RecordCount") 
    };
    
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , String.class
      , Timestamp.class
      , int.class
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
  public List<FileLoadDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<FileLoadDto> fetchPage(int startPos, int cntRows) {
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
    Criteria query = session.createCriteria(FileLoadClass.class);
    // -----------
    ProjectionList pl = Projections.projectionList();
    for (int i=0; i<fields.length; i++) {
      pl.add(Property.forName(fields[i]));
    }
    query.setProjection(pl);
    // -----------
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
    String vOwner = mapFilter.get("owner");
    String vDateWorkBeg = mapFilter.get("dateWorkBeg");
    String vDateWorkEnd = mapFilter.get("dateWorkEnd");
    String vRecAllBeg = mapFilter.get("recAllBeg");
    String vRecAllEnd = mapFilter.get("recAllEnd");
    // ----------------------------------
    String phWhere = null;
    String str = null;
    String vField = null;
    // ---
    if (!QueryUtils.emptyString(vName)) {
      str = "upper(mssf_name) like upper('" + vName + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      str = "upper(mssf_owner) like upper('" + vOwner + "%')";
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    //---
    vField = "mssf_dt_work";
    String dtBeg = null;
    String dtEnd = null;
    if (!QueryUtils.emptyString(vDateWorkBeg)) {
      dtBeg = "to_date('" + vDateWorkBeg + "','dd.mm.yyyy')";
    }
    if (!QueryUtils.emptyString(vDateWorkEnd)) {
      dtEnd = "to_date('" + vDateWorkEnd + " 23:59:59" + "','dd.mm.yyyy hh24:mi:ss')";
    }
    str = null;
    if (!QueryUtils.emptyString(dtBeg) && !QueryUtils.emptyString(dtEnd)) {
      str = vField + " between " + dtBeg + " and " + dtEnd;
    } else if (!QueryUtils.emptyString(dtBeg) && QueryUtils.emptyString(dtEnd)) {
      str = vField + " >= " + dtBeg;
    } else if (QueryUtils.emptyString(dtBeg) && !QueryUtils.emptyString(dtEnd)) {
      str = vField + " <= " + dtEnd;
    }
    if (!QueryUtils.emptyString(str)) {
      phWhere = QueryUtils.strAddAnd(phWhere, str);
    }
    //---
    vField = "mssf_rec_all";
    String intBeg = null;
    String intEnd = null;
    if (!QueryUtils.emptyString(vRecAllBeg)) {
      intBeg = vRecAllBeg;
    }
    if (!QueryUtils.emptyString(vRecAllEnd)) {
      intEnd = vRecAllEnd;
    }
    str = null;
    if (!QueryUtils.emptyString(intBeg) && !QueryUtils.emptyString(intEnd)) {
      str = vField + " between " + intBeg + " and " + intEnd;
    } else if (!QueryUtils.emptyString(intBeg) && QueryUtils.emptyString(intEnd)) {
      str = vField + " >= " + intBeg;
    } else if (QueryUtils.emptyString(intBeg) && !QueryUtils.emptyString(intEnd)) {
      str = vField + " <= " + intEnd;
    }
    if (!QueryUtils.emptyString(str)) {
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
    String vDateWorkBeg = mapFilter.get("dateWorkBeg");
    String vDateWorkEnd = mapFilter.get("dateWorkEnd");
    String vRecAllBeg = mapFilter.get("recAllBeg");
    String vRecAllEnd = mapFilter.get("recAllEnd");
    // ----------------------------------
    String vField;
    Conjunction conj = Restrictions.conjunction();
    // ---
    if (!QueryUtils.emptyString(vName)) {
      conj.add(Restrictions.like("name", vName, MatchMode.START).ignoreCase());
    }
    // ---
    if (!QueryUtils.emptyString(vOwner)) {
      conj.add(Restrictions.like("owner", vOwner, MatchMode.START).ignoreCase());
    }
    //---
    vField = "dateWork";
    Date dtBeg = null;
    Date dtEnd = null;
    if (!QueryUtils.emptyString(vDateWorkBeg)) {
      dtBeg = DateUtils.str2Date(vDateWorkBeg);
    }
    if (!QueryUtils.emptyString(vDateWorkEnd)) {
      dtEnd = DateUtils.str2DateTime(vDateWorkEnd + " 23:59:59");
    }
    if (dtBeg != null && dtEnd != null) {
      conj.add(Restrictions.between(vField, dtBeg, dtEnd));
    } else if (dtBeg != null && dtEnd == null) {
      conj.add(Restrictions.ge(vField, dtBeg));
    } else if (dtBeg == null && dtEnd != null) {
      conj.add(Restrictions.le(vField, dtEnd));
    }
    //---
    vField = "recAll";
    Integer intBeg = null;
    Integer intEnd = null;
    if (!QueryUtils.emptyString(vRecAllBeg)) {
      intBeg = Integer.valueOf(vRecAllBeg);
    }
    if (!QueryUtils.emptyString(vRecAllEnd)) {
      intEnd = Integer.valueOf(vRecAllEnd);
    }
    if (intBeg != null && intEnd != null) {
      conj.add(Restrictions.between(vField, intBeg, intEnd));
    } else if (intBeg != null && intEnd == null) {
      conj.add(Restrictions.ge(vField, intBeg));
    } else if (intBeg == null && intEnd != null) {
      conj.add(Restrictions.le(vField, intEnd));
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
  
  private FileLoadDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    String rsMd5 = (String) obj[2];
    String rsOwner = (String) obj[3];
    Timestamp rsDateWork = (Timestamp) obj[4];
    Integer rsRecAll = (Integer) obj[5];
    //---------------------------------------
    FileLoadDto objDto = new FileLoadDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setMd5(rsMd5);
    objDto.setOwner(rsOwner);
    objDto.setDateWork(rsDateWork);
    objDto.setRecAll(rsRecAll);
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
    Criteria query = session.createCriteria(FileLoadClass.class);
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