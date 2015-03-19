package softmodelling;

import java.util.List;

import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.physics.VerletParticle;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Vertex;

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
		this.pos = this;
		if (p5.displayPhysics) {
			if (!p5.showAlphaBlending) {
				render();
			} else {
				renderAlphaBlending();
			}
			renderSelector();
		}

		if (this.hasBeenDragged) {
			// this.lock();
		}
		if (this.isSelected) {
		}
	}

	void renderAlphaBlending() {
		// color c = color( agePer, agePer*.75, 1.0 - agePer );
		int c = p5.color(1.0f, 1.0f, 1.0f);
		float radius = 20;
		if (p5.selectionMode != 0) {
			radius = 9;
		}
		p5.renderImageAB(p5.particleImg, pos, radius, c, 1.0f);

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

			p5.strokeWeight(8);
			if (!isSelected) {
				p5.stroke(255, 255);

			} else {
				p5.stroke(255, 0, 255, 255);
			}
			p5.point(x, y, z);

		}

		if ((isSelected) && (p5.showAlphaBlending)) {
			p5.stroke(1, 1, 1, .5f);
			p5.strokeWeight(2);

			p5.pushMatrix();
			p5.translate(x, y, z);
			p5.point(0, 0);
			if (p5.selectionMode == 0)
				p5.ellipse(0, 0, 10, 10);
			else
				p5.ellipse(0, 0, 5, 5);
			p5.popMatrix();
		}

	}

	void checkNeighborstoAddPhysics() {
		if (!p5.physics.particles.contains(this))
			p5.physics.addParticle(this);

	}

	void checkNeighborstoRemovePhysics() {
		HE_Vertex vv = (HE_Vertex) p5.mesh.getVertexByKey(this.key);
		List<HE_Vertex> neighbors = vv.getNeighborVertices();
		List<HE_Edge> neighborEdges = vv.getEdgeStar();

		boolean neighborslocked = false;
		for (int i = 0; i < neighbors.size(); i++) {
			HE_Vertex n = neighbors.get(i);
			Particle p = p5.surface.getParticleswithKey(p5.surface.particles,
					n.key());
			if (p.isLocked)
				neighborslocked = true;
		}
		if ((neighborslocked) && (p5.physics.particles.contains(this)))
			p5.physics.removeParticle(this);

		for (int i = 0; i < neighborEdges.size(); i++) {
			HE_Edge e = neighborEdges.get(i);
			Spring s = p5.surface
					.getSpringswithKey(p5.surface.springs, e.key());
			if ((s.a.isLocked) && (s.b.isLocked)) {
				p5.physics.removeSpring(s);
			}
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
		} else {
			p5.stroke(155);
			p5.strokeWeight(10);
		}
		p5.point(this.x, this.y, this.z);

		if (p5.showIndex) {
			if (p5.selectionMode == 0) {
				p5.fill(255);
				p5.pushMatrix();
				p5.translate(x - 5, y, z + 15);
				p5.rotateX(p5.radians(-90));
				// p5.text("" + (key - 0) + " /" + (this.isSelected), 0, 0, 0);
				p5.text("" + (key - 0) + " /"
						+ (p5.surface.particlesSelected.contains(this)), 0, 0,
						0);

				p5.popMatrix();
			}
		}
	}

}// endClass
