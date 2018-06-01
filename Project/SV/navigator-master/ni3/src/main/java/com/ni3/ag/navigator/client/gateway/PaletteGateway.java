package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.shared.proto.NResponse;

public interface PaletteGateway{
	NResponse.Palette getPalette(int paletteID);
}
