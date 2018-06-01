/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.favorites;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.common.Ni3Dialog;
import com.ni3.ag.navigator.client.controller.favorites.FavoritesMouseListener;
import com.ni3.ag.navigator.client.controller.favorites.FavoritesTreeActionListener;
import com.ni3.ag.navigator.client.domain.Favorite;
import com.ni3.ag.navigator.client.domain.Folder;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.model.FavoritesModel;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;

@SuppressWarnings("serial")
public class DlgCreateFavoritesFolder extends Ni3Dialog implements TreeSelectionListener{

	private boolean cancel;

	private JTextField fname;
	private JTextArea fdescription;
	private FavoritesTree tree;

	private Favorite fav;

	private FavoriteMode mode = FavoriteMode.FAVORITE;
	private boolean createFavorite = false;

	public DlgCreateFavoritesFolder(Ni3Document doc, final boolean createFavorite, final Favorite fav){
		super();
		this.createFavorite = createFavorite;
		addWindowListener(new java.awt.event.WindowAdapter(){
			@Override
			public void windowClosing(final java.awt.event.WindowEvent evt){
				SystemGlobals.MainFrame.favoritesMenu.refreshFavorites();
			}
		});

		this.fav = fav;
		if (fav != null){
			mode = fav.getMode();
		}

		cancel = true;

		initComponents(doc);

		tree.addTreeSelectionListener(this);
		final FavoritesTreeActionListener favListener = new FavoritesTreeActionListener(doc, tree);
		tree.addMouseListener(new FavoritesMouseListener(tree, favListener));

		setInitialSelection(doc, createFavorite, fav);
	}

	private void setInitialSelection(Ni3Document doc, final boolean createFavorite, final Favorite fav){
		final FavoritesModel fModel = doc.getFavoritesModel();
		Object[] selection = null;
		if (createFavorite && fav != null){
			selection = fModel.getFavoritesPath(fav);
		} else{
			selection = new Object[] { fModel.getRootFolder(), fModel.getMyFolder() };
		}
		final TreePath path = new TreePath(selection);
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}

	public void setMode(final FavoriteMode mode){
		this.mode = mode;
	}

	public FavoriteMode getMode(){
		return mode;
	}

	public boolean isCancel(){
		return cancel;
	}

	protected void initComponents(Ni3Document doc){
		if (createFavorite){
			setTitle(UserSettings.getWord("Create favorite"));
		} else{
			setTitle(UserSettings.getWord("Organize favorites"));
		}

		getContentPane().setLayout(new BorderLayout());
		setBounds(100, 100, 285, 400);

		if (createFavorite){
			final JPanel p1 = new JPanel();
			final JPanel p11 = new JPanel();
			final JPanel p21 = new JPanel();

			p1.setLayout(new BorderLayout());
			p11.setLayout(new BorderLayout());
			p21.setLayout(new BorderLayout());

			JLabel lbl;
			lbl = new JLabel(UserSettings.getWord("Name") + "  ");
			lbl.setMaximumSize(new Dimension(30, 20));
			fname = new JTextField();

			if (fav != null){
				fname.setText(fav.getName());
			}

			fname.setMinimumSize(new Dimension(50, 20));
			fname.setPreferredSize(new Dimension(200, 20));
			fname.setMaximumSize(new Dimension(200, 20));

			p11.add(new JLabel(" "), BorderLayout.NORTH);
			p11.add(lbl, BorderLayout.WEST);
			p11.add(fname, BorderLayout.CENTER);
			p11.add(new JLabel(" "), BorderLayout.SOUTH);
			p1.add(p11, BorderLayout.NORTH);

			fdescription = new JTextArea(2, 50);

			if (fav != null){
				fdescription.setText(fav.getDescription());
			}

			fdescription.setMinimumSize(new Dimension(50, 20));
			fdescription.setPreferredSize(new Dimension(200, 50));
			fdescription.setMaximumSize(new Dimension(200, 50));
			lbl = new JLabel(UserSettings.getWord("Description") + "  ");
			lbl.setMaximumSize(new Dimension(30, 20));

			p21.add(lbl, BorderLayout.NORTH);
			p21.add(fdescription, BorderLayout.CENTER);
			p21.add(new JLabel(" "), BorderLayout.SOUTH);
			p1.add(p21, BorderLayout.SOUTH);

			add(p1, BorderLayout.NORTH);
		}

		tree = new FavoritesTree(doc);
		tree.setName("FavoritesTree");

		final JScrollPane pC = new JScrollPane();
		pC.getViewport().setView(tree);

		add(pC, BorderLayout.CENTER);

		final JPanel south = new JPanel(new BorderLayout());

		if (createFavorite){
			final JPanel p3 = new JPanel();

			final JRadioButton rbTypeFavorite = new JRadioButton(UserSettings.getWord("Favorite"));
			final JRadioButton rbTypeQuery = new JRadioButton(UserSettings.getWord("Query"));
			final JRadioButton rbTypeTopic = new JRadioButton(UserSettings.getWord("Topic"));

			final ButtonGroup grp = new ButtonGroup();
			grp.add(rbTypeFavorite);
			grp.add(rbTypeQuery);

			rbTypeFavorite.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					mode = FavoriteMode.FAVORITE;
				}
			});

			rbTypeQuery.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					mode = FavoriteMode.QUERY;
				}
			});

			p3.add(rbTypeFavorite);
			p3.add(rbTypeQuery);

			if (UserSettings.getBooleanAppletProperty("TopicGUI_InUse", true)){
				grp.add(rbTypeTopic);

				rbTypeTopic.addActionListener(new java.awt.event.ActionListener(){
					@Override
					public void actionPerformed(final java.awt.event.ActionEvent evt){
						mode = FavoriteMode.TOPIC;
					}
				});

				p3.add(rbTypeTopic);
			}

			switch (mode){
				case QUERY:
					rbTypeQuery.setSelected(true);
					break;

				case TOPIC:
					rbTypeTopic.setSelected(true);
					break;

				case FAVORITE:
				default:
					rbTypeFavorite.setSelected(true);
					break;
			}

			south.add(p3, BorderLayout.CENTER);
		}

		final JPanel p2 = new JPanel();

		if (createFavorite){
			final JButton okButton = new JButton(UserSettings.getWord("OK"));
			final JButton cancelButton = new JButton(UserSettings.getWord("Cancel"));

			okButton.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					onOk();
				}
			});

			cancelButton.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					onCancel();
				}
			});

			p2.add(okButton);
			p2.add(cancelButton);
		} else{
			final JButton cancelButton = new JButton(UserSettings.getWord("Close"));

			cancelButton.addActionListener(new java.awt.event.ActionListener(){
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt){
					onCancel();
				}
			});

			p2.add(cancelButton);
		}

		south.add(p2, BorderLayout.SOUTH);

		add(south, BorderLayout.SOUTH);
	}

	public String getFavoriteName(){
		return fname.getText().trim();
	}

	public String getFavoriteDescription(){
		return fdescription.getText().trim();
	}

	public Favorite getSelectedFavorite(){
		Favorite favorite = null;
		final Object selected = tree.getLastSelectedPathComponent();
		if (selected instanceof Favorite){
			favorite = (Favorite) selected;
		}
		return favorite;
	}

	public Folder getSelectedFolder(){
		Folder folder = null;
		final Object selected = tree.getLastSelectedPathComponent();
		if (selected instanceof Folder){
			folder = (Folder) selected;
		} else if (selected instanceof Favorite){
			folder = ((Favorite) selected).getFolder();
		}
		return folder;
	}

	private void onOk(){
		if (!createFavorite)
			return;
		String name = getFavoriteName();
		if (name == null || name.isEmpty()){
			JOptionPane.showMessageDialog(this, UserSettings.getWord("MsgEmptyFavoriteName"));
		} else if (getSelectedFolder() != null){
			cancel = false;
			setVisible(false);
		}
	}

	private void onCancel(){
		cancel = true;
		setVisible(false);
	}

	@Override
	public void valueChanged(final TreeSelectionEvent e){
		final TreePath pth = e.getPath();
		if (pth != null){
			Object parent = pth.getLastPathComponent();
			if (parent instanceof Favorite && fname != null){
				fname.setText(parent.toString());
				fdescription.setText(((Favorite) parent).getDescription());
			}
		}
	}
}
