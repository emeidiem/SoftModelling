package softmodelling;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import toxi.physics.VerletSpring;
import wblut.hemesh.HE_Face;

public class Spring extends VerletSpring {
	SoftModelling p5;
	int key;
	Particle a, b;
	float initlen;
	boolean delete = false;
	boolean isSelected = false;
	Vec3D centroid;
	

	// ////////////////CONSTRUCTOR
	Spring(SoftModelling _p5, Particle _a, Particle _b, float _len, float _str,
			int _key) {
		super(_a, _b, _len, _str);
		p5 = _p5;
		key = _key;
		initlen = _len;
		a = _a;
		b = _b;
		calculateCentroid();
	}

	// /////////////////////////

	void run() {
		if (!p5.justBeziersOn)render();
		calculateCentroid();
	}

	void calculateCentroid() {
		Vec3D midpt = (b.sub(a)).scaleSelf(.5f);
		centroid = midpt.add(a);
	}
	
	float distanceSpringCam() {

		Vec3D camPos = new Vec3D(p5.cam.getPosition()[0],
				p5.cam.getPosition()[1], p5.cam.getPosition()[2]);
		float d = this.centroid.distanceTo(camPos);

		return d;
	}


	void render() {
		p5.strokeWeight(1);
		if (!p5.showAlphaBlending) {
			if (p5.displayMesh)
				p5.stroke(0);
			else
				p5.stroke(255);
		} else {
			
//			float mapFactorStrokeWeight = PApplet.map(distanceSpringCam(), p5.mesh.minDistanceCam, p5.mesh.maxDistanceCam, 3, .2f);
			float mapFactorStroke = p5.map(distanceSpringCam(), p5.mesh.minDistanceCam, p5.mesh.maxDistanceCam, 1, .2f);
			
			p5.stroke(1, 0, 1, .05f*mapFactorStroke);
			p5.strokeWeight(5);
			p5.line(a.x, a.y, a.z, b.x, b.y, b.z);
			p5.stroke(1, 1, 1, .09f*mapFactorStroke); //0.9f
//			p5.strokeWeight(5);
//			p5.line(a.x, a.y, a.z, b.x, b.y, b.z);
			p5.strokeWeight(3.5f); //3.5f
			p5.line(a.x, a.y, a.z, b.x, b.y, b.z);
			p5.strokeWeight(1.5f); //1.5f
			p5.line(a.x, a.y, a.z, b.x, b.y, b.z);
			p5.strokeWeight(2.1f); //2.1f
			p5.line(a.x, a.y, a.z, b.x, b.y, b.z);
			p5.stroke(1, 1, 1, .2f); //.2f
			p5.strokeWeight(1f*mapFactorStroke);
		}
		p5.line(a.x, a.y, a.z, b.x, b.y, b.z);



		if (p5.showIndex) {
			if (p5.selectionMode == 1) {
				p5.fill(255);
				p5.pushMatrix();
				// Vec3D midpt = (b.sub(a)).scaleSelf(.5f);
				// Vec3D midpt2 = midpt.add(a);
				// p5.translate((float) midpt2.x, midpt2.y, midpt2.z + 15);
				p5.translate((float) centroid.x, centroid.y, centroid.z + 15);
				p5.rotateX(p5.radians(-90));
				p5.text("" + (key - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
	}
}// endClass