package com.cisdi.cpm.auth.bean;

import com.cisdi.cpm.auth.common.contant.Const;

public class DataQueryBean extends DataRoot{
    //�����ؼ���
    public void setKeyword(String keyword) {
        dataMap.put("keyword", keyword);
    }
    public String getKeyword() {
        return (String) dataMap.get("keyword");
    }

    //��ǰҳ
    public void setPage(int page) {
        dataMap.put("page", page);
    }
    public Integer getPage() {
        return (Integer) (dataMap.get("page") == null ? 1 : dataMap.get("page"));
    }

    //ÿҳ�ļ�¼��
    public void setPageSize(int pageSize) {
        dataMap.put("pageSize", pageSize);
    }
    public Integer getPageSize() {
        return (Integer) (dataMap.get("pageSize") == null ? Const.DEFAULT_PAGESIZE : dataMap.get("pageSize"));
    }
}
