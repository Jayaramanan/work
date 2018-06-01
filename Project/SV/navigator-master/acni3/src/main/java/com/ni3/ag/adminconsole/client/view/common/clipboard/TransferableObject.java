/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableObject implements Transferable, ClipboardOwner{

	private static DataFlavor objectDataFlavor = new DataFlavor(ClipboardObject.class, "objectFlavor");
	private ClipboardObject object;

	public TransferableObject(ClipboardObject object){
		this.object = object;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{
		if (!isDataFlavorSupported(flavor)){
			throw new UnsupportedFlavorException(flavor);
		}
		return object;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors(){
		return new DataFlavor[] { objectDataFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor){
		return objectDataFlavor.equals(flavor);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents){
	}

	public static DataFlavor getObjectDataFlavor(){
	    return objectDataFlavor;
    }

}
