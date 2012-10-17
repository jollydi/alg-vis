/*******************************************************************************
 * Copyright (c) 2012 Jakub Kováč, Katarína Kotrlová, Pavol Lukča, Viktor Tomkovič, Tatiana Tóthová
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package algvis.binomialheap;

import java.awt.Color;

import algvis.core.NodeColor;
import algvis.core.DataStructure;
import algvis.core.MeldablePQ;
import algvis.core.Node;
import algvis.gui.Fonts;
import algvis.gui.view.View;

public class BinHeapNode extends Node {
	private int leftw;
    public int height;
    public int rank; // TODO: size -> rank (treba ale zmenit aj pomocne upratovacie pole....)
	public BinHeapNode parent, left, right, child;
	public boolean cut;

	private BinHeapNode(DataStructure D, int key, int x, int y) {
		super(D, key, x, y);
		parent = child = null;
		left = right = this;
		rank = 0;
		steps = 0;
		//bgKeyColor();
		bgColor(Color.white);
	}

	public BinHeapNode(DataStructure D, int key) {
		this(D, key, 0, 0);
		getReady();
	}

	public BinHeapNode(BinHeapNode v) {
		this(v.D, v.getKey(), v.x, v.y);
	}

	public boolean isRoot() {
		return parent == null;
	}

	boolean isLeaf() {
		return child == null;
	}

	// vyhodi sa zo zoznamu
	public void unlink() {
		if (parent != null) {
			if (parent.child == this) {
				if (right == this)
					parent.child = null;
				else
					parent.child = right;
			}
			parent.rank--;
			parent = null;
		}
		left.right = right;
		right.left = left;
		left = right = this;
	}

	// napoji v ako prveho
	public void linkChild(BinHeapNode v) {
		if (isLeaf()) {
			v.left = v.right = v;
		} else {
			v.left = child.left;
			v.right = child;
			v.left.right = v;
			v.right.left = v;
		}
		v.parent = this;
		child = v;
		++height;
		++rank;
	}

	public void linkRight(BinHeapNode v) {
		v.parent = null;
		right.left = v;
		v.left = this;
		v.right = right;
		right = v;
	}

	public void linkLeft(BinHeapNode v) {
		v.parent = null;
		left.right = v;
		v.right = this;
		v.left = left;
		left = v;
	}

	public void linkAll(BinHeapNode v) {
		// ku this-x-x-x-u pripoj v-x-x-x-x-w
		// linkLeft prilinkuje len samotne v
		BinHeapNode u = this.left, w = v.left;
		u.right = v;
		v.left = u;
		this.left = w;
		w.right = this;
	}

	void rebox() {
		if (isLeaf()) {
			leftw = DataStructure.minsepx / 2;
			height = 1;
		} else {
			leftw = child.leftw;
			height = child.height + 1;
			BinHeapNode w = child, v = child.right;
			while (v != w) {
				leftw += Node.radius + v.leftw;
				v = v.right;
			}
		}
	}

	void reboxTree(BinHeapNode first) {
		if (!isLeaf()) {
			child.reboxTree(child);
		}
		if (right != first) {
			right.reboxTree(first);
		}
		rebox();
	}

	private void repos(int x, int y, BinHeapNode first) {
		goTo(x + leftw, y);
		if (!isLeaf()) {
			child.repos(x, y + DataStructure.minsepy, child);
		}
		if (right != first) {
			right.repos(x + leftw + Node.radius, y, first);
		}
	}

	public void _reposition(int x, int y) {
		reboxTree(this);
		repos(x, y, this);
	}

	public void drawTree(View v, BinHeapNode first, BinHeapNode parent) {
		if (!isLeaf()) {
			child.drawTree(v, child, this);
		}
		if (right != first) {
			right.drawTree(v, first, parent);
		}
		if (parent != null) { // edge to parent
			v.setColor(Color.black);
			v.drawLine(x, y, parent.x, parent.y);
			if (v.output)
				System.out.println("  Edge(" + id + "," + parent.id + ")");
		} else if (this != first) { // edge to left
			v.setColor(Color.black);
			v.drawLine(x, y, left.x, left.y);
			if (v.output)
				System.out.println("  Edge(" + id + "," + left.id + ")");
		}
		draw(v);
	}

	public void moveTree() {
		moveTree(this);
	}

	void moveTree(BinHeapNode first) {
		move();
		if (!isLeaf()) {
			child.moveTree(child);
		}
		if (right != first) {
			right.moveTree(first);
		}
	}

	void lowlight() {
		bgColor(new Color(200, 200 - getKey() / 10, 0));
	}

	void highlight() {
		bgKeyColor();
	}

	public void lowlightTree() {
		lowlightTree(this);
	}

	void lowlightTree(BinHeapNode first) {
		lowlight();
		if (!isLeaf()) {
			child.lowlightTree(child);
		}
		if (right != first) {
			right.lowlightTree(first);
		}
	}

	public void highlightTree() {
		highlightTree(this);
	}

	public void highlightTree(BinHeapNode first) {
		highlight();
		if (!isLeaf()) {
			child.highlightTree(child);
		}
		if (right != first) {
			right.highlightTree(first);
		}
	}

	public boolean prec(Node v) {
		if (((MeldablePQ) D).minHeap) {
			return this.getKey() < v.getKey();
		} else {
			return this.getKey() > v.getKey();
		}
	}

	public boolean preceq(Node v) {
		if (((MeldablePQ) D).minHeap) {
			return this.getKey() <= v.getKey();
		} else {
			return this.getKey() >= v.getKey();
		}
	}

	@Override
	public void draw(View v) {
		if (state == Node.INVISIBLE || getKey() == NULL) {
			return;
		}
		drawBg(v);
		drawKey(v);
		if (parent == null) {
			v.setColor(Color.black);
			v.drawString("" + rank, x + Node.radius, y - Node.radius, Fonts.SMALL);
		}
		if (v.output) {
			System.out.println("  Node(" + id + "," + key + "," + cpos() + ","
					+ (marked ? 1 : 0) + ")");
		}
	}

	public BinHeapNode find(BinHeapNode first, int x, int y) {
		if (inside(x, y))
			return this;
		if (!isLeaf()) {
			BinHeapNode tmp = child.find(child, x, y);
			if (tmp != null)
				return tmp;
		}
		if (right != first) {
			return right.find(first, x, y);
		}
		return null;
	}

	public void markCut() {
		cut = true;
		setColor(NodeColor.BLACK);
	}

	public void unmarkCut() {
		cut = false;
		bgKeyColor();
		fgColor(Color.black);
	}

}
