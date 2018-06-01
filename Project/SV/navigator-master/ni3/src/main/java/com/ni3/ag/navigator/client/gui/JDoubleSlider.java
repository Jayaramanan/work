/** Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.util.Utility;

@SuppressWarnings("serial")
public class JDoubleSlider extends JPanel implements ActionListener{
	private int THUMB_SIZE = 14;
	private final static int BUFFER = 2;
	private final static int TEXT_HEIGHT = 18;
	private final static int TEXT_BUFFER = 3;
	private final static int DEFAULT_WIDTH = 300; // 200;
	private final static int DEFAULT_HEIGHT = 15;

	private int leftMargin;
	private int rightMargin;

	/** Array that holds colors of each of the 3 parts. */
	private Color colors[];
	private boolean enabled = true;
	private Dimension preferredSize_;

	/* this value depends on resizing */
	private int width_;
	private int pix[]; // pixel position of the thumbs
	private double values[]; // the 2 values

	/** current font of the labels. */
	private Font font;

	private double minVal, maxVal;

	private ArrayList<ChangeListener> listeners;

	private int thumbPressed;
	private int prevX;
	private Color bckground;

	private int dlgX, dlgY;

	private boolean gluedThumbs;
	private boolean intOnly;

	private String menuItems[];
	private String menuActions[];
	private ActionListener parentListener;

	public JDoubleSlider(Color bckground, int ThumbSize, ActionListener parentListener, String menuItems[],
	        String menuActions[]){
		THUMB_SIZE = ThumbSize;

		leftMargin = 15;
		rightMargin = 15;

		thumbPressed = 0;
		intOnly = true;

		this.bckground = bckground;

		this.menuItems = menuItems;
		this.menuActions = menuActions;
		this.parentListener = parentListener;

		values = new double[2];
		colors = new Color[3];
		preferredSize_ = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT + TEXT_HEIGHT + TEXT_BUFFER);

		font = new Font("TimesRoman", Font.PLAIN, 12);

		pix = new int[2];

		width_ = DEFAULT_WIDTH;

		setSize(width_, DEFAULT_HEIGHT + TEXT_HEIGHT);

		values[0] = 0.0;
		values[1] = 1.0;
		minVal = 0.0;
		maxVal = 1.0;

		gluedThumbs = false;

		recomputeValues(true);

		setColor(0, Color.red);
		setColor(1, Color.blue);
		setColor(2, Color.red);

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent evt){
				mouseDown(evt);
			}

			public void mouseReleased(MouseEvent evt){
				mouseUp(evt);
			}
		});

		addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent evt){
				mouseDrag(evt);
			}
		});

		listeners = new ArrayList<ChangeListener>();
	}

	public void addChangeListener(ChangeListener lst){
		listeners.add(lst);
	}

	public void setColor(int part, Color color){
		colors[part] = color;
	}

	public Color getColor(int part){
		return colors[part];
	}

	public int getDlgX(){
		return dlgX;
	}

	public int getDlgY(){
		return dlgY;
	}

	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		width_ = width;

		// recompute new thumbs pixels (for the same values)
		recomputeValues(true);
		repaint();
	}

	public void setInterval(double minVal, double maxVal){
		if (intOnly){
			minVal = Math.floor(minVal);
			maxVal = Math.ceil(maxVal);
		}

		if (values[0] < minVal || values[0] == this.minVal)
			values[0] = minVal;

		if (values[1] > maxVal || values[1] == this.maxVal)
			values[1] = maxVal;

		this.minVal = minVal;
		this.maxVal = maxVal;

		setValues(values[0], values[1]);
	}

	/**
	 * Sets new values of the slider. is 1 - a - b.
	 */
	public void setValues(double a, double b){
		if (intOnly){
			a = Math.floor(a);
			b = Math.ceil(b);
		}

		values[0] = a;
		values[1] = b;

		values[0] = Math.max(values[0], minVal);
		values[1] = Math.max(values[1], minVal);

		values[0] = Math.min(values[0], maxVal);
		values[1] = Math.min(values[1], maxVal);

		gluedThumbs = (values[0] == values[1]);

		recomputeValues(false);
	}

	public void recomputeValues(boolean callUpdateValues){
		double total = (double) (width_ - THUMB_SIZE * 2 - leftMargin - rightMargin); // sum

		double f1, f2;

		if (minVal == maxVal){
			f1 = (values[0] - minVal);
			f2 = (values[1] - minVal);
		} else{
			f1 = (values[0] - minVal) / (maxVal - minVal);
			f2 = (values[1] - minVal) / (maxVal - minVal);
		}

		pix[0] = (int) (f1 * total) + THUMB_SIZE + leftMargin;
		pix[1] = (int) (f2 * total) + THUMB_SIZE + leftMargin;

		if (values[0] == values[1]){
			if (values[0] == minVal)
				pix[1] += THUMB_SIZE * 2;
			else if (values[1] == maxVal)
				pix[0] -= THUMB_SIZE * 2;
			else{
				pix[0] -= THUMB_SIZE;
				pix[1] += THUMB_SIZE;
			}
		}

		if (pix[1] - pix[0] < THUMB_SIZE * 2){
			if (pix[0] + THUMB_SIZE * 2 < width_ - THUMB_SIZE + leftMargin)
				pix[1] = pix[0] + THUMB_SIZE * 2;
			else
				pix[0] = pix[1] - THUMB_SIZE * 2;

			if (callUpdateValues){
				updateValues(0);
				updateValues(1);
			}
		}

		repaint();
	}

	public void updateValues(int index){
		double total = (double) (width_ - THUMB_SIZE * 2 - leftMargin - rightMargin); // sum
		int a = pix[index] - THUMB_SIZE - leftMargin;

		values[index] = ((double) a / total) * (maxVal - minVal) + minVal;

		if (a < 2 && index == 0)
			values[0] = minVal;

		if (a > total - 2 && index == 1)
			values[1] = maxVal;
	}

	public double getValue(int part){
		if (gluedThumbs){
			double val;

			if (values[0] == minVal)
				return Math.floor(minVal);
			else if (values[1] == maxVal)
				return Math.ceil(maxVal);
			else
				val = (values[0] + values[1]) / 2.0;

			Utility.debugToConsole(Double.toString(val));

			return Math.round(val);
		}

		if (intOnly){
			if (part == 0)
				return Math.floor(values[part]);

			return Math.ceil(values[part]);
		}

		return values[part];
	}

	public void Motion(){
		for (ChangeListener cl : listeners){
			cl.stateChanged(new ChangeEvent(this));
		}
	}

	public void paint(Graphics g){
		int width = getSize().width;
		int height = getSize().height;

		g.setColor(bckground);
		g.fillRect(0, 0, width, height);

		g.setColor(colors[0]);
		g.fillRect(leftMargin, TEXT_HEIGHT, pix[0] - THUMB_SIZE, height - TEXT_HEIGHT);
		g.setColor(colors[1]);
		g.fillRect(pix[0] + THUMB_SIZE, TEXT_HEIGHT, pix[1] - pix[0] - THUMB_SIZE * 2, height - TEXT_HEIGHT);
		g.setColor(colors[2]);
		g.fillRect(pix[1] + THUMB_SIZE, TEXT_HEIGHT, width_ - rightMargin - pix[1] - THUMB_SIZE, height - TEXT_HEIGHT);

		/* draw two thumbs */
		g.setColor(Color.lightGray);
		g.fill3DRect(pix[0] - THUMB_SIZE, TEXT_HEIGHT /* + BUFFER */, THUMB_SIZE * 2 + 1, height /*- 2 * BUFFER*/
		        - TEXT_HEIGHT, true);
		g.fill3DRect(pix[1] - THUMB_SIZE, TEXT_HEIGHT /* + BUFFER */, THUMB_SIZE * 2 + 1, height /*- 2 * BUFFER*/
		        - TEXT_HEIGHT, true);
		g.setColor(Color.black);
		g.drawLine(pix[0], TEXT_HEIGHT + BUFFER + 1, pix[0], height - 2 * BUFFER);
		g.drawLine(pix[1], TEXT_HEIGHT + BUFFER + 1, pix[1], height - 2 * BUFFER);
		g.setFont(font);

		if (gluedThumbs){
			// center each value in the middle
			String str = render(getValue(0));
			g.drawString(str, (pix[0] + pix[1]) / 2 - (int) (getFontMetrics(font).stringWidth(str) / 2), TEXT_HEIGHT
			        - TEXT_BUFFER);
		} else{
			// center each value in the middle
			String str = render(getValue(0));
			g.drawString(str, pix[0] - THUMB_SIZE, TEXT_HEIGHT - TEXT_BUFFER);

			str = render(getValue(1));
			g
			        .drawString(str, pix[1] + THUMB_SIZE - (int) (getFontMetrics(font).stringWidth(str)), TEXT_HEIGHT
			                - TEXT_BUFFER);
		}
	}

	private String render(double value){
		DecimalFormat myF = new DecimalFormat("###,###,###.#");
		return myF.format(value);
	}

	private void mouseDown(MouseEvent e){
		if (e.getButton() == MouseEvent.BUTTON1){
			if (enabled){
				prevX = (int) e.getPoint().getX();
				thumbPressed = getThumb(prevX);
				if (thumbPressed > 0)
					HandleMouse(prevX, false);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3){
			JPopupMenu popup = new JPopupMenu();

			fillPopupMenu(popup);

			dlgX = e.getX() + getX();
			dlgY = e.getY() + getY();
			popup.show(this, e.getX(), e.getY());
		}
	}

	private void mouseUp(MouseEvent e){
		if (enabled && e.getButton() == MouseEvent.BUTTON1){
			thumbPressed = 0;
			Motion();
		}
	}

	private void mouseDrag(MouseEvent e){
		if (enabled && ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)){
			if (thumbPressed > 0){
				HandleMouse((int) e.getPoint().getX(), true);
				prevX = (int) e.getPoint().getX();
			}
		}
	}

	protected int getThumb(int x){
		if (x < pix[0] + THUMB_SIZE)
			return 1;

		if (x > pix[1] - THUMB_SIZE)
			return 2;

		return 0;
	}

	/**
	 * Does all the recalculations related to user interaction with the slider.
	 */
	protected void HandleMouse(int x, boolean drag){
		int left = pix[0], right = pix[1];
		int xmin = THUMB_SIZE + leftMargin;
		int xmax = width_ - THUMB_SIZE - rightMargin;

		if (thumbPressed == 1){
			if (drag)
				left += x - prevX;
			else
				left = x;
		}

		if (thumbPressed == 2){
			if (drag)
				right += x - prevX;
			else
				right = x;

		}

		/* verify boundaries and reconcile */
		if (thumbPressed == 1){
			if (left < xmin){
				left = xmin;
			} else if (left > (xmax - THUMB_SIZE * 2)){
				left = xmax - THUMB_SIZE * 2;
			} else{
				if (left > (right - THUMB_SIZE * 2) && right < xmax){
					right = left + THUMB_SIZE * 2;
				}
			}
		} else{
			// right control
			if (right > xmax){
				right = xmax;
			} else if (right < (xmin + THUMB_SIZE * 2)){
				right = xmin + THUMB_SIZE * 2;
			} else{
				if (right < (left + THUMB_SIZE * 2) && left > xmin){
					// push left
					left = right - THUMB_SIZE * 2;
				}
			}
		}

		pix[0] = left;
		pix[1] = right;
		gluedThumbs = (pix[1] - pix[0] <= THUMB_SIZE * 2);

		if (gluedThumbs){
			updateValues(0);
			updateValues(1);

			values[0] = getValue(0);
			values[1] = getValue(1);
		} else
			updateValues(thumbPressed - 1);

		repaint();
	}

	/**
	 * Overrides the default update(Graphics) method in order not to clear screen to avoid flicker.
	 */
	public void update(Graphics g){
		paint(g);
	}

	public Dimension preferredSize(){
		return preferredSize_;
	}

	public Dimension minimumSize(){
		return preferredSize_;
	}

	public void setEnabled(boolean flag){
		enabled = flag;
	}

	public void actionPerformed(ActionEvent e){
		if ("MinMax".equals(e.getActionCommand())){
			JInputValuesDialog dlg2 = new JInputValuesDialog(UserSettings.getWord("Min/Max value"), new String[] {
			        UserSettings.getWord("Min value"), UserSettings.getWord("Max value") }, new Object[] {
			        new Double(getValue(0)), new Double(getValue(1)) }, new String[] { "####", "####" });

			dlg2.setBounds(dlgX, dlgY, dlg2.getWidth(), dlg2.getHeight());
			dlg2.setVisible(true);
			if (dlg2.getReturnStatus() == JInputValuesDialog.RET_CANCEL)
				return;

			double min, max;
			try{
				min = Math.min(maxVal, Math.max((Double) dlg2.getValue(0), minVal));
				max = Math.min(maxVal, Math.max((Double) dlg2.getValue(1), minVal));

				if (min > max){
					double sw = min;
					min = max;
					max = sw;
				}

				setValues(min, max);
				Motion();
			} catch (NumberFormatException ex){
				JOptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("EnterNumericalValue"), UserSettings
				        .getWord("Data validation"), JOptionPane.INFORMATION_MESSAGE);

				return;
			}
		}
	}

	public void fillPopupMenu(JPopupMenu popup){
		JMenuItem item;

		if (menuItems == null){
			item = new JMenuItem(UserSettings.getWord("Set Min/Max"));
			item.setActionCommand("MinMax");
			item.addActionListener(this);
			popup.add(item);
		} else{
			for (int n = 0; n < menuItems.length; n++){
				item = new JMenuItem(UserSettings.getWord(menuItems[n]));
				item.setActionCommand(menuActions[n]);
				item.addActionListener(parentListener);
				popup.add(item);
			}
		}
	}

	public double getMinVal(){
		return minVal;
	}

	public double getMaxVal(){
		return maxVal;
	}

	public boolean isCurrentStart(){
		return getMinVal() == values[0];
	}

	public boolean isCurrentEnd(){
		return getMaxVal() == values[1];
	}

	public void resetToStart(){
		values[0] = getMinVal();
		gluedThumbs = (values[0] == values[1]);
		recomputeValues(false);
	}

	public void resetToEnd(){
		values[1] = getMaxVal();
		gluedThumbs = (values[0] == values[1]);
		recomputeValues(false);
	}
}
