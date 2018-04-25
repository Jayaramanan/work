/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
class ImageSelection extends TransferHandler implements Transferable{

	private final DataFlavor flavors[] = { DataFlavor.imageFlavor };

	private Image image;

	public int getSourceActions(JComponent c){
		return TransferHandler.COPY;
	}

	public boolean canImport(JComponent comp, DataFlavor flavor[]){
		return false;
	}

	public Transferable createTransferable(JComponent comp){
		// Clear
		image = null;

		if (comp instanceof GisPanel){
			GisPanel gis = (GisPanel) comp;
			if (gis.offscreenGraph != null){
				image = gis.offscreenGraph;
				return this;
			}
		}

		return null;
	}

	public boolean importData(JComponent comp, Transferable t){
		return false;
	}

	// Transferable
	public Object getTransferData(DataFlavor flavor){
		if (isDataFlavorSupported(flavor)){
			return image;
		}
		return null;
	}

	public DataFlavor[] getTransferDataFlavors(){
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor){
		return flavor.equals(DataFlavor.imageFlavor);
	}
}
