package com.ls.tc.speech.controller.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ListResponseContainer<T extends java.io.Serializable> implements java.io.Serializable {

	private static final long serialVersionUID = 1014589627994932215L;

	private int totalRecords;
	private List<T> entries = new ArrayList<>();

	public ListResponseContainer() {
		// noop
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public List<T> getEntries() {
		return entries;
	}

	public void setEntries(List<T> entries) {
		this.entries = entries;
	}
}
