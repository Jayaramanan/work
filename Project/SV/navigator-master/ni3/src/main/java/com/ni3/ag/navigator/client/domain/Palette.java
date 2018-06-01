/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import com.ni3.ag.navigator.client.gateway.PaletteGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpPaletteGatewayImpl;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.proto.NResponse;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

public class Palette{
	List<Integer> Sequence;
	List<Color> colors;

	int CurrSequence, CurrColor;

	public Palette(int PaletteID){
		colors = new ArrayList<Color>();
		Sequence = new ArrayList<Integer>();

		PaletteGateway paletteGateway = new HttpPaletteGatewayImpl();
		NResponse.Palette palette = paletteGateway.getPalette(PaletteID);
		List<NResponse.Color> protoColors = palette.getColorsList();

		int PrevSeq = -1;
		for (NResponse.Color c : protoColors){
			if (c.getSequence() != PrevSeq){
				PrevSeq = c.getSequence();
				Sequence.add(colors.size());
			}

			colors.add(Utility.createColor(c.getColor()));
		}

		Sequence.add(colors.size());
		CurrSequence = 0;
		CurrColor = 0;
	}

	public void nextSequence(){
		CurrSequence++;
		if (CurrSequence >= Sequence.size())
			CurrSequence = 0;

		CurrColor = Sequence.get(CurrSequence);
	}

	public void resetPalette(){
		CurrSequence = CurrColor = 0;
	}

	public Color nextColor(){
		if (CurrColor == colors.size())
			CurrColor = 0;
		return colors.get(CurrColor++);
	}

	public int getColorCount(){
		return colors.size();
	}
}
