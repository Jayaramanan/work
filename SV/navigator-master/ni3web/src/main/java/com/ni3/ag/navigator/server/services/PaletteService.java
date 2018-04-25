package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.Palette;

import java.util.List;

public interface PaletteService{
	List<Palette> getPalettes(int id);
}
