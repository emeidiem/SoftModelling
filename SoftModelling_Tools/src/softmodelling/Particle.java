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
	float prevMoveXP, prevMoveYP, prevMoveZP;
	float initOffsetX, initOffsetY, initOffsetZ;
	Vec3D posBeforeTransformation;

	Particle(SoftModelling _p5, Vec3D _pos, int _key) {
		super(_pos); // everything coming from the SuperClass (VerletParticle)
		p5 = _p5;
		pos = _pos;
		key = _key;
		posBeforeTransformation = pos.copy();
		initOffsetX = 0;
		initOffsetY = 0;
		initOffsetZ = 0;
	}
	void run() {
		render();
		if (this.hasBeenDragged) {
			updateprevMove();
			// this.lock();
		}
		if (this.isSelected) {
//			moveXYParticle();
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
		p5.point(this.x, this.y, this.z);
		if (this.isSelected) {
			p5.strokeWeight(7);
			p5.stroke(255, 0, 0);
			p5.point(this.x, this.y, this.z);
		}
		if (p5.displayVertexKey) {
			if (p5.selectionMode == 0) {
				p5.fill(255);
				p5.pushMatrix();
				p5.translate(x - 5, y, z + 15);
				p5.rotateX(p5.radians(-90));
				p5.text("" + (key - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
	}

	void updateprevMove() {
		prevMoveXP = p5.gui.prevMoveX;
		prevMoveYP = p5.gui.prevMoveY;
		prevMoveZP = p5.gui.prevMoveZ;
	}
	void moveXYParticle() {
		if (!this.hasBeenDragged) {
			if ((p5.gui.slider2d.arrayValue()[0] > p5.gui.size2dSlider / 2 + 1) || (p5.gui.slider2d.arrayValue()[0] < p5.gui.size2dSlider / 2 - 1)
					|| (p5.gui.slider2d.arrayValue()[1] > p5.gui.size2dSlider / 2 + 1) || (p5.gui.slider2d.arrayValue()[1] < p5.gui.size2dSlider / 2 - 1)
					|| (p5.gui.sliderZ.arrayValue()[1] > p5.gui.sizeZSlider / 2 + 1) || (p5.gui.sliderZ.arrayValue()[1] < p5.gui.sizeZSlider / 2 - 1)) {
				initOffsetX += p5.gui.prevMoveX;
				initOffsetY += p5.gui.prevMoveY;
				initOffsetZ += p5.gui.prevMoveZ;

				this.hasBeenDragged = true;
			}
		} else {
			this.x = (p5.gui.slider2d.arrayValue()[0] - (p5.gui.size2dSlider / 2)) * 10 + this.posBeforeTransformation.x + prevMoveXP - initOffsetX;
			this.y = (p5.gui.slider2d.arrayValue()[1] - (p5.gui.size2dSlider / 2)) * 10 + this.posBeforeTransformation.y + prevMoveYP - initOffsetY;
			this.z = (p5.gui.sliderZ.arrayValue()[1] - (p5.gui.sizeZSlider / 2)) * 10 + this.posBeforeTransformation.z + prevMoveZP - initOffsetZ;
		}
	}
}// endClass