/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MultiSlider extends JPanel{

	private static final long serialVersionUID = 5427277539495035486L;

	private int min, max;
	private SliderItem[] items;
	private boolean couldCrossRegion = false;
	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public MultiSlider(final int min, final int max){
		this.min = min;
		this.max = max;
		this.setPreferredSize(new Dimension(max - min, 18));

		MouseSlideListener lsn = new MouseSlideListener();
		this.addMouseListener(lsn);
		this.addMouseMotionListener(lsn);

	}

	public void setSliderItems(List<Integer> values){
		SliderItem[] items = new SliderItem[values.size()];
		for (int i = 0; i < values.size(); i++){
			items[i] = new SliderItem(Color.BLACK, values.get(i));
		}
		setSliderItems(items);
	}

	public List<Integer> getSliderValues(){
		List<Integer> values = new ArrayList<Integer>();
		for (SliderItem item : items){
			values.add(item.getValue());
		}
		return values;
	}

	private void setSliderItems(SliderItem[] items){
		this.items = items;
	}

	public void setCrossRegion(boolean b){
		couldCrossRegion = b;
	}

	public boolean getCrossRegion(){
		return couldCrossRegion;
	}

	public void setMin(int min){
		this.min = min;
	}

	public void setMax(int max){
		this.max = max;
	}

	@Override
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.black);
		g2d.drawLine(3, 1, this.getWidth() - 3, 1);
		for (int i = items.length - 1; i >= 0; i--){
			items[i].plot(g2d);
		}
	}

	public void addChangeListener(ChangeListener l){
		listeners.add(l);
	}

	public void removeChangeListener(ChangeListener l){
		listeners.remove(l);
	}

	protected void onValueChanged(){
		for (ChangeListener l : listeners){
			l.stateChanged(new ChangeEvent(this));
		}
	}

	public class SliderItem{

		private int value;
		private int dragValue;
		private Color color;

		public SliderItem(Color color, int value){
			this.color = color;
			this.value = value;
			this.dragValue = -1;
		}

		public int getValue(){
			return value;
		}

		public void setValue(int value){
			this.value = value;
			this.dragValue = value;
		}

		public void plot(Graphics2D g2d){
			g2d.setColor(color);
			int x = getCenterX();
			g2d.fillRect(x - 2, 8, 5, 5);
			g2d.setColor(Color.black);
			g2d.drawRect(x - 3, 7, 6, 6);
			g2d.drawLine(x - 3, 6, x, 2);
			g2d.drawLine(x + 3, 6, x, 2);
		}

		public int getCenterX(){
			if (dragValue > 0){
				return dragValue;
			}
			return 3 + (getWidth() - 6) * value / (max - min);
		}

		public boolean hitTest(int x, int y){
			int cx = getCenterX();
			return cx - 8 <= x && x <= cx + 8 && 4 <= y && y <= 18;
		}
	}

	private class MouseSlideListener extends MouseAdapter{

		SliderItem item;

		@Override
		public void mousePressed(MouseEvent e){
			for (SliderItem si : items){
				if (si.hitTest(e.getX(), e.getY()) == true){
					item = si;
					break;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e){
			if (item != null){
				item.dragValue = -1;
				item = null;
				onValueChanged();
			}
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e){
			if (item != null){
				double val = (e.getX() - 3.0) * (max - min) / (getWidth() - 6.0);
				int v = new Long(Math.round(val)).intValue();
				if (!couldCrossRegion && items.length > 0){
					int[] values = new int[items.length - 1];
					int index = 0;
					for (SliderItem si : items){
						if (si == item)
							continue;
						values[index] = si.value;
						index++;
					}
					Arrays.sort(values);
					int low = min - 1;
					for (int i = 0; i < values.length; i++){
						if (values[i] > item.value)
							break;
						low = values[i];
					}
					int high = max + 1;
					for (int i = values.length - 1; i >= 0; i--){
						if (values[i] < item.value)
							break;
						high = values[i];
					}
					v = Math.max(low + 1, Math.min(high - 1, v));
				}

				item.value = Math.max(min, Math.min(max, v));
				item.dragValue = e.getX();
				repaint();
			}
		}
	};

}
