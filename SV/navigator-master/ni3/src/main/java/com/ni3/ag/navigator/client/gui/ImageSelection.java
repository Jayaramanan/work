/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.ni3.ag.navigator.client.gui.graph.GraphPanel;

@SuppressWarnings("serial")
public class ImageSelection extends TransferHandler implements Transferable{

	private final DataFlavor flavors[] = { DataFlavor.imageFlavor };

	private Image image;

	public int getSourceActions(JComponent c){
		return TransferHandler.COPY;
	}

	public boolean canImport(JComponent comp, DataFlavor flavor[]){
		return false;
	}

	@Override
	public Transferable createTransferable(JComponent comp){
		// Clear
		image = null;

		if (comp instanceof GraphPanel){
			GraphPanel graph = (GraphPanel) comp;
			//TODO if copies whole offsreen image (even not visible part of it, which is out of drawing area of panel)
			if (graph.getOffscreenImage() != null){
				image = graph.getOffscreenImage();
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
