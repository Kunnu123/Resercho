package com.resercho.Adapters;

import com.resercho.POJO.ModelSuggested;

import java.util.List;

public class PeopleListHolder {
    List<ModelSuggested> list;

    public PeopleListHolder() {
    }

    public PeopleListHolder(List<ModelSuggested> list) {
        this.list = list;
    }

    public List<ModelSuggested> getList() {
        return list;
    }

    public void setList(List<ModelSuggested> list) {
        this.list = list;
    }
}
