package com.ni3.ag.navigator.server.sync;

import java.io.IOException;

public interface SyncProgressListener{

	void error() throws IOException;

	void start(String string, int size) throws IOException;

	void stop() throws IOException;
}
