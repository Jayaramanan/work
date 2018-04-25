/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import javax.swing.*;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphGeometry;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public class SpringGraphLayoutManager extends DefaultGraphLayoutManager{
	public static final String NAME = "Spring";

	int RelaxAlg;

	public SpringGraphLayoutManager(){
		super();
		RelaxAlg = UserSettings.getIntegerProperty("graph", "RelaxType", 1);
	}

	@Override
	public synchronized boolean doLayout(GraphCollection graph){
		synchronized (graph.getEdges()){
			for (Edge e : graph.getEdges())
				e.edgeStyle = Edge.ES_Line;
		}

		boolean allZeros = true;
		synchronized (graph.getNodes()){
			for (Node n : graph.getNodes()){
				n.ZigZag = false;
				if (n.getX() != 0 || n.getY() != 0)
					allZeros = false;
			}
		}

		if (allZeros)
			randomPlace(graph);

		for (int n = 0; n < 3; n++){
			if (RelaxAlg == 1)
				relax1(graph);
			else if (RelaxAlg == 2)
				relax2(graph);
		}

		return true;
	}

	@Override
	public void initialize(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needLayout(GraphCollection graph){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void fromXML(NanoXML nextX){
		// TODO Auto-generated method stub

	}

	@Override
	public void showPropertyDialog(JFrame parent){
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML(){
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized void relax1(GraphCollection graph){
		double vx, vy, len, f, fx, fy, dlen;

		synchronized (graph.getNodes()){
			for (Node n : graph.getNodes()){
				n.fx = 0;
				n.fy = 0;
			}
		}

		synchronized (graph.getEdges()){
			for (Edge e : graph.getEdges()){
				if (!e.isActive() || e.getMetaindex() == -1)
					continue;

				vx = e.to.getX() - e.from.getX();
				vy = e.to.getY() - e.from.getY();
				len = Math.sqrt(vx * vx + vy * vy) * graph.NodeSpace;

				len = (len == 0) ? .0001 : len;
				f = (e.len * 5 - len) / (len * 4);

				if (e.to.fixed || e.to.degree < e.from.degree){
					e.from.fx += -2 * f * vx;
					e.from.fy += -2 * f * vy;
				} else if (e.from.fixed || e.to.degree > e.from.degree){
					e.to.fx += 2 * f * vx;
					e.to.fy += 2 * f * vy;
				} else if (e.from.degree == e.to.degree){
					e.to.fx += f * vx;
					e.to.fy += f * vy;
					e.from.fx += -f * vx;
					e.from.fy += -f * vy;
				}
			}
		}

		synchronized (graph.getNodes()){
			for (Node n1 : graph.getNodes()){
				fx = 0;
				fy = 0;

				if (!n1.isActive() || n1.fixed)
					continue;

				for (Node n2 : graph.getNodes()){
					if (!n2.isActive() || n1 == n2 || n1.degree < n2.degree)
						continue;

					vx = n1.getX() - n2.getX();
					vy = n1.getY() - n2.getY();
					len = vx * vx + vy * vy;
					if (len < 1){
						fx += Math.random();
						fy += Math.random();
					} else if (len < (n1.getScaledMetaphorRadius(false) + n2.getScaledMetaphorRadius(false))
					        * (n1.getScaledMetaphorRadius(false) + n2.getScaledMetaphorRadius(false)) / 2){
						fx += vx / Math.sqrt(len);
						fy += vy / Math.sqrt(len);
					}
				}

				dlen = fx * fx + fy * fy;

				if (dlen > 0){
					dlen = Math.sqrt(dlen) / 2;
					n1.fx += fx / dlen;
					n1.fy += fy / dlen;
				}
			}

			boolean MoveFixed = true;
			if (graph.getRoots().size() < 3)
				MoveFixed = false;

			for (Node n : graph.getNodes()){
				if (n.isActive() && (!n.fixed || MoveFixed)){
					if (Math.abs(n.getX()) < 4000)
						n.setX(n.getX() + Math.max(-5, Math.min(5, n.fx)));
					if (Math.abs(n.getY()) < 4000)
						n.setY(n.getY() + Math.max(-5, Math.min(5, n.fy)));
				}
			}
		}
	}

	/**
	 * relax links between nodes, make them looks more clear
	 */

	public synchronized boolean relax2(GraphCollection graph){
		synchronized (graph.getNodes()){
			// clean all
			for (Node n : graph.getNodes()){
				n.fx = 0;
				n.fy = 0;
			}

			synchronized (graph.getEdges()){
				linkShrink(graph);
				nodeRepulse(graph);
			}

			boolean MoveFixed = true;
			if (graph.getRoots().size() < 3)
				MoveFixed = false;

			// move all
			boolean ret = false;
			for (Node n : graph.getNodes()){
				if (!n.isActive())
					continue;

				if (!MoveFixed && n.fixed)
					continue;

				double c = 0.1;

				if ((n.fx != 0.0 || n.fy != 0.0)){
					n.setX(n.getX() + c * Math.min(n.fx, 100));
					n.setY(n.getY() + c * Math.min(n.fy, 100));

					ret = true;
				}
			}
			return ret;
		}
	}

	private void linkShrink(GraphCollection graph){
		for (Edge e : graph.getEdges()){
			if (!e.isActive() || e.getMetaindex() == -1)
				continue;

			// current distance
			double dx = e.from.getX() - e.to.getX();
			double dy = e.from.getY() - e.to.getY();
			double d = Math.sqrt(dx * dx + dy * dy);

			if (d < 1)
				d = 1;

			double s = (e.len * graph.NodeSpace * 5 - d) / (d * 4);

			if (s > 50)
				s = 50;
			dx *= s;
			dy *= s;
			if (dx > 50)
				dx = 50;
			if (dy > 50)
				dy = 50;

			if (!e.from.fixed || e.to.fixed){
				e.from.fx += dx;
				e.from.fy += dy;

				if (e.from.fixed)
					e.from.fixed = true;
			}

			if (!e.to.fixed || e.from.fixed){
				e.to.fx -= dx;
				e.to.fy -= dy;

				if (e.to.fixed)
					e.to.fixed = true;
			}
		}
	}

	private void nodeRepulse(GraphCollection graph){
		int cnt = graph.getNodes().size();

		int i = 0;
		for (Node n1 : graph.getNodes()){
			if (!n1.isActive())
				continue;

			for (Node n2 : graph.getNodes()){

				if (!n2.isActive())
					continue;

				// real distance
				double xx = n1.getX() - n2.getX();
				double yy = n1.getY() - n2.getY();
				double d = xx * xx + yy * yy;

				if (d < 1)
					d = 1;

				// desired distance
				double m = (n1.getScaledMetaphorRadius(false) + n2.getScaledMetaphorRadius(false)) * 1.2 * graph.NodeSpace;
				m = m * m;
				if (d < m){
					double s;
					s = (m - d) / d;

					if (Math.abs(s) > 150)
						s = 50 * Math.signum(s);

					xx = 0.2 * xx * s;
					yy = 0.2 * yy * s;
					if (xx == 0.0)
						xx = s * 0.2;
					if (yy == 0.0)
						yy = s * 0.2;

					if (!n1.fixed || n2.fixed){
						n1.fx += xx;
						n1.fy += yy;
					}

					if (!n2.fixed || n1.fixed){
						n2.fx -= xx;
						n2.fy -= yy;
					}
				}
			}

			i++;
		}
	}

	@SuppressWarnings("unused")
	private void linkNodeRepulse(GraphCollection graph){
		for (Node n : graph.getNodes()){
			if (!n.isActive())
				continue;

			for (Edge e : graph.getEdges()){
				if (!e.isActive())
					continue;

				// node does not belong to the link
				if (e.from == n || e.to == n)
					continue;

				double[] link = { 0, 0, 0 };
				double[] node = { n.getX(), n.getY() };
				double[] perp = { 0, 0, 0 };
				double[] inter = { 0, 0 };

				GraphGeometry.lineCoef(e.from.getX(), e.from.getY(), e.to.getX(), e.to.getY(), link);
				GraphGeometry.perpend(link, node, perp);
				GraphGeometry.intersect(link, perp, inter);

				// if intersection outside the link, do not worry
				if (!GraphGeometry.inSegment(e.from.getX(), e.from.getY(), e.to.getX(), e.to.getY(), inter))
					continue;

				double d = GraphGeometry.distance(node, inter);
				double rate;
				if (Math.abs(d) < 1)
					rate = 0.2;
				else
					rate = 0.2 / Math.sqrt(d);

				// add repulsion along the distance vector
				if (d > 0.01){
					n.fx -= rate * (inter[0] - node[0]) / d;
					n.fy -= rate * (inter[1] - node[1]) / d;
				} else{
					n.fx -= 1;
					n.fy -= 1;
				}
			}
		}
	}

	@Override
	public void action(String option, GraphObject object){
		// TODO Auto-generated method stub

	}

	@Override
	public void editSettings(){
	}

	@Override
	public void graphChanged(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOptionEnabled(Node n, int index){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName(){
		return NAME;
	}
}
