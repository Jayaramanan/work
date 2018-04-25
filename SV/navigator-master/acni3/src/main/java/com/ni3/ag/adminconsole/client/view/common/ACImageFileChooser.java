/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

public class ACImageFileChooser extends JFileChooser{

	private static final long serialVersionUID = 7721687029059010543L;

	public ACImageFileChooser(){
		super();
		setFileSelectionMode(JFileChooser.FILES_ONLY);
		addChoosableFileFilter(new ImageFilter());
		setAcceptAllFileFilterUsed(false);
		setMultiSelectionEnabled(true);
		setFileView(new ThumbNailFileView(this));
	}

	class ThumbNailFileView extends FileView{

		private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
		private Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
		private Icon computerIcon = UIManager.getIcon("FileView.computerIcon");
		private Icon hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");

		private String fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText");
		private String directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText");

		private JFileChooser chooser;
		private FileSystemView fsv;

		public ThumbNailFileView(JFileChooser c){
			chooser = c;
			fsv = chooser.getFileSystemView();
		}

		public String getDescription(File f){
			return getTypeDescription(f);
		}

		public Icon getIcon(File f){
			if (fsv.isDrive(f)){
				return hardDriveIcon;
			} else if (fsv.isComputerNode(f)){
				return computerIcon;
			} else if (f.isDirectory()){
				return folderIcon;
			}
			String name = f.getName().toLowerCase();
			if (name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".jpeg")){
				return new Icon16(f.getAbsolutePath());
			}

			return fileIcon;
		}

		public String getName(File f){
			String fileName = null;
			if (f != null){
				fileName = fsv.getSystemDisplayName(f);
			}
			return fileName;
		}

		public String getTypeDescription(File f){
			String type = fsv.getSystemTypeDescription(f);
			if (type == null){
				if (f.isDirectory()){
					type = directoryDescriptionText;
				} else{
					type = fileDescriptionText;
				}
			}
			return type;
		}

		public Boolean isTraversable(File f){
			return f.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
		}

		Icon getScaledIcon(File imageFile){
			ImageIcon imageIcon = new ImageIcon(imageFile.getAbsolutePath());
			Image image = imageIcon.getImage();
			Image scaledImage = image.getScaledInstance(16, 16, Image.SCALE_FAST);
			return new ImageIcon(scaledImage);
		}

		class Icon16 extends ImageIcon{
			private static final long serialVersionUID = -2304976923464323776L;

			public Icon16(String f){
				super(f);
				Image i = chooser.createImage(16, 16);
				i.getGraphics().drawImage(getImage(), 0, 0, 16, 16, chooser);
				setImage(i);
			}

			public int getIconHeight(){
				return 16;
			}

			public int getIconWidth(){
				return 16;
			}

			public void paintIcon(Component c, Graphics g, int x, int y){
				g.drawImage(getImage(), x, y, c);
			}
		}
	}
}
