package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.DeltaHeader;
import java.util.List;

public interface DeltaProcessor{

	void processDeltas(List<DeltaHeader> deltas, boolean onOffline);

}
