/** Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.navigator.client.controller.DynamicChart;
import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.domain.cache.IconCache;
import com.ni3.ag.navigator.client.gui.common.Ni3Frame;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.ChartType;

@SuppressWarnings("serial")
public class LegendFrame extends Ni3Frame implements Ni3ItemListener, ComponentListener, ActionListener{

	private static final int FILTER_ITEM_HEIGHT = 30;

	private JButton resetButton;
	private JDoubleSlider filterSlider;
	private List<JDoubleSlider> filterValuesSliders;

	private List<JCheckBox> filterOutCheckBoxes;
	private LegendPane pane;

	private Ni3Document doc;
	private Font font;

	private Entity entity;
	private int labelWidth = 10;
	private Node nodeInHighlight;
	// TODO: Decrypt variable meaning
	private int columnX[] = { 0, 80, 130, 135, 155 };

	private ChangeListener changeListener;

	private JRadioButton pieChartButton;
	private JRadioButton barChartButton;

	private ChartParams chartParams;

	public LegendFrame(Ni3Document doc, Entity entity){
		super();
		this.doc = doc;
		this.entity = entity;
		this.chartParams = doc.getChartParams(entity.ID);

		font = Utility.createFont(UserSettings.getProperty("FontColor", "ChartLegendFont", "Dialog,1,14"));

		doc.registerListener(this);

		nodeInHighlight = null;

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		pane = new LegendPane(this);
		pane.setLayout(null);

		JScrollPane scroll = new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		setLayout(new BorderLayout());

		add(scroll, BorderLayout.CENTER);

		// Enable tool tips.
		ToolTipManager.sharedInstance().registerComponent(pane);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setReshowDelay(0);

		filterSlider = new JDoubleMainLegendSlider(this, getBackground(), 7, null, null, null);
		filterSlider.setName("FilterSlider");
		changeListener = new LegendFrameChangeListener();
		filterSlider.addChangeListener(changeListener);
		filterSlider.setInterval(0, 100);
		filterSlider.setPreferredSize(new Dimension(100, 25));

		add(filterSlider, BorderLayout.NORTH);

		resetButton = new JButton(UserSettings.getWord("Reset"));
		resetButton.addActionListener(this);
		resetButton.setActionCommand("Reset");
		resetButton.setPreferredSize(new Dimension(70, 23));

		JPanel southPanel = new JPanel();
		southPanel.setPreferredSize(new Dimension(300, 33));
		SpringLayout sLayout = new SpringLayout();
		southPanel.setLayout(sLayout);
		add(southPanel, BorderLayout.SOUTH);
		southPanel.add(resetButton);

		sLayout.putConstraint(SpringLayout.NORTH, resetButton, 5, SpringLayout.NORTH, southPanel);
		sLayout.putConstraint(SpringLayout.WEST, resetButton, -80, SpringLayout.EAST, southPanel);

		filterValuesSliders = new ArrayList<JDoubleSlider>();
		filterOutCheckBoxes = new ArrayList<JCheckBox>();

//		ImageIcon frameIcon = IconCache.getImageIcon("molecule.png");
//		if (frameIcon != null)
//			setIconImage(frameIcon.getImage());

		addComponentListener(this);
		pieChartButton = new JRadioButton(UserSettings.getWord("Pie charts"));
		sLayout.putConstraint(SpringLayout.NORTH, pieChartButton, 5, SpringLayout.NORTH, southPanel);
		sLayout.putConstraint(SpringLayout.WEST, pieChartButton, 10, SpringLayout.WEST, southPanel);
		southPanel.add(pieChartButton);

		barChartButton = new JRadioButton(UserSettings.getWord("Bar charts"));
		sLayout.putConstraint(SpringLayout.NORTH, barChartButton, 5, SpringLayout.NORTH, southPanel);
		sLayout.putConstraint(SpringLayout.WEST, barChartButton, 10, SpringLayout.EAST, pieChartButton);
		southPanel.add(barChartButton);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(pieChartButton);
		buttonGroup.add(barChartButton);

		pieChartButton.setSelected(true);
		pieChartButton.addActionListener(this);
		barChartButton.addActionListener(this);
	}

	public Entity getEntity(){
		return entity;
	}

	private boolean isAllEntityLegend(){
		return entity.ID == Entity.COMMON_ENTITY_ID;
	}

	public int getListenerType(){
		return Ni3ItemListener.SRC_LegendFrame;
	}

	public class LegendPane extends JComponent implements MouseMotionListener{
		LegendFrame parent;

		public LegendPane(LegendFrame parent){
			this.parent = parent;
		}

		public void paint(Graphics g){

			Graphics2D g2 = (Graphics2D) g;

			setFont(font);

			Dimension d = getSize();

			g2.setColor(getBackground());
			g2.fillRect(0, 0, d.width, d.height);

			TextLayout layout;

			final boolean showValue = nodeInHighlight != null && nodeInHighlight.hasChart()
			        && (isAllEntityLegend() || nodeInHighlight.Obj.getEntity().ID == entity.ID) && chartParams.isShowLabelOnLegend();

			int y = 20;
			final List<ChartAttributeDescriptor> chartAttributes = chartParams.getChartAttributes();
			for (int n = 0; n < chartAttributes.size(); n++){
				final Attribute attribute = chartAttributes.get(n).getAttribute();
				if (showValue){
					g2.setColor(Color.black);
					String num;
					final double chartValue = nodeInHighlight.getChartValue(n);
					if (attribute != null && attribute.getDataType() != null && !attribute.predefined){
						num = attribute.displayValue(chartValue);
					} else if (chartParams.getSummaryFormat() != null){
						num = chartParams.getSummaryFormat().format(chartValue);
					} else{
						num = String.valueOf(chartValue);
					}
					layout = new TextLayout(num, g2.getFont(), g2.getFontRenderContext());
					// TODO: Extract constants (padding???)
					g2.drawString(num, columnX[1] - 5 - (int) (layout.getBounds().getWidth()), y + 15);
				}

				final ChartAttributeDescriptor descriptor = chartAttributes.get(n);
				g2.setColor(descriptor.getColor());
				g2.fillRect(columnX[1], y, 40, 20);
				g2.setColor(Color.black);
				g2.drawRect(columnX[1], y, 40, 20);
				g2.drawString(descriptor.getLabel(), columnX[2], y + 15);

				y += 30;
			}

			for (JDoubleSlider sld : filterValuesSliders){
				if (sld.isVisible()){
					Point pt = sld.getLocation();
					g.translate(pt.x, pt.y);
					sld.paint(g);
					g.translate(-pt.x, -pt.y);
				}
			}

			for (JCheckBox box : filterOutCheckBoxes){
				if (box != null && box.isVisible()){
					Point pt = box.getLocation();
					g.translate(pt.x, pt.y);
					box.paint(g);
					g.translate(-pt.x, -pt.y);
				}
			}
		}

		public String getToolTipText(MouseEvent e){
			int index = (e.getY() - 20) / 30;
			if (index >= 0 && index < chartParams.getChartAttributes().size()){
				final ChartAttributeDescriptor descriptor = chartParams.getChartAttributes().get(index);
				// TODO: Refactor if
				if (descriptor.getMinVal() > 0 || descriptor.getMaxVal() > 0){
					return descriptor.getLabel() + " (" + descriptor.getMinVal() + " - " + descriptor.getMaxVal() + ")";
				}
			}
			return "";
		}

		public void mouseDragged(MouseEvent arg0){
		}

		public void mouseMoved(MouseEvent arg0){

		}
	}

	public void initialize(int ChartID, String Caption, DataFilter filter){
		this.chartParams = doc.getChartParams(entity.ID);
		final ChartType chartType = chartParams.getChartType();
		if (chartType == ChartType.Pie)
			pieChartButton.setSelected(true);
		else if (chartType == ChartType.Bar)
			barChartButton.setSelected(true);

		if (Caption != null)
			setTitle(Caption);

		for (JDoubleSlider sld : filterValuesSliders)
			pane.remove(sld);
		filterValuesSliders.clear();

		for (JCheckBox box : filterOutCheckBoxes)
			pane.remove(box);
		filterOutCheckBoxes.clear();

		fillLegendData(chartParams, filter);

		int y = 10;
		final List<ChartAttributeDescriptor> descriptors = chartParams.getChartAttributes();
		int descriptorsCount = descriptors.size();

		int widthOfSlider = 150;
		final ChartFilter cFilter = filter.getChartFilter(entity.ID);

		for (int n = 0; n < descriptorsCount; n++){
			final ChartAttributeDescriptor descriptor = descriptors.get(n);

			JDoubleSlider slider = new JDoubleSlider(getBackground(), 7, null, null, null);
			slider.setBounds(columnX[4] + labelWidth, y, widthOfSlider, 25);
			slider.setName("V" + descriptor.getValueIndex());
			slider.addChangeListener(changeListener);
			slider.setColor(1, descriptor.getColor());
			filterValuesSliders.add(slider);
			pane.add(slider);

			JCheckBox checkBox = new JCheckBox("", !cFilter.isExcluded(n));
			checkBox.setBounds(columnX[3] + labelWidth, y + 10, 15, 15);
			checkBox.setName("C" + descriptor.getValueIndex());
			checkBox.addChangeListener(changeListener);
			filterOutCheckBoxes.add(checkBox);
			pane.add(checkBox);
			y += FILTER_ITEM_HEIGHT;
		}

		double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int maxHeight = (int) screenHeight - 50;

		pane.setPreferredSize(new Dimension(labelWidth + 80, 40 + descriptorsCount * 30));
		setMinimumSize(new Dimension(columnX[4] + 20 + labelWidth + widthOfSlider, Math.min(40 + descriptorsCount * 30,
		        maxHeight)));

		this.setSize(labelWidth + columnX[4] + 20 + widthOfSlider, Math.min(descriptorsCount * 30 + 150, maxHeight));
		this.setResizable(false);

		setSliderInterval(doc.Subgraph, filter, true, true, true, true);

		setAlwaysOnTop(true);

		repaint();
	}

	private void fillLegendData(ChartParams params, DataFilter filter){
		final List<ChartAttributeDescriptor> attributes = params.getChartAttributes();

		int index = 0;
		int lblWidth;

		final ChartFilter cFilter = filter.getChartFilter(entity.ID);
		for (ChartAttributeDescriptor chartAttribute : attributes){
			final int attributeId = chartAttribute.getAttribute().ID;
			ChartFilterAttribute attr = cFilter.getAttribute(index);
			attr.setAttributeId(attributeId);

			lblWidth = getFontMetrics(font).stringWidth(chartAttribute.getLabel());

			if (labelWidth < lblWidth)
				labelWidth = lblWidth;

			index++;
		}
	}

	public void setSliderInterval(GraphCollection subgraph, DataFilter filter, boolean resetAF, boolean resetVS){
		setSliderInterval(subgraph, filter, resetAF, resetVS, true, false);
	}

	public void setSliderInterval(GraphCollection subgraph, DataFilter filter, boolean resetAF, boolean resetVS,
	        boolean updateAFInterval, boolean valuesFromFilter){
		double min, max;

		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;

		for (Node n : subgraph.getNodes()){
			if (isCurrentNode(n)){
				n.recalculateGraphValues(filter);
				if (n.hasChart() && (n.isActive() || n.filteredOutByChartAF)){
					if (n.chartTotalWithoutFiltering > max)
						max = n.chartTotalWithoutFiltering;
					if (n.chartTotalWithoutFiltering < min)
						min = n.chartTotalWithoutFiltering;
				}
			}
		}

		final ChartFilter cFilter = filter != null ? filter.getChartFilter(entity.ID) : null;

		if (max != Double.NEGATIVE_INFINITY){
			if (cFilter != null && valuesFromFilter){
				double minV = cFilter.getMinChartVal();
				double maxV = cFilter.getMaxChartVal();
				if (min == max && minV == maxV && min == minV){
					max += 1.0;
					min -= 1.0;
					minV = min;
					maxV = max;
				}
				filterSlider.setInterval(min, max);
				filterSlider.setValues(minV, maxV);
			} else{
				if (max == min){
					max += 1.0;
					min -= 1.0;
				}

				boolean resetToStart = filterSlider.isCurrentStart();
				boolean resetToEnd = filterSlider.isCurrentEnd();

				if (updateAFInterval){
					filterSlider.setInterval(min, max);
				}
				if (resetAF || resetToStart){
					filterSlider.resetToStart();
				}
				if (resetAF || resetToEnd){
					filterSlider.resetToEnd();
				}
			}

			if (cFilter != null){
				cFilter.setMinChartVal(filterSlider.getValue(0));
				cFilter.setMaxChartVal(filterSlider.getValue(1));
			}

			filterSlider.setVisible(true);
		} else
			filterSlider.setVisible(false);

		final List<ChartAttributeDescriptor> descriptors = chartParams.getChartAttributes();
		if (!descriptors.isEmpty()){
			for (int i = 0; i < descriptors.size(); i++){
				setSliderValueInterval(subgraph, i, descriptors.get(i).getValueIndex(), filter, resetVS, valuesFromFilter);
			}
		}
	}

	public void setSliderValueInterval(GraphCollection subgraph, int index, int valueIndex, DataFilter filter,
	        boolean resetVS, boolean valuesFromFilter){
		double min, max;

		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;

		final List<ChartAttributeDescriptor> descriptors = chartParams.getChartAttributes();

		final ChartAttributeDescriptor valueIndexDescriptor = descriptors.get(valueIndex);
		valueIndexDescriptor.setMinVal(Integer.MAX_VALUE);
		valueIndexDescriptor.setMaxVal(Integer.MIN_VALUE);

		final ChartAttributeDescriptor indexDescriptor = descriptors.get(index);

		for (Node n : subgraph.getNodes()){
			if (isCurrentNode(n) && n.hasChart()){
				valueIndexDescriptor.setMinVal((int) Math.min(Math.floor(n.getChartValue(index)), indexDescriptor
				        .getMinVal()));
				valueIndexDescriptor.setMaxVal((int) Math
				        .max(Math.ceil(n.getChartValue(index)), indexDescriptor.getMaxVal()));

				if (n.isActive() && !n.filteredOutByChartAF){
					if (n.getChartValue(index) > max)
						max = n.getChartValue(index);

					if (n.getChartValue(index) < min)
						min = n.getChartValue(index);
				}
			}
		}

		final ChartFilter cFilter = filter != null ? filter.getChartFilter(entity.ID) : null;

		if (min != Double.POSITIVE_INFINITY && (min != 0.0 || max != 0.0)){
			if (min == max){
				max += 1.0;
				min -= 1.0;
			}
			if (cFilter != null && valuesFromFilter){
				final double minV = cFilter.getMinChartAttrVal(index);
				final double maxV = cFilter.getMaxChartAttrVal(index);
				filterValuesSliders.get(index).setInterval(min, max);
				filterValuesSliders.get(index).setValues(minV, maxV);
			} else{
				boolean resetToStart = filterValuesSliders.get(index).isCurrentStart();
				boolean resetToEnd = filterValuesSliders.get(index).isCurrentEnd();

				filterValuesSliders.get(index).setInterval(min, max);
				if (resetVS || resetToStart){
					filterValuesSliders.get(index).resetToStart();
				}
				if (resetVS || resetToEnd){
					filterValuesSliders.get(index).resetToEnd();
				}
			}

			if (filter != null){
				cFilter.setMinChartAttrVal(valueIndex, filterValuesSliders.get(index).getValue(0));
				cFilter.setMaxChartAttrVal(valueIndex, filterValuesSliders.get(index).getValue(1));
			}

			filterOutCheckBoxes.get(index).setVisible(true);
			filterValuesSliders.get(index).setVisible(true);

			if (filter != null){
				if (cFilter.isExcluded(valueIndex))
					filterValuesSliders.get(index).setVisible(false);
			}
		} else{
			filterValuesSliders.get(index).setVisible(false);
			filterOutCheckBoxes.get(index).setVisible(false);
		}

		if (filter != null){
			filterOutCheckBoxes.get(index).setSelected(!cFilter.isExcluded(valueIndex));
		}
	}

	private boolean isCurrentNode(Node node){
		return isAllEntityLegend() || node.Obj.getEntity().ID == entity.ID;
	}

	public void event(int EventCode, int SourceID, Object source, Object Param){
		switch (EventCode){
			case Ni3ItemListener.MSG_NodeShowToolTip:
			case Ni3ItemListener.MSG_MatrixPointedNodeChanged:
			case Ni3ItemListener.MSG_GraphPointedNodeChanged:
			case Ni3ItemListener.MSG_MapPointedNodeChanged: {
				nodeInHighlight = (Node) Param;
				repaint();
			}
				break;
			case Ni3ItemListener.MSG_ChartTypeChanged:
			case Ni3ItemListener.MSG_FilterChanged: {
				doc.Subgraph.filter(doc.filter, doc.getFavoritesID(), false);
				setSliderInterval(doc.Subgraph, doc.filter, false, false);
				doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
				doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
				setSliderInterval(doc.Subgraph, doc.filter, false, false);
			}
				break;

			case Ni3ItemListener.MSG_SchemaChanged: {
				setVisible(false);
			}
				break;

			case Ni3ItemListener.MSG_SubgraphChanged: {
				doc.Subgraph.filter(doc.filter, doc.getFavoritesID(), false);
				setSliderInterval(doc.Subgraph, doc.filter, false, false);
				doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
				doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
				setSliderInterval(doc.Subgraph, doc.filter, false, false);
			}
				break;

			case Ni3ItemListener.MSG_SubgraphObjectsRemoved: {
				setSliderInterval(doc.Subgraph, doc.filter, true, true);
			}
				break;
		}
	}

	public void componentHidden(ComponentEvent e){
	}

	public void componentMoved(ComponentEvent e){
	}

	public void componentResized(ComponentEvent e){
		Rectangle rct = getBounds();
		int widthOfSlider = Math.max(rct.width - columnX[2] - 20 - (labelWidth + 20), 0);
		int y = 10;

		if (widthOfSlider > 30 && !chartParams.getChartAttributes().isEmpty()){
			for (int n = 0; n < chartParams.getChartAttributes().size(); n++){
				filterValuesSliders.get(n).setBounds(columnX[4] + labelWidth, y, widthOfSlider, 25);

				y += 30;
			}
		}

		repaint();
	}

	public void componentShown(ComponentEvent e){
	}

	@Override
	public void actionPerformed(ActionEvent e){
		ChartParams chartParams = doc.getChartParams(entity.ID);
		if ("MinMaxScale".equals(e.getActionCommand())){
			double minScale = Double.MAX_VALUE, maxScale = Double.MIN_VALUE;

			if (chartParams != null){
				minScale = Math.min(minScale, chartParams.getChartMinScale());
				maxScale = Math.max(maxScale, chartParams.getChartMaxScale());
			}

			JInputValuesDialog dlg2 = new JInputValuesDialog(UserSettings.getWord("Min/Max scale"), new String[] {
			        UserSettings.getWord("Min scale"), UserSettings.getWord("Max Scale") }, new Object[] { minScale,
			        maxScale }, new String[] { "####", "####" });

			dlg2.setBounds(filterSlider.getDlgX(), filterSlider.getDlgY(), dlg2.getWidth(), dlg2.getHeight());
			dlg2.setVisible(true);
			if (dlg2.getReturnStatus() == JInputValuesDialog.RET_CANCEL)
				return;

			chartParams.setChartMinScale((Double) dlg2.getValue(0));
			chartParams.setChartMaxScale((Double) dlg2.getValue(1));
			doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFrame, this, doc.filter);
		} else if ("Relative".equals(e.getActionCommand())){
			chartParams.setAbsolute(false);
			doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFrame, this, doc.filter);
		} else if ("Absolute".equals(e.getActionCommand())){
			chartParams.setAbsolute(true);
			doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFrame, this, doc.filter);
		} else if ("ResetScale".equals(e.getActionCommand())){
			chartParams.reset();
			chartParams.setAbsolute(isAbsoluteScaleAvailable());
			doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFrame, this, doc.filter);
		} else if ("Reset".equals(e.getActionCommand())){
			resetChartFilters();
		} else if (pieChartButton.equals(e.getSource())){
			doc.setChartType(entity.ID, ChartType.Pie);
		} else if (barChartButton.equals(e.getSource())){
			doc.setChartType(entity.ID, ChartType.Bar);
		}
	}

	protected void resetChartFilters(){
		doc.filter.resetChartFilter(entity.ID);
		doc.Subgraph.filter(doc.filter, doc.getFavoritesID(), true);
		doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
		setSliderInterval(doc.Subgraph, doc.filter, true, true);
		doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFilterSlider, this, doc.filter);
	}

	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		if (!visible){
			doc.setChartLegendVisible(entity.ID, visible);
		}
	}

	public boolean isAbsoluteScaleAvailable(){
		return doc.getCurrentChartId() != SNA.SNA_CHART_ID
		        && (doc.getCurrentChartId() != DynamicChart.DYNAMIC_CHART_ID || !doc.getChartParams(entity.ID)
		                .hasDynamicAttributes());
	}

	class JDoubleMainLegendSlider extends JDoubleSlider{
		LegendFrame legend;

		public JDoubleMainLegendSlider(LegendFrame legend, Color bckground, int ThumbSize, ActionListener parentListener,
		        String menuItems[], String menuActions[]){
			super(bckground, ThumbSize, parentListener, menuItems, menuActions);
			this.legend = legend;
		}

		public void fillPopupMenu(JPopupMenu popup){
			JMenuItem item;

			item = new JMenuItem(UserSettings.getWord("Min/Max Scale"));
			item.setActionCommand("MinMaxScale");
			item.addActionListener(legend);
			popup.add(item);

			popup.addSeparator();

			ButtonGroup group = new ButtonGroup();

			JRadioButtonMenuItem bitem = new JRadioButtonMenuItem(UserSettings.getWord("Relative"), !chartParams
			        .isAbsolute());
			bitem.setActionCommand("Relative");
			bitem.addActionListener(legend);
			popup.add(bitem);
			group.add(bitem);

			bitem = new JRadioButtonMenuItem(UserSettings.getWord("Absolute"), chartParams.isAbsolute());
			bitem.setActionCommand("Absolute");
			bitem.addActionListener(legend);
			bitem.setEnabled(isAbsoluteScaleAvailable());
			popup.add(bitem);
			group.add(bitem);

			popup.addSeparator();

			item = new JMenuItem(UserSettings.getWord("Reset Scale"));
			item.setActionCommand("ResetScale");
			item.addActionListener(legend);
			popup.add(item);
		}
	}

	class LegendFrameChangeListener implements ChangeListener{

		private LegendFrameChangeListener(){
		}

		public void legendAggregateUpdate(final double val1, final double val2, final boolean forceUpdate,
		        final boolean resetAF, final boolean resetVS){
			final ChartFilter cFilter = doc.filter.getChartFilter(entity.ID);

			if (cFilter.getMinChartVal() != val1 || cFilter.getMaxChartVal() != val2 || forceUpdate){
				cFilter.setMinChartVal(val1);
				cFilter.setMaxChartVal(val2);

				doc.Subgraph.filter(doc.filter, doc.getFavoritesID(), false);
				doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
				setSliderInterval(doc.Subgraph, doc.filter, resetAF, resetVS, false, false);

				doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
				doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
				setSliderInterval(doc.Subgraph, doc.filter, resetAF, resetVS, false, false);

				doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFilterSlider,
				        LegendFrame.this, doc.filter);
			}
		}

		@Override
		public void stateChanged(final ChangeEvent e){
			final ChartFilter cFilter = doc.filter.getChartFilter(entity.ID);

			final JComponent source = (JComponent) e.getSource();
			if (source.getName().equals("FilterSlider")){
				final JDoubleSlider slider = (JDoubleSlider) source;
				legendAggregateUpdate(slider.getValue(0), slider.getValue(1), false, false, true);
				doc.dispatchEvent(Ni3ItemListener.MSG_ChartAFChanged, Ni3ItemListener.SRC_LegendFilterSlider,
				        LegendFrame.this, doc.filter);
			} else if (source.getName().equals("NoEmptyCharts")){
				final JCheckBox box = (JCheckBox) source;

				doc.filter.FilterEmptyCharts = box.isSelected();
				doc.Subgraph.filter(doc.filter, doc.getFavoritesID());

				doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFilterSlider,
				        LegendFrame.this, doc.filter);
			} else if (source.getName().charAt(0) == 'V'){
				final JDoubleSlider slider = (JDoubleSlider) source;

				final int i = Integer.valueOf(slider.getName().substring(1));

				double val1, val2;

				val1 = slider.getValue(0);
				val2 = slider.getValue(1);

				if (cFilter.getMinChartAttrVal(i) != val1 || cFilter.getMaxChartAttrVal(i) != val2){
					cFilter.setMinChartAttrVal(i, val1);
					cFilter.setMaxChartAttrVal(i, val2);

					doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
					doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);

					doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFilterSlider,
					        LegendFrame.this, doc.filter);
					doc.dispatchEvent(Ni3ItemListener.MSG_ChartVSChanged, Ni3ItemListener.SRC_LegendFilterSlider,
					        LegendFrame.this, doc.filter);
				}
			} else if (source.getName().charAt(0) == 'C'){
				final JCheckBox box = (JCheckBox) source;

				final int i = Integer.valueOf(box.getName().substring(1));

				final boolean state = !box.isSelected();

				if (cFilter.isExcluded(i) != state){
					cFilter.setExcluded(i, state);
					cFilter.setMaxChartVal(Double.POSITIVE_INFINITY);
					cFilter.setMinChartVal(Double.NEGATIVE_INFINITY);

					doc.Subgraph.filter(doc.filter, doc.getFavoritesID(), false);
					setSliderInterval(doc.Subgraph, doc.filter, true, true);
					doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
					doc.Subgraph.recalculateGraphValues(doc.getChartParams(), doc.filter, false);
					setSliderInterval(doc.Subgraph, doc.filter, true, true);

					doc.dispatchEvent(Ni3ItemListener.MSG_ChartFilterChanged, Ni3ItemListener.SRC_LegendFilterSlider,
					        LegendFrame.this, doc.filter);
					doc.dispatchEvent(Ni3ItemListener.MSG_ChartTOChanged, Ni3ItemListener.SRC_LegendFilterSlider,
					        LegendFrame.this, doc.filter);
				}

				repaint();
			}
		}

	}
}
