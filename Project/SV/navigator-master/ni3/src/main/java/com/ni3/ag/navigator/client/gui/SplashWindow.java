/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.net.URL;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

@SuppressWarnings({ "deprecation", "serial" })
public class SplashWindow extends Window{

	private static final Logger log = Logger.getLogger(SplashWindow.class);
	private static SplashWindow instance;

	private Image image;

	private boolean paintCalled = false;

	private SplashWindow(Frame parent, Image image){
		super(parent);
		this.image = image;

		// Load the image
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(image, 0);
		try{
			mt.waitForID(0);
		} catch (InterruptedException ie){
			log.error(ie);
		}

		// Abort on failure
		if (mt.isErrorID(0)){
			setSize(0, 0);
			System.err.println("Warning: SplashWindow couldn't load splash image.");
			synchronized (this){
				paintCalled = true;
				notifyAll();
			}
			return;
		}

		// Center the window on the screen
		int imgWidth = image.getWidth(this);
		int imgHeight = image.getHeight(this);
		setSize(imgWidth, imgHeight);
		//setLocation(CenterX - (imgWidth / 2), CenterY - (imgHeight / 2));
        setLocationRelativeTo(null);

		// Users shall be able to close the splash window by
		// clicking on its display area. This mouse listener
		// listens for mouse clicks and disposes the splash window.
		MouseAdapter disposeOnClick = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt){
				// Note: To avoid that method splash hangs, we
				// must set paintCalled to true and call notifyAll.
				// This is necessary because the mouse click may
				// occur before the contents of the window
				// has been painted.
				synchronized (SplashWindow.this){
					SplashWindow.this.paintCalled = true;
					SplashWindow.this.notifyAll();
				}
				dispose();
			}
		};
		addMouseListener(disposeOnClick);
	}

	/**
	 * Updates the display area of the window.
	 */
	public void update(Graphics g){
		// Note: Since the paint method is going to draw an
		// image that covers the complete area of the component we
		// do not fill the component with its background color
		// here. This avoids flickering.
		paint(g);
	}

	/**
	 * Paints the image on the window.
	 */
	public void paint(Graphics g){
		g.drawImage(image, 0, 0, this);

		// Notify method splash that the window
		// has been painted.
		// Note: To improve performance we do not enter
		// the synchronized block unless we have to.
		if (!paintCalled){
			paintCalled = true;
			synchronized (this){
				notifyAll();
			}
		}
	}

	/**
	 * Open's a splash window using the specified image.
	 * 
	 * @param image
	 *            The splash image.
	 */
	public static void splash(Image image){
		if (instance == null && image != null){
			Frame f = new Frame();

			// Create the splash image
			instance = new SplashWindow(f, image);

			// Show the window.
			instance.show();

			if (!EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1){
				synchronized (instance){
					while (!instance.paintCalled){
						try{
							instance.wait();
						} catch (InterruptedException e){
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	public static void splash(URL imageURL){
		if (imageURL != null){
			splash(Toolkit.getDefaultToolkit().createImage(imageURL));
		}
	}

	/**
	 * Closes the splash window.
	 */
	public static void disposeSplash(){
		if (instance != null){
			instance.getOwner().dispose();
			instance = null;
		}
	}
}
