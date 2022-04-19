package acp.db.service.impl.hiber.sqljpa;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

//import org.hibernate.type.LongType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IFileLoadManagerEdit;
import acp.db.service.impl.hiber.all.ManagerBaseHiber;
import acp.forms.dto.FileLoadDto;
import acp.utils.DialogUtils;

public class FileLoadManagerEditSqlJpa extends ManagerBaseHiber implements IFileLoadManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerEditSqlJpa.class);

//  private FileLoadDto createDto(FileLoadClass objClass) {
//    FileLoadDto objDto = new FileLoadDto();
//    // ----------------------------------
//    objDto.setId(objClass.getId());    
//    objDto.setName(objClass.getName());    
//    objDto.setMd5(objClass.getMd5());    
//    objDto.setDateCreate(objClass.getDateCreate());
//    objDto.setDateWork(objClass.getDateWork());
//    objDto.setOwner(objClass.getOwner());
//    objDto.setConfigId(objClass.getConfigId());
//    if (objClass.getConfig() != null) {
//      objDto.setConfigName(objClass.getConfig().getName());
//    }
//    objDto.setRecAll(objClass.getRecAll());
//    objDto.setRecErr(objClass.getRecErr());
//    // ----------------------------------
//    ArrayList<String> statList = new ArrayList<>();
//    statList.add(String.valueOf(objClass.getRecAll()));
//    statList.add(String.valueOf(objClass.getRecErr()));
//    objDto.setStatList(statList);
//    // ----------------------------------
//    return objDto;
//  }

  private FileLoadDto createDto(Object[] objArr) {
    FileLoadDto objDto = new FileLoadDto();
    // ----------------------------------
    BigDecimal bdId = (BigDecimal) objArr[0];
    Long longId = bdId.longValue();
    objDto.setId(longId);
    
    objDto.setName((String) objArr[1]);  
    objDto.setMd5((String) objArr[2]);  
    objDto.setDateCreate((Timestamp) objArr[3]);
    objDto.setDateWork((Timestamp) objArr[4]);
    objDto.setOwner((String) objArr[5]);
    
    BigDecimal bdConf = (BigDecimal) objArr[6];
    Long longConf = bdConf.longValue();
    objDto.setConfigId(longConf);
 
    objDto.setConfigName((String) objArr[13]);

    BigDecimal bdRecAll = (BigDecimal) objArr[8];
    int intRecAll = bdRecAll.intValue();
    objDto.setRecAll(intRecAll);

    BigDecimal bdRecErr = (BigDecimal) objArr[9];
    int intRecErr = bdRecErr.intValue();
    objDto.setRecErr(intRecErr);
    // ----------------------------------
    ArrayList<String> statList = new ArrayList<>();
    statList.add(String.valueOf(intRecAll));
    statList.add(String.valueOf(intRecErr));
    objDto.setStatList(statList);
    // ----------------------------------
    return objDto;
  }

  public FileLoadDto select(Long objId) {
    FileLoadDto objDto = null; 
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();

    sbQuery.append("select mssf_id,mssf_name,mssf_md5");
    sbQuery.append(",mssf_dt_create,mssf_dt_work,mssf_owner,mssf_msso_id");
//    sbQuery.append(",fl.mssf_statistic");  // error !!!
    sbQuery.append(",fl.mssf_statistic.getStringVal() mssf_statistic");
    sbQuery.append(",mssf_rec_all,mssf_rec_er,mssf_sec_all,mssf_sec_er");

    sbQuery.append(",msso_id,msso_name,msso_dt_begin,msso_dt_end,msso_comment,msso_msss_id");
//    sbQuery.append(",cfg.msso_config");  // error !!!
    sbQuery.append(",cfg.msso_config.getStringVal() msso_config");
    sbQuery.append(",msso_dt_create,msso_dt_modify,msso_owner");
    
    sbQuery.append("  from mss_files fl");
    sbQuery.append("  left join mss_options cfg on fl.mssf_msso_id=msso_id");
    sbQuery.append(" where mssf_id=:id");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    EntityManager entityManager = dbConnect.getEntityManager();
    EntityTransaction tx = dbConnect.getEntityTransaction(entityManager);
    try {
      // ----------------------------------------
      Query query = entityManager.createNativeQuery(strQuery);
      query.setParameter("id", objId);
      // ----------------------------------------
      Object[] objArr = (Object[]) query.getSingleResult();
      objDto = createDto(objArr);
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

}
