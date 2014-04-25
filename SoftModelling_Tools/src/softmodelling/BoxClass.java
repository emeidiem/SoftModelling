package softmodelling;

import processing.core.PApplet;
import shapes3d.Box;
import toxi.geom.Vec3D;

public class BoxClass extends Box {

	SoftModelling p5;

	Vec3D pos;
	float x, y, z;
	boolean isSelected = false;
	int type = 0;
	// 0 = Vertex
	// 1 = Edge
	// 2 = Face
	int key;

	// ////////////////CONSTRUCTOR
	BoxClass(SoftModelling _p5, float _x, float _y, float _z, int _type, int _key) {

		super(_p5, _x, _y, _z); // everything coming from the SuperClass
		// (VerletParticle)
		p5 = _p5;
		x = _x;
		y = _y;
		z = _z;
		type = _type;
		key = _key;
	}
	// /////////////////////////
	void run() {
		pos = new Vec3D(this.getPosVec().x, getPosVec().y, getPosVec().z);
		render();
		renderkey();
	}

	void render() {
		if (p5.selectionMode == this.type) {
			if (p5.displaySelectors) this.draw();
			if (!isSelected) {
				if (type == 0) this.fill(p5.color(255));
				if (type == 1) this.fill(p5.color(255));
				if (type == 2) this.fill(p5.color(255));
			} else {
				this.fill(p5.color(255, 0, 0));
			}
		}
	}

	void renderkey() {
		if (p5.displayVertexKey) {
			if (p5.selectionMode == type) {
				p5.fill(255,255,0);
				p5.pushMatrix();
				p5.translate(pos.x - 5, pos.y, pos.z + 25);
				p5.rotateX(p5.radians(-90));
				p5.text("" + (key - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
	}

}
