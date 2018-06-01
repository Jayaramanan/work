package com.ni3.ag.navigator.client.gui.graph.layoutManager.layoutManagerSettings;

import java.util.HashSet;
import java.util.Set;

import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class HierarchyManagerSettings{
    private static final String layoutSettingsNodeName = "layoutSettings";
    private static final String verticalItemsNode = "verticalItems";
    private static final String horizontalItemsNode = "horizontalItems";

	private boolean hideHierarchyEdges;
    private Set<Integer> verticalNodes = new HashSet<Integer>();
    private Set<Integer> horizontalNodes = new HashSet<Integer>();

    public HierarchyManagerSettings(){
	}

	public void editSettings(){
		DlgHierarchyManagerSettings dlg = new DlgHierarchyManagerSettings(this);

		dlg.setVisible(true);
	}

    public boolean isVerticalNode(int id) {
        return verticalNodes.contains(id);
    }

    public boolean isHorizontalNode(int id) {
        return horizontalNodes.contains(id);
    }

    public void addHorizontalNode(int id) {
        verticalNodes.remove(id);
        horizontalNodes.add(id);
    }

    public void addVerticalNode(int id) {
        horizontalNodes.remove(id);
        verticalNodes.add(id);
    }

    public void setAutoNode(int id) {
        horizontalNodes.remove(id);
        verticalNodes.remove(id);
    }

    public boolean hideHierarchyEdges() {
        return hideHierarchyEdges;
    }

    public void setHideHierarchyEdges(boolean hideHierarchyEdges) {
        this.hideHierarchyEdges = hideHierarchyEdges;
    }

    public void fromXML(NanoXML xml) {
        if(xml == null)
            return;
        NanoXMLAttribute attr;
        while ((attr = xml.Tag.getNextAttribute()) != null){
            if("hideHierarchyEdges".equals(attr.Name))
                hideHierarchyEdges = Boolean.parseBoolean(attr.Value);
        }
        NanoXML node;
        NanoXML itemNode;
        while((node = xml.getNextElement()) != null){
            Set<Integer> dest;
            if(verticalItemsNode.equals(node.Tag.Name))
                dest = verticalNodes;
            else if(horizontalItemsNode.equals(node.Tag.Name))
                dest = horizontalNodes;
            else
                continue;
            while((itemNode = node.getNextElement()) != null){
                if(!"item".equals(itemNode.Tag.Name))
                    continue;
                NanoXMLAttribute value = itemNode.Tag.getAttribute("value");
                if(value == null)
                    continue;
                dest.add(value.getIntegerValue());
            }
        }
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(layoutSettingsNodeName).append(" hideHierarchyEdges='").append(hideHierarchyEdges).append("'").append(">");

        sb.append("<").append(verticalItemsNode).append(">");
        for(int i : verticalNodes)
            sb.append("<item value='").append(i).append("'/>");
        sb.append("</").append(verticalItemsNode).append(">");

        sb.append("<").append(horizontalItemsNode).append(">");
        for(int i : horizontalNodes)
            sb.append("<item value='").append(i).append("'/>");
        sb.append("</").append(horizontalItemsNode).append(">");

        sb.append("</").append(layoutSettingsNodeName).append(">");
        return sb.toString();
    }
}
