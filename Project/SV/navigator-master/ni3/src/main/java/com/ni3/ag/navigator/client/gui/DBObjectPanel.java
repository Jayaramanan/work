/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.common.JValueTree;
import com.ni3.ag.navigator.client.gui.customlayouts.GridLayout3;
import com.ni3.ag.navigator.client.gui.jtreecombobox.TreeListCellRenderer;
import com.ni3.ag.navigator.client.gui.jtreecombobox.TreeListModel;
import com.ni3.ag.navigator.client.model.Ni3Document;

@SuppressWarnings("serial")
public class DBObjectPanel extends JScrollPane{
	private static final Logger log = Logger.getLogger(DBObjectPanel.class);
	private static final String INVALID_DATA_MSG = "Invalid data entered";
	private static final String MANDATORY_MSG = "Fields marked with red are mandatory";
	private static final String REPLICATE_MSG = "This node is being created by duplicating another node, please change at least one attribute to be able to save";

	DBObject obj;
	public Entity activeEntity;
	public JPanel panel;
	private Ni3Document doc;

	private JComboBox attrPredefined;
	private JTextField textField;
	public int count;
	@SuppressWarnings("unused")
	private boolean EmptyRow;
	public int Operation;
	public boolean locked;
	List<Attribute> attributes;

	//this field tracks whether any value has changed, needed for "Replicate node functionality;
	private InputValueChangeListener inputValueChangeListener;

	public DBObjectPanel(DBObject obj, Ni3Document doc, boolean locked){
		this.obj = obj;
		this.doc = doc;

		panel = new JPanel();
		setViewportView(panel);

		this.locked = locked;
	}

	public void MakeAttributesPanel(Entity entity, List<Attribute> attributes, CheckValueIntegrity check,
			boolean AddEmptyRow, int Operation, boolean locked, FontMetrics fontMetrics, Graphics g){
		this.locked = locked;
		this.attributes = attributes;
		EmptyRow = AddEmptyRow;

		panel.removeAll();

		//initialize the listener with changed = false
		inputValueChangeListener = new InputValueChangeListener();

		activeEntity = entity;
		if (obj != null && obj.getEntity().ID != entity.ID){
			obj.initObjectData(entity);
		}

		count = 0;
		for (Attribute a : attributes){
			if (a.isDisplayableOnEdit(locked))
				count++;
		}

		panel.setLayout(new GridLayout3(count, 2, 2, 2));

		count = 0;
		int multivalueCount = 0;

		this.Operation = Operation;

		for (Attribute a : attributes){
			if (a.isDisplayableOnEdit(locked)){
				count++;

				if (a.multivalue){
					multivalueCount++;
				}

				JLabel fieldLabel = new JLabel();
				fieldLabel.setName(a.label + "Label");
				fieldLabel.setText(a.label);

				int width = (int) (fontMetrics.getStringBounds(a.label, g).getWidth());

				fieldLabel.setPreferredSize(new Dimension(width, 25));
				fieldLabel.setMinimumSize(new Dimension(width, 25));
				fieldLabel.setMaximumSize(new Dimension(width, 25));

				if (a.isMandatoryOnEdit(locked))
					fieldLabel.setForeground(Color.red);

				panel.add(fieldLabel);

				if (a.predefined){
					if (a.multivalue){
						JValueTree tree = new JValueTree(a, check, doc.SYSGroupPrefilter);
						tree.setName(a.label);
						JScrollPane treePanel = new JScrollPane(tree);

						treePanel.setPreferredSize(new Dimension(50, 3 + (int) (16.5 * Math.min(9, tree.getModel()
								.getChildCount(tree.getModel().getRoot())))));

						treePanel.setPreferredSize(new Dimension(100, 80));
						treePanel.setMinimumSize(new Dimension(100, 80));
						treePanel.setMaximumSize(new Dimension(200, 80));

						if (obj != null)
							tree.setMultivalue((Value[]) (obj.getValue(a.ID)));

						if (!a.isEditable(locked))
							tree.setEnabled(false);

						tree.getModel().addTreeModelListener(inputValueChangeListener);
						panel.add(treePanel);
					} else{
						TreeModel treeModel = Models.getPredefinedTree(a, check, doc.SYSGroupPrefilter);
						TreeListModel model = new TreeListModel(treeModel);

						attrPredefined = new JComboBox(model);
						attrPredefined.setName(a.label);

						DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
						renderer.setLeafIcon(null);
						renderer.setClosedIcon(null);
						renderer.setOpenIcon(null);

						attrPredefined.setRenderer(new TreeListCellRenderer(treeModel, renderer));

						attrPredefined.setPreferredSize(new Dimension(100, 25));
						attrPredefined.setMinimumSize(new Dimension(100, 25));
						attrPredefined.setMaximumSize(new Dimension(200, 25));

						if (obj != null){
							if (obj.getValue(a.ID) != null){
								if (check == null || check.checkValue(entity, a, (Value) (obj.getValue(a.ID))))
									model.setSelectedItem(obj.getValue(a.ID));
							}
						}

						if (a.isMandatoryOnEdit(locked) && Operation == 1){
							attrPredefined.setSelectedItem(getFirstLeaf(treeModel));
						}

						if (!a.isEditable(locked))
							attrPredefined.setEnabled(false);

						attrPredefined.getModel().addListDataListener(inputValueChangeListener);
						panel.add(attrPredefined);
					}
				} else{
					if (a.multivalue){
						//TODO: implement value tracking for this
						JMultivalueTextField multi = new JMultivalueTextField(a.isURLAttribute());
						multi.setName(a.label);

						multi.setPreferredSize(new Dimension(100, 75));
						multi.setMinimumSize(new Dimension(100, 75));
						multi.setMaximumSize(new Dimension(200, 75));
						if (obj != null)
							multi.setItems(a, (Object[]) (obj.getValue(a.ID)));

						if (!a.isEditable(locked))
							multi.setEnabled(false);

						panel.add(multi);
					} else{
						if (a.formatFactory != null){
							textField = new JFormattedTextField();
							textField.setName(a.label);

							final AbstractFormatterFactory editFormatter = getEditFormatter(a);
							((JFormattedTextField) textField).setFormatterFactory(editFormatter);
							((JFormattedTextField) textField).setFocusLostBehavior(JFormattedTextField.COMMIT);
							if (a.isDateAttribute()){
								Date now = new Date(System.currentTimeMillis());
								try{
									String format = a.formatFactory.getEditFormatter().valueToString(now);
									textField.setToolTipText(UserSettings.getWord("e.g.") + " " + format);
								} catch (ParseException e){
									log.error("Invalid date format");
								}
							}
						} else{
							textField = new JTextField();
							textField.setName(a.label);
						}

						InputVerifier vf = a.getDataType().getInputVerifier();
						if (vf != null)
							textField.setInputVerifier(vf);

						textField.setPreferredSize(new Dimension(100, 25));
						textField.setMinimumSize(new Dimension(100, 25));
						textField.setMaximumSize(new Dimension(200, 25));
						if (obj != null)
							textField.setText(a.getDataType().editValue(obj.getValue(a.ID)));

						if (!a.isEditable(locked))
							textField.setEnabled(false);

						//this must be called after we set the text, no avoid false triggers
						textField.getDocument().addDocumentListener(inputValueChangeListener);
						panel.add(textField);
					}
				}
			}
		}

		int height = (int) (25.3 * (count - multivalueCount) + 80 * multivalueCount);
		panel.setPreferredSize(new Dimension(200, height));
		panel.setMinimumSize(new Dimension(180, height));

		panel.revalidate();
		panel.repaint();

		scrollRectToVisible(new Rectangle(1, 1, 1, 1));
	}

	private AbstractFormatterFactory getEditFormatter(Attribute a){
		final AbstractFormatter editFormatter = a.formatFactory.getEditFormatter();
		final AbstractFormatterFactory factory = new DefaultFormatterFactory(editFormatter, editFormatter, editFormatter);
		return factory;
	}

	public boolean validateForm(DlgNodePropertiesAction action){
		if (DlgNodePropertiesAction.REPLICATE == action){
			if (!inputValueChangeListener.isValuesHaveChanged()){
				showWarningMessage(null, REPLICATE_MSG);
				return false;
			}
		}

		int n = 1;
		for (Attribute a : attributes){
			if (a.isDisplayableOnEdit(locked)){

				if (a.isMandatoryOnEdit(locked)){

					if (a.predefined){
						if (a.multivalue){
							Component c1 = ((JScrollPane) panel.getComponent(n)).getViewport().getComponent(0);
							if (c1 instanceof JValueTree){
								JValueTree tree = (JValueTree) c1;
								if (tree.getMultivalueCount() == 0){
									showWarningMessage(panel.getComponent(n), MANDATORY_MSG);
									return false;
								}
							}
						} else{
							JComboBox cb = (JComboBox) panel.getComponent(n);
							DefaultMutableTreeNode item = (DefaultMutableTreeNode) cb.getSelectedItem();
							if (item == null || item.getUserObject() == null || item.getUserObject() == Attribute.nullValue){
								showWarningMessage(cb, MANDATORY_MSG);
								return false;
							}
						}
					} else{
						if (a.multivalue){
							JMultivalueTextField multi = (JMultivalueTextField) panel.getComponent(n);
							if (multi.getItemsCount() == 0){
								showWarningMessage(multi, MANDATORY_MSG);
								return false;
							}
						} else{
							if (a.formatFactory != null){
								JFormattedTextField text = (JFormattedTextField) panel.getComponent(n);
								if (text.getText() == null || text.getText().isEmpty()){
									showWarningMessage(text, MANDATORY_MSG);
									return false;
								}
								try{
									text.commitEdit();
								} catch (ParseException e){
									showWarningMessage(text, INVALID_DATA_MSG);
									return false;
								}
								Object val = text.getValue();
								if (val == null){
									showWarningMessage(text, MANDATORY_MSG);
									return false;
								}
							} else{
								JTextField text = (JTextField) panel.getComponent(n);
								String txt = text.getText().trim();
								if (txt.isEmpty()
										|| (text.getInputVerifier() != null && !text.getInputVerifier().verify(text))){
									showWarningMessage(text, txt.isEmpty() ? MANDATORY_MSG : INVALID_DATA_MSG);

									return false;
								}
							}
						}
					}
				} else if (a.formatFactory != null && panel.getComponent(n) instanceof JFormattedTextField){
					JFormattedTextField text = (JFormattedTextField) panel.getComponent(n);
					try{
						if (text.getText() != null && !text.getText().isEmpty()){
							text.commitEdit();
							Object o = text.getValue();
							if (!a.getDataType().checkValue(o)){
								showWarningMessage(text, INVALID_DATA_MSG);
								return false;
							}
						}
					} catch (ParseException e){
						final String text2 = text.getText();
						final AbstractFormatter editFormatter = a.formatFactory.getEditFormatter();
						if (text2.length() > 0
								&& (!(editFormatter instanceof MaskFormatter) || !(((MaskFormatter) editFormatter).getMask()
										.equals(text2)))){
							showWarningMessage(text, INVALID_DATA_MSG);
							return false;
						}
					}
				} else if (panel.getComponent(n) instanceof JTextField){
					JTextField text = (JTextField) panel.getComponent(n);
					String txt = text.getText().trim();
					if (!txt.isEmpty() && text.getInputVerifier() != null && !text.getInputVerifier().verify(text)){
						showWarningMessage(text, INVALID_DATA_MSG);
						return false;
					}
				}

				if (a.regEx != null){
					JTextField text = (JTextField) panel.getComponent(n);
					if (text.getText().length() > 0){
						Matcher m = a.regEx.matcher(text.getText());
						if (!m.matches()){
							showWarningMessage(text, a.valueDescription);
							return false;
						}
					}
				}

				n += 2;
			}
		}

		return true;
	}

	private void showWarningMessage(Component c, String desc){
		if (c != null) {
			c.requestFocus();
			panel.scrollRectToVisible(c.getBounds());
		}
		JOptionPane.showMessageDialog(this, UserSettings.getWord(desc), UserSettings.getWord("Data validation"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void clearPanel(){
		int n;

		int l = panel.getComponentCount();

		for (n = 0; n < l; n++){
			Component c = panel.getComponent(n);

			if (c instanceof JComboBox){
				JComboBox cb = (JComboBox) c;
				cb.getModel().setSelectedItem(Attribute.nullValue);
			} else if (c instanceof JTextField){
				JTextField text = (JTextField) c;
				text.setText("");
			} else if (c instanceof JMultivalueTextField){
				JMultivalueTextField text = (JMultivalueTextField) c;
				text.setItems(null, null);
			} else if (c instanceof JScrollPane){
				Component c1 = ((JScrollPane) c).getViewport().getComponent(0);
				if (c1 instanceof JValueTree){
					JValueTree tree = (JValueTree) c1;
					tree.clearValues();
				}
			}

		}
	}

	public DBObject fillObj(boolean newObject){
		DBObject fill;

		if (newObject){
			fill = new DBObject(activeEntity);
		} else{
			if (obj == null){
				obj = new DBObject(activeEntity);
			}

			fill = obj;
		}

		int n = 1;
		for (Attribute a : attributes){
			if (a.isDisplayableOnEdit(locked)){
				if (a.predefined){
					if (a.multivalue){
						Component c1 = ((JScrollPane) panel.getComponent(n)).getViewport().getComponent(0);
						if (c1 instanceof JValueTree){
							JValueTree tree = (JValueTree) c1;
							fill.setValue(a.ID, tree.getMultivalue());
						}
					} else{
						JComboBox cb = (JComboBox) panel.getComponent(n);
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) cb.getModel().getSelectedItem();

						if (node == null || node.getUserObject() == Attribute.nullValue)
							fill.setValue(a.ID, null);
						else
							fill.setValue(a.ID, node.getUserObject());
					}
				} else{
					if (a.multivalue){
						JMultivalueTextField text = (JMultivalueTextField) panel.getComponent(n);
						fill.setValue(a.ID, text.getItems());
					} else{
						if (a.formatFactory != null){
							JFormattedTextField text = (JFormattedTextField) panel.getComponent(n);
							Object val;
							try{
								text.commitEdit();
								val = text.getValue();
							} catch (ParseException e){
								val = null;
							}

							if (val == null)
								fill.setValue(a.ID, null);
							else if (val instanceof String){
								fill.setValue(a.ID, val.toString().replaceAll("\t+", " ").replaceAll("\\s+/g", " ").trim());
							} else
								fill.setValue(a.ID, val);

						} else{
							JTextField text = (JTextField) panel.getComponent(n);
							fill.setValue(a.ID, text.getText().replaceAll("\t+", " ").replaceAll("\\s+/g", " ").trim());
						}
					}
				}

				n += 2;
			} else
				fill.setValue(a.ID, null);
		}

		return fill;
	}

	public void restoreObj(DBObject src){
		if (src.getEntity() != activeEntity){
			return;
		}

		int n = 1;
		for (Attribute a : attributes){
			if (a.isDisplayableOnEdit(locked)){
				if (a.predefined){
					if (a.multivalue){
						Component c1 = ((JScrollPane) panel.getComponent(n)).getViewport().getComponent(0);
						if (c1 instanceof JValueTree){
							JValueTree tree = (JValueTree) c1;
							tree.setMultivalue((Value[]) src.getValue(a.ID));
						}
					} else{
						JComboBox cb = (JComboBox) panel.getComponent(n);

						cb.getModel().setSelectedItem(src.getValue(a.ID));
					}
				} else{
					if (a.multivalue){
						JMultivalueTextField text = (JMultivalueTextField) panel.getComponent(n);
						text.setItems(null, (Object[]) (src.getValue(a.ID)));
					} else{
						JTextField text = (JTextField) panel.getComponent(n);
						text.setText(a.getDataType().editValue(src.getValue(a.ID)));
					}
				}

				n += 2;
			}

		}
	}

	public boolean isEmpty(){
		int n;

		int l = panel.getComponentCount();

		for (n = 0; n < l; n++){
			Component c = panel.getComponent(n);

			if (c instanceof JComboBox){
				JComboBox cb = (JComboBox) c;
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) cb.getSelectedItem();
				if (item != null && item.getUserObject() != null && item.getUserObject() != Attribute.nullValue)
					return false;
			} else if (c instanceof JFormattedTextField){
				JFormattedTextField text = (JFormattedTextField) c;
				if (text.getValue() != null)
					return false;
			} else if (c instanceof JTextField){
				JTextField text = (JTextField) c;
				if (!text.getText().isEmpty())
					return false;
			} else if (c instanceof JMultivalueTextField){
				JMultivalueTextField text = (JMultivalueTextField) c;
				if (text.getItemsCount() > 0)
					return false;
			} else if (c instanceof JScrollPane){
				Component c1 = ((JScrollPane) c).getViewport().getComponent(0);
				if (c1 instanceof JValueTree){
					JValueTree tree = (JValueTree) c1;
					if (tree.getMultivalueCount() != 0)
						return false;
				}

				c.setVisible(false);
			}
		}

		return true;
	}

	private Object getFirstLeaf(TreeModel treeModel){
		DefaultMutableTreeNode node = null;
		Object o;

		for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++){
			node = (DefaultMutableTreeNode) treeModel.getChild(treeModel.getRoot(), i);
			if (node.isLeaf()){
				o = node.getUserObject();
				if (o instanceof Value)
					if (!((Value) o).getLabel().trim().isEmpty())
						return node;
			} else{
				node = (DefaultMutableTreeNode) getLeaf(node);
				if (node != null)
					return node;
			}
		}

		return node;
	}

	private Object getLeaf(DefaultMutableTreeNode node){
		DefaultMutableTreeNode Child = null;
		Object o;

		for (int i = 0; i < node.getChildCount(); i++){
			Child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (Child.isLeaf()){
				o = Child.getUserObject();
				if (o instanceof Value)
					if (!((Value) o).getLabel().trim().isEmpty())
						return Child;
			} else
				return getLeaf((DefaultMutableTreeNode) node.getChildAt(i));
		}
		return Child;
	}
}
