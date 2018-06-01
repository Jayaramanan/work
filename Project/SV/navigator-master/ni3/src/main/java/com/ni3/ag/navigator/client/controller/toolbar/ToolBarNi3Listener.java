package com.ni3.ag.navigator.client.controller.toolbar;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.gui.ToolBarPanel;

public class ToolBarNi3Listener implements Ni3ItemListener {
    private ToolBarPanel toolBarPanel;

    public ToolBarNi3Listener(ToolBarController toolBarController) {
        this.toolBarPanel = toolBarController.getToolbarPanel();
    }

    @Override
   	public int getListenerType(){
   		return Ni3ItemListener.SRC_OtherGUIComponents;
   	}

    @Override
    public void event(int eventCode, int sourceID, Object source, Object param) {
        if (Ni3ItemListener.MSG_QueryNumberChanged == eventCode && param != null) {
            int newQueryCount = (Integer) param;
            toolBarPanel.setQueryNumber(newQueryCount);
        } else if (Ni3ItemListener.MSG_StatusChanged == eventCode) {
            toolBarPanel.showStatus(param instanceof String ? (String) param : "");
        } else if (Ni3ItemListener.MSG_SearchNewChanged == eventCode) {
            toolBarPanel.updateSearchButtonState((Boolean) param);
        }
    }
}
