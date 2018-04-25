package com.ni3.ag.navigator.client.domain;

import java.util.Comparator;

public class AttributeComparator implements Comparator<Attribute>{
	public enum SortMode{
		SORT, SORT_FILTER, SORT_LABEL, SORT_SEARCH, SORT_DISPLAY, SORT_MATRIX
	}

	private SortMode sortMode;

	public AttributeComparator(SortMode mode){
		this.sortMode = mode;
	}

	@Override
	public int compare(Attribute attr1, Attribute attr2){
		int s1 = 0, s2 = 0;
		int ss1 = 0, ss2 = 0;

		switch (sortMode){
			case SORT:
				s1 = attr1.getSort();
				s2 = attr2.getSort();
				break;

			case SORT_FILTER:
				s1 = attr1.getSortFilter();
				s2 = attr2.getSortFilter();
				break;

			case SORT_LABEL:
				s1 = attr1.getSortLabel();
				s2 = attr2.getSortLabel();
				break;

			case SORT_SEARCH:
				s1 = attr1.getSortSearch();
				s2 = attr2.getSortSearch();
				break;

			case SORT_DISPLAY:
				s1 = attr1.getSortDisplay();
				s2 = attr2.getSortDisplay();
				break;

			case SORT_MATRIX:
				ss1 = attr1.getSortMatrix();
				ss2 = attr2.getSortMatrix();
				s1 = attr1.getInMatrix();
				s2 = attr2.getInMatrix();

				break;
		}

		if (s1 < s2)
			return -1;

		if (s1 > s2)
			return 1;

		if (ss1 < ss2)
			return -1;

		if (ss1 > ss2)
			return 1;

		return 0;
	}

}