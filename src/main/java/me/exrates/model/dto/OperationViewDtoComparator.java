package me.exrates.model.dto;

import java.util.Comparator;

public class OperationViewDtoComparator implements Comparator<OperationViewDto>{

	@Override
	public int compare(OperationViewDto one, OperationViewDto two) {
		return two.getDatetime().compareTo(one.getDatetime());
	}

}
