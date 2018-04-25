/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway;

import java.awt.Image;
import java.util.List;

public interface IconGateway{

	List<String> getIconNames();

	Image loadImage(String name);
}
