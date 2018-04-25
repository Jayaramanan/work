/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.search;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.query.Condition;
import com.ni3.ag.navigator.client.domain.query.Operation;
import com.ni3.ag.navigator.client.domain.query.Order;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gui.CheckValueIntegrity;
import com.ni3.ag.navigator.client.gui.common.JValueTree;
import com.ni3.ag.navigator.client.gui.customlayouts.GridLayout2;
import com.ni3.ag.navigator.client.model.Ni3Document;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class EntitySearchPane extends JScrollPane{
	private static final Logger log = Logger.getLogger(EntitySearchPane.class);
	private static final String[] orderItems = { "", "1" };
	private Entity activeEntity;
	private JPanel panel;
	private Ni3Document doc;

	private JTextField textField;
	private int count;
	private int maxSortOrder;
	private int[] sortOrder;

	public EntitySearchPane(Ni3Document doc){
		this.doc = doc;

		panel = new JPanel();
		setViewportView(panel);

		maxSortOrder = 0;
	}

	public void makeAttributesPanel(Entity entity, CheckValueIntegrity check){
		panel.removeAll();

		activeEntity = entity;
		count = 0;

		maxSortOrder = 0;
		String[] ascdesc = { UserSettings.getWord("Asc"), UserSettings.getWord("Desc") };

		count = entity.getInAdvancedSearchAttributes().size();

		panel.setLayout(new GridLayout2(count + 1, 5, 2, 2));

		int colPrefHeight = 25;

		panel.add(new JLabel(UserSettings.getWord("Attribute"), JLabel.LEFT));
		panel.add(new JLabel(UserSettings.getWord("Operation"), JLabel.CENTER));
		panel.add(new JLabel(UserSettings.getWord("Values"), JLabel.CENTER));
		panel.add(new JLabel(UserSettings.getWord("Sort"), JLabel.CENTER));
		panel.add(new JLabel(UserSettings.getWord("A/D"), JLabel.CENTER));

		count = 0;
		FontMetrics fontMetrics = getFontMetrics(getFont());
		Graphics graphics = getGraphics();
		for (Attribute a : entity.getInAdvancedSearchAttributes()){
			JCheckBox polje1Label = new JCheckBox(a.label, false);
			polje1Label.setName(a.label + "CheckBox");

			polje1Label.setActionCommand("R" + count);
			polje1Label.addChangeListener(new EnableAttributeCheckBoxListener());
			int width = (int) fontMetrics.getStringBounds(a.label, graphics).getWidth() + 30;
			int max = width > 200 ? width : 200;
			polje1Label.setPreferredSize(new Dimension(width, colPrefHeight));
			polje1Label.setMinimumSize(new Dimension(width, colPrefHeight));
			polje1Label.setMaximumSize(new Dimension(max, colPrefHeight));

			panel.add(polje1Label);

			JComboBox oper = getOperationComboBox(a);
			oper.setPreferredSize(new Dimension(85, colPrefHeight));
			oper.setName(a.label + "Operation");
			oper.setMinimumSize(new Dimension(85, colPrefHeight));
			oper.setMaximumSize(new Dimension(85, colPrefHeight));
			oper.setVisible(false);
			panel.add(oper);

			if (a.predefined){
				JValueTree tree = new JValueTree(a, check, doc.SYSGroupPrefilter);
				tree.setName(a.label + "Value");
				JScrollPane treePanel = new JScrollPane(tree);

				treePanel.setPreferredSize(new Dimension(50, 3 + (int) (16.5 * Math.min(9, tree
						.getChildCount((DefaultMutableTreeNode) tree.getModel().getRoot())))));
				treePanel.setVisible(false);
				panel.add(treePanel);
			} else{
				if (a.formatFactory != null){
					textField = new JFormattedTextField();
					DefaultFormatterFactory factory = getFormatterFactory(a);
					((JFormattedTextField) textField).setFormatterFactory(factory);
					((JFormattedTextField) textField).setFocusLostBehavior(JFormattedTextField.COMMIT);

					String tooltip = getTooltip(a);
					if (tooltip != null){
						textField.setToolTipText(tooltip);
					}
				} else{
					textField = new JTextField();
				}
				textField.setName(a.label + "Value");

				InputVerifier vf = a.getDataType().getInputVerifier();

				if (vf != null)
					textField.setInputVerifier(vf);

				textField.setPreferredSize(new Dimension(100, colPrefHeight));
				textField.setMinimumSize(new Dimension(100, colPrefHeight));
				textField.setMaximumSize(new Dimension(200, colPrefHeight));

				textField.setVisible(false);
				panel.add(textField);
			}

			JComboBox cb = new JComboBox(orderItems);
			cb.setName(a.label + "Sort");
			cb.setPreferredSize(new Dimension(35, colPrefHeight));
			cb.setMinimumSize(new Dimension(35, colPrefHeight));
			cb.setMaximumSize(new Dimension(35, colPrefHeight));
			cb.setActionCommand("C" + count);
			cb.addItemListener(new OrderComboBoxItemListener());
			panel.add(cb);
			if (a.multivalue){
				// hide sort combobox for multivalue attributes
				cb.setVisible(false);
			}

			cb = new JComboBox(ascdesc);
			cb.setName(a.label + "Order");
			cb.setPreferredSize(new Dimension(50, colPrefHeight));
			cb.setMinimumSize(new Dimension(50, colPrefHeight));
			cb.setMaximumSize(new Dimension(50, colPrefHeight));
			cb.setVisible(false);
			panel.add(cb);

			count++;
		}

		sortOrder = new int[count];
		panel.repaint();
	}

	private DefaultFormatterFactory getFormatterFactory(Attribute attr){
		AbstractFormatter editFormatter = attr.formatFactory.getEditFormatter();
		return new DefaultFormatterFactory(editFormatter, editFormatter, editFormatter);
	}

	private String getTooltip(Attribute attr){
		String tooltip = null;
		if (attr.isDateAttribute()){
			Date now = new Date(System.currentTimeMillis());
			try{
				AbstractFormatter editFormatter = attr.formatFactory.getEditFormatter();
				String format = editFormatter.valueToString(now);
				tooltip = UserSettings.getWord("e.g.") + " " + format;
			} catch (ParseException e){
				log.error("Invalid date format");
			}
		}
		return tooltip;
	}

	public void clearPanel(){
		maxSortOrder = 0;
		Arrays.fill(sortOrder, 0);

		int l = panel.getComponentCount();
		for (int n = 0; n < l; n++){
			Component c = panel.getComponent(n);

			if (c instanceof javax.swing.JCheckBox){
				JCheckBox cb = (JCheckBox) c;
				cb.setSelected(false);
			}
			if (c instanceof JComboBox){
				JComboBox cb = (JComboBox) c;
				cb.getModel().setSelectedItem(Attribute.nullValue);
				cb.setSelectedIndex(0);

				if ("XXX".equals(cb.getName()))
					c.setVisible(false);
				else if (cb.getActionCommand() != null && cb.getActionCommand().charAt(0) == 'C'){
					cb.setModel(new DefaultComboBoxModel(orderItems));
				}
			} else if (c instanceof JTextField){
				JTextField text = (JTextField) c;
				text.setText("");

				c.setVisible(false);
			} else if (c instanceof JScrollPane){
				Component c1 = ((JScrollPane) c).getViewport().getComponent(0);
				if (c1 instanceof JValueTree){
					JValueTree tree = (JValueTree) c1;
					tree.clearValues();
				}

				c.setVisible(false);
			}
		}
		renumberSortCombos(false);
	}

	public Section getQuerySection(){
		int n = 0;

		Section section = new Section("", activeEntity);

		JCheckBox cbox;
		Object ctrl;
		JComboBox operation;
		Object value;

		for (Attribute a : activeEntity.getInAdvancedSearchAttributes()){
			cbox = (JCheckBox) panel.getComponent(5 + n * 5);
			if (cbox.isSelected()){
				value = "";
				operation = (JComboBox) panel.getComponent(5 + n * 5 + 1);
				ctrl = panel.getComponent(5 + n * 5 + 2);

				if (ctrl instanceof JFormattedTextField){
					try{
						((JFormattedTextField) ctrl).commitEdit();

						value = ((JFormattedTextField) ctrl).getValue();
					} catch (ParseException e){
						JOptionPane.showMessageDialog(this, UserSettings.getWord("Invalid data entered"), UserSettings
								.getWord("Data validation"), JOptionPane.INFORMATION_MESSAGE);
						((JFormattedTextField) ctrl).requestFocus();
						panel.scrollRectToVisible(((JFormattedTextField) ctrl).getBounds());
						return null;
					}
				} else if (ctrl instanceof JTextField){
					String s;
					s = ((JTextField) ctrl).getText();
					s = s.replace("\t", " ");
					s = s.replace("\n", " ");
					s = s.trim();

					value = s;
				} else if (ctrl instanceof JScrollPane){
					Component c1 = ((JScrollPane) ctrl).getViewport().getComponent(0);
					if (c1 instanceof JValueTree){
						JValueTree tree = (JValueTree) c1;
						value = tree.getValues();
					}
				}

				if (value != null){
					if (value instanceof String){
						if (((String) value).length() > 0)
							section.add(new Condition(a, ((Operation) (operation.getSelectedItem())).operation, value));
					} else
						section.add(new Condition(a, ((Operation) (operation.getSelectedItem())).operation, value));
				}
			}

			JComboBox c2 = (JComboBox) panel.getComponent(5 + n * 5 + 3);
			JComboBox asc = (JComboBox) panel.getComponent(5 + n * 5 + 4);

			if (c2.getSelectedIndex() > 0){
				section.add(new Order(a, asc.getSelectedIndex() == 0), c2.getSelectedIndex() - 1);
			}

			n++;
		}

		return section;
	}

	public JComboBox getOperationComboBox(Attribute atAtr){
		if (atAtr.predefined){
			if (atAtr.multivalue){
				Operation[] st = { new Operation(UserSettings.getWord("At least one"), "AtLeastOne"),
						new Operation(UserSettings.getWord("All"), "All"),
						new Operation(UserSettings.getWord("None of"), "NoneOf") };
				return new JComboBox(st);
			} else{
				Operation[] st = { new Operation("="), new Operation("<>") };
				return new JComboBox(st);
			}
		} else if (atAtr.isTextAttribute() || atAtr.isURLAttribute()){
			Operation[] st = { new Operation("="), new Operation("<>"), new Operation("~") };
			return new JComboBox(st);
		} else if (atAtr.isNumericAttribute()){
			if (atAtr.multivalue){
				Operation[] st = { new Operation("="), new Operation("<>") };
				return new JComboBox(st);
			} else{
				Operation[] st = { new Operation(">="), new Operation("="), new Operation("<="), new Operation("<>") };
				return new JComboBox(st);
			}
		} else if (atAtr.isBooleanAttribute()){
			Operation[] st = { new Operation("="), new Operation("<>") };
			return new JComboBox(st);
		} else if (atAtr.isDateAttribute()){
			Operation[] st = { new Operation("="), new Operation("<>"), new Operation(">="), new Operation("<=") };
			return new JComboBox(st);
		}

		return new JComboBox();
	}

	void renumberSortCombos(boolean ChangeModel){
		String st[] = null;
		String sts[] = null;

		if (ChangeModel){
			st = new String[maxSortOrder + 2];
			sts = new String[maxSortOrder + 1];
			st[0] = "";
			for (int n = 1; n <= maxSortOrder + 1; n++){
				st[n] = Integer.toString(n);
				if (n != maxSortOrder + 1)
					sts[n] = Integer.toString(n);
			}
		}

		JComboBox c2, asc;
		for (int n = 0; n < count; n++){
			c2 = (JComboBox) panel.getComponent(5 + n * 5 + 3);
			asc = (JComboBox) panel.getComponent(5 + n * 5 + 4);
			if (ChangeModel){
				if (sortOrder[n] == 0){
					c2.setModel(new DefaultComboBoxModel(st));
				} else{
					c2.setModel(new DefaultComboBoxModel(sts));
				}
			}

			c2.setSelectedIndex(sortOrder[n]);
			asc.setVisible(sortOrder[n] > 0);
		}
	}

	public static boolean inChange = false;

	private class EnableAttributeCheckBoxListener implements ChangeListener{
		private boolean currentState;

		@Override
		public void stateChanged(ChangeEvent e){
			JCheckBox cb = (JCheckBox) e.getSource();
			boolean toShow = cb.isSelected();
			if (currentState == toShow){
				return;
			}
			currentState = toShow;
			int index = 5 + Integer.decode(cb.getActionCommand().substring(1)) * 5;
			panel.getComponent(index + 1).setVisible(toShow);
			panel.getComponent(index + 2).setVisible(toShow);

			panel.revalidate();
			panel.repaint();
		}
	}

	private class OrderComboBoxItemListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent e){
			if (inChange)
				return;
			log.debug(e);
			inChange = true;
			JComboBox order = (JComboBox) e.getSource();
			int index = Integer.decode(order.getActionCommand().substring(1));
			JComboBox sort = (JComboBox) panel.getComponent(5 + index * 5 + 3);

			int selectedSortIndex = sort.getSelectedIndex();
			if (selectedSortIndex <= 0){
				if (maxSortOrder > 0){
					maxSortOrder--;

					for (int n = 0; n < count; n++){
						if (n != index && sortOrder[n] > sortOrder[index]){
							sortOrder[n]--;
						}
					}

					sortOrder[index] = 0;
					renumberSortCombos(true);
				}
			} else if (selectedSortIndex > 0 && sortOrder[index] == 0){
				maxSortOrder++;

				for (int n = 0; n < count; n++){
					if (n != index && sortOrder[n] >= selectedSortIndex){
						sortOrder[n]++;
					}
				}

				sortOrder[index] = selectedSortIndex;
				renumberSortCombos(true);
			} else if (selectedSortIndex > 0 && sortOrder[index] < selectedSortIndex){
				for (int n = 0; n < count; n++){
					if (n != index && (sortOrder[n] > sortOrder[index]) && (sortOrder[n] <= selectedSortIndex)){
						sortOrder[n]--;
					}
				}
				sortOrder[index] = selectedSortIndex;
				renumberSortCombos(false);
			} else if (selectedSortIndex > 0 && sortOrder[index] > selectedSortIndex){
				for (int n = 0; n < count; n++){
					if (n != index && (sortOrder[n] >= selectedSortIndex) && (sortOrder[n] < sortOrder[index])){
						sortOrder[n]++;
					}
				}
				sortOrder[index] = selectedSortIndex;
				renumberSortCombos(false);
			}

			panel.revalidate();
			panel.repaint();

			inChange = false;
		}
	}
}
