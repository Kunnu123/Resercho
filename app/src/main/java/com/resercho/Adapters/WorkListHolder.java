package com.resercho.Adapters;

import com.resercho.POJO.ModelWork;

import java.util.List;

public class WorkListHolder {
    List<ModelWork> list;

    public WorkListHolder(List<ModelWork> list) {
        this.list = list;
    }

    public WorkListHolder() {
    }

    public List<ModelWork> getList() {
        return list;
    }

    public void setList(List<ModelWork> list) {
        this.list = list;
    }
}
