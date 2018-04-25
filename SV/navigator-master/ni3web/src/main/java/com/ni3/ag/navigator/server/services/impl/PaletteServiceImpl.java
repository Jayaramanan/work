package com.ni3.ag.navigator.server.services.impl;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.PaletteDAO;
import com.ni3.ag.navigator.server.domain.Palette;
import com.ni3.ag.navigator.server.services.PaletteService;
import java.util.List;

public class PaletteServiceImpl implements PaletteService{
	@Override
	public List<Palette> getPalettes(int id){
		PaletteDAO paletteDAO = NSpringFactory.getInstance().getPaletteDAO();
		return paletteDAO.getPalette(id);
	}
}
