/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableImage implements Transferable{
	private Image image;

	public TransferableImage(Image image){
		this.image = image;
	}

	// Returns supported flavors
	public DataFlavor[] getTransferDataFlavors(){
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	// Returns true if flavor is supported
	public boolean isDataFlavorSupported(DataFlavor flavor){
		return DataFlavor.imageFlavor.equals(flavor);
	}

	// Returns image
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{
		if (!DataFlavor.imageFlavor.equals(flavor)){
			throw new UnsupportedFlavorException(flavor);
		}
		return image;
	}
}
