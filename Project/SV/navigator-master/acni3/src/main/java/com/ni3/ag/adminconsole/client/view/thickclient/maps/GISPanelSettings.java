/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class GISPanelSettings{
	private Rectangle offscreenArea;
	private Rectangle visibleRect;
	private AffineTransform At;

	public GISPanelSettings(){
		offscreenArea = new Rectangle();
		visibleRect = new Rectangle();
	}

	public GISPanelSettings(GISPanelSettings set){
		offscreenArea = new Rectangle(set.offscreenArea);
		visibleRect = new Rectangle(set.visibleRect);
		if (set.At != null)
			At = new AffineTransform(set.At);
	}

	public GISPanelSettings clone(){
		return new GISPanelSettings(this);
	}

	public AffineTransform getAt(){
		return At;
	}

	public void setAt(AffineTransform affineTransform){
		this.At = affineTransform;
	}

	public Rectangle getOffscreenArea(){
		return offscreenArea;
	}

	public void setOffscreenArea(Rectangle rectangle){
		this.offscreenArea = rectangle;
	}

	public void setVisibleRect(Rectangle rectangle){
		this.visibleRect = rectangle;
    }

}
