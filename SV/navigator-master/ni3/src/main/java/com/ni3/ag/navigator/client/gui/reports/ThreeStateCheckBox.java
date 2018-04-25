/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.reports;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

public class ThreeStateCheckBox extends JCheckBox{
	private static final long serialVersionUID = 8335025657755273773L;

	/** This is a type-safe enumerated type */
	public static class State{
		private State(){
		}
	}

	public static final State NOT_SELECTED = new State();
	public static final State SELECTED = new State();
	public static final State PARTIAL = new State();

	private final TristateDecorator model;

	public ThreeStateCheckBox(String text, Icon icon, State initial){
		super(text, icon);
		// Add a listener for when the mouse is pressed
		super.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				grabFocus();
				model.nextState();
			}
		});

		// set the model to the adapted model
		model = new TristateDecorator(getModel());
		setModel(model);
		setState(initial);
	}

	public ThreeStateCheckBox(String text, State initial){
		this(text, null, initial);
	}

	public ThreeStateCheckBox(String text){
		this(text, NOT_SELECTED);
	}

	public ThreeStateCheckBox(){
		this(null);
	}

	/** No one may add mouse listeners, not even Swing! */
	public void addMouseListener(MouseListener l){
	}

	/**
	 * Set the new state to either SELECTED, NOT_SELECTED or PARTIAL. If state == null, it is treated as NOT_SELECTED.
	 */
	public void setState(State state){
		model.setState(state);
	}

	/**
	 * Return the current state, which is determined by the selection status of the model.
	 */
	public State getState(){
		return model.getState();
	}

	public void setSelected(boolean b){
		if (b){
			setState(SELECTED);
		} else{
			setState(NOT_SELECTED);
		}
	}

	private class TristateDecorator implements ButtonModel{
		private final ButtonModel other;

		private TristateDecorator(ButtonModel other){
			this.other = other;
		}

		private void setState(State state){

			if (SELECTED.equals(state)){
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else if (PARTIAL.equals(state)){
				other.setArmed(true);
				setPressed(true);
				setSelected(true);
			} else{ // null or NOT_SELECTED)
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			}
		}

		/**
		 * The current state is embedded in the selection / armed state of the model.
		 * 
		 * We return the SELECTED state when the checkbox is selected but not armed, PARTIAL state when the checkbox is
		 * selected and armed (grey) and NOT_SELECTED when the checkbox is deselected.
		 */
		private State getState(){
			if (isSelected() && !isArmed()){
				// normal black tick
				return SELECTED;
			} else if (isSelected() && isArmed()){
				// partially grey tick
				return PARTIAL;
			} else{
				// normal deselected
				return NOT_SELECTED;
			}
		}

		private void nextState(){
			State current = getState();
			if (NOT_SELECTED.equals(current)){
				setState(SELECTED);
			} else if (SELECTED.equals(current)){
				setState(NOT_SELECTED);
			} else if (PARTIAL.equals(current)){
				setState(NOT_SELECTED);
			}
		}

		/** Filter: No one may change the armed status except us. */
		public void setArmed(boolean b){
		}

		/**
		 * We disable focusing on the component when it is not enabled.
		 */
		public void setEnabled(boolean b){
			setFocusable(b);
			other.setEnabled(b);
		}

		/**
		 * All these methods simply delegate to the "other" model that is being decorated.
		 */
		public boolean isArmed(){
			return other.isArmed();
		}

		public boolean isSelected(){
			return other.isSelected();
		}

		public boolean isEnabled(){
			return other.isEnabled();
		}

		public boolean isPressed(){
			return other.isPressed();
		}

		public boolean isRollover(){
			return other.isRollover();
		}

		public void setSelected(boolean b){
			other.setSelected(b);
		}

		public void setPressed(boolean b){
			other.setPressed(b);
		}

		public void setRollover(boolean b){
			other.setRollover(b);
		}

		public void setMnemonic(int key){
			other.setMnemonic(key);
		}

		public int getMnemonic(){
			return other.getMnemonic();
		}

		public void setActionCommand(String s){
			other.setActionCommand(s);
		}

		public String getActionCommand(){
			return other.getActionCommand();
		}

		public void setGroup(ButtonGroup group){
			other.setGroup(group);
		}

		public void addActionListener(ActionListener l){
			other.addActionListener(l);
		}

		public void removeActionListener(ActionListener l){
			other.removeActionListener(l);
		}

		public void addItemListener(ItemListener l){
			other.addItemListener(l);
		}

		public void removeItemListener(ItemListener l){
			other.removeItemListener(l);
		}

		public void addChangeListener(ChangeListener l){
			other.addChangeListener(l);
		}

		public void removeChangeListener(ChangeListener l){
			other.removeChangeListener(l);
		}

		public Object[] getSelectedObjects(){
			return other.getSelectedObjects();
		}
	}
}
