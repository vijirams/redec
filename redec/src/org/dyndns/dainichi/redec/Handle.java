package org.dyndns.dainichi.redec;

import processing.core.PApplet;

public class Handle {
	PApplet parent;
	int x, y;
	int boxx, boxy;
	int length;
	int size;
	boolean over;
	boolean press;
	boolean locked = false;
	boolean otherslocked = false;
	Handle[] others;

	public Handle(int ix, int iy, int il, int is, Handle[] o, PApplet theParent) {
		x = ix;
		y = iy;
		length = il;
		size = is;
		boxx = x + length - size / 2;
		boxy = y - size / 2;
		others = o;
		parent = theParent;
	}
	public static void processSliders(Handle[] handles,int[] vals) {

		for (int i = 0; i < handles.length; i++) {
			handles[i].update();
			handles[i].display();
			vals[i] = handles[i].length;
		}

	}

	public void update() {
		boxx = x + length;
		boxy = y - size / 2;

		for (int i = 0; i < others.length; i++) {
			if (others[i].locked == true) {
				otherslocked = true;
				break;
			} else {
				otherslocked = false;
			}
		}

		if (otherslocked == false) {
			over();
			press();
		}

		if (press) {
			length = lock(parent.mouseX - x - size / 2, 0, 255);
		}
	}

	void over() {
		if (overRect(boxx, boxy, size, size)) {
			over = true;
		} else {
			over = false;
		}
	}

	void press() {
		if (over && parent.mousePressed || locked) {
			press = true;
			locked = true;
		} else {
			press = false;
		}
	}

	void release() {
		locked = false;
	}

	void display() {
		parent.line(x, y, x + length, y);
		parent.fill(255);
		parent.stroke(0);
		parent.rect(boxx, boxy, size, size);
		if (over || press) {
			parent.line(boxx, boxy, boxx + size, boxy + size);
			parent.line(boxx, boxy + size, boxx + size, boxy);
		}

	}

	boolean overRect(int thex, int they, int width, int height) {
		if (parent.mouseX >= thex && parent.mouseX <= thex + width
				&& parent.mouseY >= they && parent.mouseY <= they + height) {
			return true;
		} else {
			return false;
		}
	}

	int lock(int val, int minv, int maxv) {
		return parent.min(parent.max(val, minv), maxv);
	}
}