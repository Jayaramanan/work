package com.ni3.ag.navigator.client.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.domain.UserSettings;

public class ErrorDialog extends Ni3Dialog{
    private static final int NO_DETAILS_HEIGHT = 130;
    private static final int WITH_DETAILS_HEIGHT = 300;
    private static final int WIDTH = 500;

    private JLabel iconLabel;
    private JLabel titleLabel;
    private JButton detailsButton;
    private JTextPane detailsArea;
    private boolean details;
    private JButton okButton;

    public ErrorDialog(Container container, String title, String content) {
        setTitle(UserSettings.getWord("Error"));
        initComponents();
        titleLabel.setText(title);
        setSize(WIDTH, NO_DETAILS_HEIGHT);
        setLocationRelativeTo(container);

        detailsArea.setText(content);
        
        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showHideDetails();
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        });
    }

    private void showHideDetails() {
        if(details){
            detailsArea.setVisible(false);
            setSize(getWidth(), NO_DETAILS_HEIGHT);
        }
        else{
            detailsArea.setVisible(true);
            setSize(getWidth(), WITH_DETAILS_HEIGHT);
        }
        details = !details;
    }

    private void initComponents() {
        Container c = getContentPane();
        SpringLayout layout = new SpringLayout();
        c.setLayout(layout);
        
        Icon icon = new ImageIcon(getClass().getResource("/error.png"));
        iconLabel = new JLabel(icon);
        layout.putConstraint(SpringLayout.NORTH, iconLabel, 10, SpringLayout.NORTH, c);
        layout.putConstraint(SpringLayout.WEST, iconLabel, 10, SpringLayout.WEST, c);
        c.add(iconLabel);

        titleLabel = new JLabel();
        layout.putConstraint(SpringLayout.NORTH, titleLabel, 13, SpringLayout.NORTH, c);
        layout.putConstraint(SpringLayout.WEST, titleLabel, 10, SpringLayout.EAST, iconLabel);
        layout.putConstraint(SpringLayout.EAST, titleLabel, -10, SpringLayout.EAST, c);
        c.add(titleLabel);
        
        detailsButton = new JButton("...");
        layout.putConstraint(SpringLayout.NORTH, detailsButton, 10, SpringLayout.SOUTH, iconLabel);
        layout.putConstraint(SpringLayout.WEST, detailsButton, 10, SpringLayout.WEST, c);
        c.add(detailsButton);

        okButton = new JButton(UserSettings.getWord("Ok"));
        layout.putConstraint(SpringLayout.NORTH, okButton, 0, SpringLayout.NORTH, detailsButton);
        layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.EAST, c);
        c.add(okButton);

        detailsArea = new JTextPane();
        JScrollPane detailsAreaPane = new JScrollPane();
        detailsAreaPane.setViewportView(detailsArea);
        layout.putConstraint(SpringLayout.NORTH, detailsAreaPane, 10, SpringLayout.SOUTH, detailsButton);
        layout.putConstraint(SpringLayout.WEST, detailsAreaPane, 10, SpringLayout.WEST, c);
        layout.putConstraint(SpringLayout.EAST, detailsAreaPane, -10, SpringLayout.EAST, c);
        layout.putConstraint(SpringLayout.SOUTH, detailsAreaPane, -10, SpringLayout.SOUTH, c);
        c.add(detailsAreaPane);
    }
}
