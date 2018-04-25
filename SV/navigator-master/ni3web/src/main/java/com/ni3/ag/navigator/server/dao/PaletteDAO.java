package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.Palette;

public interface PaletteDAO{
	List<Palette> getPalette(int id);
}
