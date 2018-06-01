/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.customlayouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;

/**
 * Custom Grid Layout which allows components of differrent heights
 */
@SuppressWarnings("serial")
public class GridLayout3 extends GridLayout{
	public GridLayout3(){
		this(1, 0, 0, 0);
	}

	public GridLayout3(int rows, int cols){
		this(rows, cols, 0, 0);
	}

	public GridLayout3(int rows, int cols, int hgap, int vgap){
		super(rows, cols, hgap, vgap);
	}

	public Dimension preferredLayoutSize(Container parent){
		synchronized (parent.getTreeLock()){
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = getRows();
			int ncols = getColumns();
			if (nrows > 0){
				ncols = (ncomponents + nrows - 1) / nrows;
			} else{
				nrows = (ncomponents + ncols - 1) / ncols;
			}
			int[] w = new int[ncols];
			int[] h = new int[nrows];

			for (int i = 0; i < ncomponents; i++){
				int r = i / ncols;
				int c = i % ncols;
				Component comp = parent.getComponent(i);
				Dimension d = comp.getPreferredSize();
				if (w[c] < d.width){
					w[c] = d.width;
				}

				if (h[r] < d.height){
					h[r] = d.height;
				}
			}

			int nw = 0;
			for (int j = 0; j < ncols; j++){
				nw += w[j];
			}
			int nh = 0;
			for (int i = 0; i < nrows; i++){
				nh += h[i];
			}

			return new Dimension(insets.left + insets.right + nw + (ncols - 1) * getHgap(), insets.top + insets.bottom + nh
			        + (nrows - 1) * getVgap());
		}
	}

	public Dimension minimumLayoutSize(Container parent){
		System.err.println("minimumLayoutSize");
		synchronized (parent.getTreeLock()){
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = getRows();
			int ncols = getColumns();
			if (nrows > 0){
				ncols = (ncomponents + nrows - 1) / nrows;
			} else{
				nrows = (ncomponents + ncols - 1) / ncols;
			}

			int[] w = new int[ncols];
			int[] h = new int[nrows];
			for (int i = 0; i < ncomponents; i++){
				int r = i / ncols;
				int c = i % ncols;
				Component comp = parent.getComponent(i);
				Dimension d = comp.getMinimumSize();
				if (w[c] < d.width){
					w[c] = d.width;
				}
				if (h[r] < d.height){
					h[r] = d.height;
				}
			}

			int nw = 0;
			for (int j = 0; j < ncols; j++){
				nw += w[j];
			}
			int nh = 0;
			for (int i = 0; i < nrows; i++){
				nh += h[i];
			}

			return new Dimension(insets.left + insets.right + nw + (ncols - 1) * getHgap(), insets.top + insets.bottom + nh
			        + (nrows - 1) * getVgap());
		}
	}

	public void layoutContainer(Container parent){
		synchronized (parent.getTreeLock()){
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = getRows();
			int ncols = getColumns();
			if (ncomponents == 0){
				return;
			}
			if (nrows > 0){
				ncols = (ncomponents + nrows - 1) / nrows;
			} else{
				nrows = (ncomponents + ncols - 1) / ncols;
			}
			int hgap = getHgap();
			int vgap = getVgap();
			// scaling factors
			Dimension pd = preferredLayoutSize(parent);
			double sh = (1.0 * parent.getHeight()) / pd.height;
			// scale
			int[] w = new int[ncols];
			int[] h = new int[nrows];

			int countScrolls = 0;
			for (int i = 0; i < ncomponents; i++){
				Component comp = parent.getComponent(i);

				if (comp instanceof JScrollPane && comp.isVisible())
					countScrolls++;
			}

			for (int i = 0; i < ncomponents; i++){
				int r = i / ncols;
				int c = i % ncols;
				Component comp = parent.getComponent(i);

				Dimension d = comp.getPreferredSize();
				Dimension dmax = comp.getMaximumSize();
				if (c == 1)
					d.width = (int) (d.width + parent.getWidth() - pd.width - hgap);
				d.height = Math.min((int) (sh * d.height), dmax.height);
				if (w[c] < d.width){
					w[c] = d.width;
				}

				if (comp.isVisible()){
					if (h[r] < d.height)
						h[r] = d.height;
				}
			}

			if (countScrolls > 0){
				int htotal = vgap * 2;
				for (int n = 0; n < nrows; n++)
					htotal += h[n] + vgap;

				int hplus = (parent.getParent().getHeight() - htotal) / countScrolls;

				if (hplus > 0){
					for (int i = 0; i < ncomponents; i++){
						Component comp = parent.getComponent(i);

						if (comp instanceof JScrollPane && comp.isVisible()){
							int r = i / ncols;
							h[r] += hplus;
						}
					}
				}
			}

			for (int c = 0, x = insets.left + getHgap(); c < ncols; c++){
				for (int r = 0, y = insets.top + getHgap(); r < nrows; r++){
					int i = r * ncols + c;
					if (i < ncomponents){
						Component comp = parent.getComponent(i);
						if (comp.isVisible()){
							Dimension d = comp.getMaximumSize();
							if (comp instanceof JScrollPane)
								comp.setBounds(x, y, w[c], h[r]);
							else
								comp.setBounds(x, y, w[c], Math.min(h[r], d.height));
						}
					}
					y += h[r] + vgap;
				}
				x += w[c] + hgap;
			}
		}
	}
}
