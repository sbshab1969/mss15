package acp.db.service.impl.dbcp.all;

import acp.db.service.IManagerList;

public abstract class ManagerListDbcp<T> extends ManagerBaseDbcp implements IManagerList<T> {

  @Override
  public void openQueryAll() {
  }  

  @Override
  public void openQueryPage() {
  }  

  @Override
  public void closeQuery() {
  }

  public String prepareQueryPage(String queryStr, int startPos, int cntRows) {
    String queryPage = queryStr;
    if (startPos>0 && cntRows>0) {
      int begPos = startPos - 1;
      int endPos = begPos + cntRows;
      if (begPos == 0) {
        queryPage = "select * from (" + queryStr + ") where rownum<=" + endPos;
      } else {
        queryPage = "select * from (" +  
                    "select row1.*, rownum rownum1 from (" + queryStr + ") row1" + 
                    " where rownum<=" + endPos + ")" +
                    " where rownum1>" + begPos;
      }
    }
    System.out.println("queryPage: " + queryPage);
    return queryPage;
  }

}
