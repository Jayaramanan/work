/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SeqRangeTableModel extends ACTableModel{

	private static final long serialVersionUID = 5253216455086077105L;
	private List<UserSequenceState> sequenceRanges;

	public SeqRangeTableModel(){
		addColumn(Translation.get(TextID.SequenceName), false, String.class, false);
		addColumn(Translation.get(TextID.IntervalStart), false, Integer.class, false);
		addColumn(Translation.get(TextID.IntervalEnd), false, Integer.class, false);
		addColumn(Translation.get(TextID.CurrentValue), false, Integer.class, false);
		addColumn(Translation.get(TextID.AvalibleCount), false, Integer.class, false);
		sequenceRanges = new ArrayList<UserSequenceState>();
	}

	public SeqRangeTableModel(List<UserSequenceState> states){
		this();
		this.sequenceRanges = states;
	}

	@Override
	public int getRowCount(){
		return sequenceRanges.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		UserSequenceState uss = sequenceRanges.get(rowIndex);
		switch (columnIndex){
			case 0:
				return uss.getSequenceName();
			case 1:
				return uss.getIntervalStart();
			case 2:
				return uss.getIntervalEnd();
			case 3:
				return uss.getCurrent();
			case 4:
				return uss.getIntervalEnd() - uss.getCurrent();
		}
		return null;
	}

	@Override
	public String getToolTip(int row, int column){
		return Translation.get(TextID.ReadonlyFilledAutomatically);
	}
}
