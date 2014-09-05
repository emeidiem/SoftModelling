package softmodelling;

import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;

public class Particle extends VerletParticle {

	SoftModelling p5;
	Vec3D pos;
	int key;
	boolean isCorner = false;
	boolean lockSelected = false;
	boolean keepLocked = false;
	boolean isSelected = false;
	boolean hasBeenDragged = false;


	Particle(SoftModelling _p5, Vec3D _pos, int _key) {
		super(_pos); // everything coming from the SuperClass (VerletParticle)
		p5 = _p5;
		pos = _pos;
		key = _key;

	}

	void run() {
		if (p5.displayPhysics){
			render();
			renderSelector();
		}

		if (this.hasBeenDragged) {
			// this.lock();
		}
		if (this.isSelected) {
		}
	}

	void renderSelector() {
		if ((p5.selectionMode == 0) && (p5.displaySelectors)) {
			p5.strokeWeight(15);
			if (!isSelected) {
				p5.stroke(100, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(x, y, z);

			p5.strokeWeight(10);
			if (!isSelected) {
				p5.stroke(255, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(x, y, z);
		}
	}

	void render() {
		if (this.keepLocked) {
			p5.strokeWeight(18);
			p5.stroke(0, 255, 255, 200);
		} else {
			p5.stroke(155);
			p5.strokeWeight(10);
		}
		if (this.isSelected) {
			p5.strokeWeight(7);
			p5.stroke(255, 0, 0);
		}
		else{
			p5.stroke(155);
			p5.strokeWeight(10);
		}
		p5.point(this.x, this.y, this.z);

		if (p5.displayVertexKey) {
			if (p5.selectionMode == 0) {
				p5.fill(255);
				p5.pushMatrix();
				p5.translate(x - 5, y, z + 15);
				p5.rotateX(p5.radians(-90));
//				p5.text("" + (key - 0) + " /" + (this.isSelected), 0, 0, 0);
				p5.text("" + (key - 0) + " /" + (p5.surface.particlesSelected.contains(this)), 0, 0, 0);

				p5.popMatrix();
			}
		}
	}

}// endClass
