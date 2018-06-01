package com.ni3.ag.navigator.client.gui.map;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class MapToolbar extends JToolBar implements ChangeListener, ActionListener, MouseListener, Ni3ItemListener{
	private static final long serialVersionUID = -6989297147194903382L;
	private JSlider zoomSlider;
	private JSlider iconSizeSlider, edgeSizeSlider;
	private JRadioButton showMetaphorsBtn, showNumbersBtn;
	private JButton zoomInButton, zoomOutButton;

	private Ni3Document doc;
	private int maxZoomLevel;
	private int minZoomLevel;
	private boolean dragActive = false;

	public MapToolbar(Ni3Document doc, int maxZoomLevel){
		super(SwingConstants.VERTICAL);
		this.doc = doc;
		this.maxZoomLevel = maxZoomLevel;
		minZoomLevel = 1;
		setRollover(true);
		int currentZoom = doc.getMapSettings().getZoom();
		createZoomSlider(currentZoom, maxZoomLevel);
		createMetaphorNumberSwitch();
		doc.registerListener(this);
	}

	private void createZoomSlider(int currentZoom, int maxZoomLevel){
		zoomSlider = new JSlider(SwingConstants.VERTICAL, 0, maxZoomLevel - 1, 0);
		zoomSlider.setMinimumSize(new Dimension(10, 10));
		zoomSlider.setPreferredSize(new Dimension(10, 300));
		zoomSlider.setPaintTicks(true);
		zoomSlider.setSnapToTicks(true);
		zoomSlider.setMajorTickSpacing(1);
		zoomSlider.setInverted(true);
		zoomSlider.setName("ZoomSlider");
		zoomSlider.setToolTipText(UserSettings.getWord("Zoom slider"));
		setZoom(currentZoom);

		zoomInButton = new JButton(IconCache.getImageIcon(IconCache.ZOOM_IN));
		zoomInButton.setMinimumSize(new Dimension(24, 24));
		zoomInButton.setPreferredSize(new Dimension(24, 24));
		zoomInButton.setToolTipText(UserSettings.getWord("Zoom in"));
		zoomInButton.addActionListener(this);

		zoomOutButton = new JButton(IconCache.getImageIcon(IconCache.ZOOM_OUT));
		zoomOutButton.setMinimumSize(new Dimension(24, 24));
		zoomOutButton.setPreferredSize(new Dimension(24, 24));
		zoomOutButton.setToolTipText(UserSettings.getWord("Zoom out"));
		zoomOutButton.addActionListener(this);

		iconSizeSlider = new JSlider(SwingConstants.VERTICAL, 3, 30, 10);
		iconSizeSlider.setMinimumSize(new Dimension(10, 10));
		iconSizeSlider.setPreferredSize(new Dimension(10, 50));
		iconSizeSlider.setPaintTicks(false);
		iconSizeSlider.setSnapToTicks(false);
		iconSizeSlider.setMajorTickSpacing(1);
		iconSizeSlider.setInverted(false);
		iconSizeSlider.setName("IconSizeSlider");
		iconSizeSlider.setToolTipText(UserSettings.getWord("Metaphor size"));

		edgeSizeSlider = new JSlider(SwingConstants.VERTICAL, 3, 30, 10);
		edgeSizeSlider.setMinimumSize(new Dimension(10, 10));
		edgeSizeSlider.setPreferredSize(new Dimension(10, 50));
		edgeSizeSlider.setPaintTicks(false);
		edgeSizeSlider.setSnapToTicks(false);
		edgeSizeSlider.setMajorTickSpacing(1);
		edgeSizeSlider.setInverted(false);
		edgeSizeSlider.setName("EdgeSizeSlider");
		edgeSizeSlider.setToolTipText(UserSettings.getWord("Connection size"));

		final JPanel sliderPanel = new JPanel(new GridLayout(1, 2));

		sliderPanel.setMinimumSize(new Dimension(22, 10));
		sliderPanel.setPreferredSize(new Dimension(22, 100));

		sliderPanel.add(iconSizeSlider);
		sliderPanel.add(edgeSizeSlider);

		setMargin(new Insets(0, 0, 0, 0));
		add(zoomInButton);
		add(zoomSlider);
		add(zoomOutButton);

		add(sliderPanel);

		setFloatable(false);

		zoomSlider.addMouseListener(this);
		zoomSlider.addChangeListener(this);
		iconSizeSlider.addChangeListener(this);
		edgeSizeSlider.addChangeListener(this);
	}

	private void createMetaphorNumberSwitch(){
		final JPanel mPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		final JPanel nPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		showMetaphorsBtn = new JRadioButton();
		showNumbersBtn = new JRadioButton();

		final JLabel metaphorLabel = new JLabel(IconCache.getImageIcon(IconCache.MAP_NODE_TOGGLE));
		final JLabel numberLabel = new JLabel(IconCache.getImageIcon(IconCache.MAP_PIN_TOGGLE));

		showMetaphorsBtn.setToolTipText(UserSettings.getWord("DisplayMetaphorsTooltip"));
		metaphorLabel.setToolTipText(UserSettings.getWord("DisplayMetaphorsTooltip"));
		showNumbersBtn.setToolTipText(UserSettings.getWord("DisplayNumbersTooltip"));
		numberLabel.setToolTipText(UserSettings.getWord("DisplayNumbersTooltip"));

		mPanel.add(showMetaphorsBtn);
		mPanel.add(metaphorLabel);
		add(mPanel);

		nPanel.add(showNumbersBtn);
		nPanel.add(numberLabel);
		add(nPanel);

		mPanel.setMaximumSize(new Dimension(50, 20));
		nPanel.setMaximumSize(new Dimension(50, 20));

		ButtonGroup group = new ButtonGroup();
		group.add(showMetaphorsBtn);
		group.add(showNumbersBtn);
		showMetaphorsBtn.setSelected(true);

		showMetaphorsBtn.addActionListener(this);
		showNumbersBtn.addActionListener(this);
	}

	private void adjustSliders(MapSettings settings){
		setZoom(settings.getZoom());
		setIconSize(settings.getNodeScale());
		setEdgeSize(settings.getEdgeScale());
	}

	private void updateMetaphorButtonState(boolean showNumericMetaphors){
		showMetaphorsBtn.setSelected(!showNumericMetaphors);
		showNumbersBtn.setSelected(showNumericMetaphors);
	}

	private int getZoom(){
		return maxZoomLevel - zoomSlider.getValue();
	}

	private void setZoom(int zoom){
		zoomSlider.setValue(maxZoomLevel - zoom);
	}

	private void setEdgeSize(double edgeScale){
		edgeSizeSlider.setValue((int) (edgeScale * 10));
	}

	private void setIconSize(double nodeScale){
		iconSizeSlider.setValue((int) (nodeScale * 10));
	}

	private void setZoomToModel(int newZoom){
		final MapSettings settings = doc.getMapSettings();
		if (newZoom <= maxZoomLevel && newZoom >= minZoomLevel && newZoom != settings.getZoom()){
			settings.setZoom(newZoom);
			doc.setMapSettings(settings);
		}
	}

	private void setIconSizeToModel(double v){
		final MapSettings settings = doc.getMapSettings();
		settings.setNodeScale(v);
		doc.setMapSettings(settings);
	}

	private void setEdgeSizeScaleToModel(double v){
		final MapSettings settings = doc.getMapSettings();
		settings.setEdgeScale(v);
		doc.setMapSettings(settings);
	}

	@Override
	public void stateChanged(ChangeEvent e){
		if (zoomSlider == e.getSource() && !dragActive){
			setZoomToModel(getZoom());
		}
		if (e.getSource() == iconSizeSlider && !dragActive){
			setIconSizeToModel((double) iconSizeSlider.getValue() / 10.0);
		}
		if (e.getSource() == edgeSizeSlider && !dragActive){
			setEdgeSizeScaleToModel((double) edgeSizeSlider.getValue() / 10.0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (zoomInButton == e.getSource()){
			MapSettings settings = doc.getMapSettings();
			setZoomToModel(settings.getZoom() + 1);

		} else if (zoomOutButton == e.getSource()){
			MapSettings settings = doc.getMapSettings();
			setZoomToModel(settings.getZoom() - 1);
		} else if (showMetaphorsBtn == e.getSource()){
			if (showMetaphorsBtn.isSelected() && doc.isShowNumericMetaphors()){
				doc.setShowNumericMetaphors(false);
			}
		} else if (showNumbersBtn == e.getSource()){
			if (showNumbersBtn.isSelected() && !doc.isShowNumericMetaphors()){
				doc.setShowNumericMetaphors(true);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e){
		dragActive = true;
	}

	@Override
	public void mouseReleased(MouseEvent e){
		if (zoomSlider == e.getSource()){
			setZoomToModel(getZoom());
		}
		dragActive = false;
	}

	@Override
	public void mouseClicked(MouseEvent e){
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public void event(int eventCode, int sourceID, Object source, Object param){
		switch (eventCode){
			case MSG_MapSettingsChanged:
				if (param instanceof MapSettings)
					adjustSliders((MapSettings) param);
				break;
			case MSG_ShowNumericMetaphorsChanged:
				updateMetaphorButtonState((Boolean) param);
				break;
		}
	}

	@Override
	public int getListenerType(){
		return SRC_GIS;
	}
}
