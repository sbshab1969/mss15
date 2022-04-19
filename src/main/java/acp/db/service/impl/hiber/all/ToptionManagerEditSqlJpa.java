package acp.db.service.impl.hiber.all;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IToptionManagerEdit;
import acp.forms.dto.ToptionDto;
import acp.utils.DialogUtils;

public class ToptionManagerEditSqlJpa extends ManagerBaseHiber implements IToptionManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ToptionManagerEditSqlJpa.class);

  private String path;
  private ArrayList<String> attrs;
  private String attrPrefix;

  private int attrSize;
  private int attrMax = 5;

  public ToptionManagerEditSqlJpa(String path, ArrayList<String> attrs) {
    this.path = path;
    this.attrs = attrs;
    this.attrSize = attrs.size();
    String[] pathArray = path.split("/");
    this.attrPrefix = pathArray[pathArray.length - 1];
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public ArrayList<String> getAttrs() {
    return attrs;
  }

  @Override
  public String getAttrPrefix() {
    return attrPrefix;
  }

  @Override
  public ToptionDto select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.* from table(mss.spr_option_id(?,?,?,?,?,?,?)) t");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    ToptionDto toptObj = null;
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
//    int j = 0;  
    int j = 1;  // !!!!!!!!!
    try {
      // --------------------
      Query query = entityManager.createNativeQuery(strQuery);
      // --------------------
//      query.setParameter( j, objId, LongType.INSTANCE);
      query.setParameter( j, objId);
      j++;
//      query.setParameter( j, path, StringType.INSTANCE);
      query.setParameter( j, path);
      // ----------------------------------
      for (int i = 0; i < attrSize; i++) {
        j++;
        query.setParameter( j, attrs.get(i));
      }
      for (int i = attrSize; i < attrMax; i++) {
        j++;
        query.setParameter( j, "");
      }
      // --------------------
      Object[] obj = (Object[]) query.getSingleResult();
      toptObj = getObject(obj);       
      // --------------------
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      dbConnect.close(entityManager);
    }  
    // ------------------------------------------------------
    return toptObj;
  }

  private ToptionDto getObject(Object[] obj) {
    //---------------------------------------
    BigDecimal idBigDec = (BigDecimal) obj[0];
    Long rsId = idBigDec.longValue();
    // ----------------------
    int j = 0;
    ArrayList<String> pArr = new ArrayList<>();
    for (int i = 0; i < attrSize; i++) {
      j = i + 1;
      String pj = (String) obj[j];
      pArr.add(pj);
    }
    // ----------------------
    Date rsDateBegin = (Date) obj[++j];
    Date rsDateEnd = (Date) obj[++j];
    //---------------------------------------
    ToptionDto objDto = new ToptionDto();
    objDto.setId(rsId);
    objDto.setArrayP(pArr);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    //---------------------------------------
    return objDto;
  }

  @Override
  public boolean update(ToptionDto objOld, ToptionDto objNew) {
    boolean res = false;
    Long objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
    // -----------------------------------------
    String where = "";
    for (int i = 0; i < attrSize; i++) {
      if (recOldValue.get(i) != null) {
        where += "[@" + attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]";
      }
    }
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options set ");
    sbQuery.append("msso_config=updateXml(msso_config");
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        String param = path + where + "/@" + attrs.get(i);
        sbQuery.append(",'" + param + "',?");
      }
    }
    sbQuery.append(") where msso_id=?");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      Query query = entityManager.createNativeQuery(strQuery);
//      int j = 0;
      int j = 1; // !!! Hibernate 5.2 !!!
      for (int i = 0; i < attrSize; i++) {
        if (!recNewValue.get(i).equals("")) {
          query.setParameter( j++, recNewValue.get(i));
        }
      }
      query.setParameter( j, objId);
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
      dbConnect.close(entityManager);
    }  
    // -----------------------------------------------------
    return res;
  }

}
