package softmodelling;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class BoxClass extends Vec3D {

	SoftModelling p5;

	Vec3D pos;
	float x, y, z;
	boolean isSelected = false;
	int type = 0;
	// 0 = Vertex
	// 1 = Edge
	// 2 = Face
	int key;
	int scrX, scrY;

	// ////////////////CONSTRUCTOR
	BoxClass(SoftModelling _p5, float _x, float _y, float _z, int _type, int _key) {

		super(_x, _y, _z); // everything coming from the SuperClass
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
		// pos = new Vec3D(this.getPosVec().x, getPosVec().y, getPosVec().z);
		render();
		renderkey();

	}

	void render() {
		if (p5.selectionMode == this.type) {
			if (p5.displaySelectors) {
				p5.strokeWeight(15);
				if (!isSelected) {
					p5.stroke(100,255);

				} else {
					p5.stroke(255, 0, 0,255);
				}
				p5.point(x, y, z);
				
				p5.strokeWeight(10);
				if (!isSelected) {
					p5.stroke(255,255);

				} else {
					p5.stroke(255, 0, 0,255);
				}
				p5.point(x, y, z);
				
			}
		}
	}

	void renderkey() {
		if (p5.displayVertexKey) {
			if (p5.selectionMode == type) {
				p5.fill(255, 255, 0);
				p5.pushMatrix();
				p5.translate(pos.x - 5, pos.y, pos.z + 25);
				p5.rotateX(p5.radians(-90));
				p5.text("" + (key - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
	}

}
