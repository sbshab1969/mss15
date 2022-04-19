package acp.db.service.impl.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;

import java.util.ArrayList;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.service.IFileLoadManagerEdit;
import acp.db.service.impl.dbutil.all.ManagerBaseDbutil;
import acp.forms.dto.FileLoadDto;
import acp.utils.*;

public class FileLoadManagerEditDbutil extends ManagerBaseDbutil implements IFileLoadManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerEditDbutil.class);
 
  private FileLoadDto createDto(Object[] objArr) {
    FileLoadDto objDto = new FileLoadDto();
    // ----------------------------------
    BigDecimal bdId = (BigDecimal) objArr[0];
    Long longId = bdId.longValue();
    objDto.setId(longId);
    
    objDto.setName((String) objArr[1]);  
    objDto.setMd5((String) objArr[2]);  
    objDto.setOwner((String) objArr[3]);
    objDto.setDateCreate((Timestamp) objArr[4]);
    objDto.setDateWork((Timestamp) objArr[5]);
    
    BigDecimal bdConf = (BigDecimal) objArr[6];
    Long longConf = bdConf.longValue();
    objDto.setConfigId(longConf);
 
    objDto.setConfigName((String) objArr[7]);

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

  @Override
  public FileLoadDto select(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
//    sbQuery.append("select * from mss_files,mss_options"); // Error 
    sbQuery.append("select mssf_id,mssf_name,mssf_md5,mssf_owner");
    sbQuery.append(",mssf_dt_create,mssf_dt_work");
    sbQuery.append(",mssf_msso_id,msso_name");
    sbQuery.append(",mssf_rec_all records_all, mssf_rec_er records_err");
//    sbQuery.append(",extract(mssf_statistic,'statistic/records/all/text()').getStringVal() records_all");
//    sbQuery.append(",extract(mssf_statistic,'statistic/records/error/text()').getStringVal() records_err");
    sbQuery.append("  from mss_files,mss_options");
    sbQuery.append(" where mssf_msso_id=msso_id");
    sbQuery.append("   and mssf_id=?");
    String strQuery = sbQuery.toString();
    System.out.println("select (edit): " + strQuery);
    // ------------------------------------------------------
    FileLoadDto filesObj = null;
    try {
      Connection conn = dbConnect.getConnection();
      ResultSetHandler<Object[]> handler = new ArrayHandler();
      Object[] result = queryRunner.query(conn,strQuery,handler,objId);
      filesObj = createDto(result);
      dbConnect.close(conn);
    } catch (Exception e) {
      DialogUtils.errorPrint(e, logger);
      filesObj = null;
    }
    // ------------------------------------------------------
    return filesObj;
  }

}
