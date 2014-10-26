package softmodelling;

import java.util.ArrayList;
import java.util.List;

import toxi.geom.Vec3D;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Vertex;

public class Surface {

	SoftModelling p5;
	float strength = 0.1f;
	ArrayList<Particle> particles = new ArrayList<Particle>();
	ArrayList<Particle> particlesSelected = new ArrayList<Particle>();
	ArrayList<Spring> springs = new ArrayList<Spring>();
	ArrayList<Spring> springsSelected = new ArrayList<Spring>();

	// ////////////////CONSTRUCTOR
	Surface(SoftModelling _p5) {
		p5 = _p5;
		initSurface();
		lockCorners();
	}

	// /////////////////////////

	void run() {
		runParticles();
		runSprings();
	}

	void runParticles() {
		for (int i = 0; i < particles.size(); i++) {
			Particle p = (Particle) particles.get(i);
			p.run();
		}
	}

	void runSprings() {
		for (int i = 0; i < springs.size(); i++) {
			Spring s = (Spring) springs.get(i);
			s.run();
		}
	}

	void initSurface() {
		ArrayList<HE_Vertex> emptyList = new ArrayList<HE_Vertex>();
		createNewParticlesFromMesh(p5.mesh.getVerticesAsList(),emptyList);
		List<HE_Edge> listCheck = (List<HE_Edge>) p5.mesh.getEdgesAsList();
		createSpringsFromMesh(listCheck);
	}

	void createNewParticlesFromMesh(List<HE_Vertex> verticesToCheck,
			List<HE_Vertex> verticesToAvoid) {

		for (int i = 0; i < verticesToCheck.size(); i++) {

			HE_Vertex vv = (HE_Vertex) verticesToCheck.get(i);
			if (!verticesToAvoid.contains(vv)) {
				Vec3D v1 = new Vec3D((float) vv.xf(), (float) vv.yf(),
						(float) vv.zf());
				Particle p = (Particle) new Particle(p5, v1, vv.key());
				if (!particles.contains(p)) {
					particles.add(p);
					p5.physics.addParticle(p);
					p.key = vv.key();
				}
			}
		}
	}

	Particle getParticleswithKey(List<Particle> particleList, int key) {
		int index = 0;
		for (int i = 0; i < particleList.size(); i++) {
			Particle p = (Particle) particleList.get(i);
			if (p.key == key) {
				index = i;
			}
		}
		Particle p2 = (Particle) particleList.get(index);
		return p2;
	}

	Spring getSpringswithKey(List<Spring> springsList, int key) {
		int index = 0;
		for (int i = 0; i < springsList.size(); i++) {
			Spring s = (Spring) springsList.get(i);
			if (s.key == key) {
				index = i;
			}
		}
		Spring s2 = (Spring) springsList.get(index);
		return s2;
	}

	void createSpringsFromMesh(List listToCheck) {

		for (int i = 0; i < listToCheck.size() / 1; i++) {
			HE_Edge e = (HE_Edge) listToCheck.get(i);
			HE_Vertex va = e.getStartVertex();
			HE_Vertex vb = e.getEndVertex();

			Particle a, b;
			a = (Particle) getParticleswithKey(particles, va.key());
			b = (Particle) getParticleswithKey(particles, vb.key());
			Spring s = (Spring) new Spring(p5, a, b,
					(float) (e.getLength() * .8f), strength, e.key());
			p5.physics.addSpring(s);
			springs.add(s);
			p5.println("mesh.getEdgesAsList().size() = "
					+ p5.mesh.getEdgesAsList().size());
			p5.println("surface.springs.size() = " + springs.size());
			// }
		}
	}

	void lockCorners() {
		for (int i = 0; i < p5.mesh.getVerticesAsList().size(); i++) {
			HE_Vertex v = p5.mesh.getVerticesAsList().get(i);
			if (v.getEdgeStar().size() <= 2) {
				Particle p = this.getParticleswithKey(particles, v.key());
				p.lock();
				p.isCorner = true;
				p.lockSelected = true;
				p.keepLocked = true;
			}
		}
	}

	void resizeSprings() {
		for (int i = 0; i < springs.size(); i++) {
			Spring s = (Spring) springs.get(i);
			if ((s.isSelected) || (p5.mesh.selection.getEdgesAsList().size()==0)){
				float initlength = s.initlen;
				// float initlength = s.getRestLength();
				p5.println("initlength = " + initlength);
				float newlength = initlength * ((p5.springlengthScale / 100));
				if ((newlength < 300) && (newlength > 1)) {
					s.setRestLength(newlength);
					s.initlen = newlength;
				}
			}
		}
		// for (int i = 0; i < springs.size(); i++) {
		// BoxClass b = (BoxClass) p5.mesh.boxArrayEdges.get(i);
		// Spring s = (Spring) springs.get(i);
		// if (b.isSelected) {
		// float initlength = s.initlen;
		// float newlength = initlength * ((p5.springlengthScale / 100));
		// //if (newlength < 200)
		// s.setRestLength(newlength);
		// }
		// }
	}

	void deselectParticles() {
		for (int i = 0; i < this.particlesSelected.size(); i++) {
			Particle p = (Particle) particlesSelected.get(i);
			// if (p.isLocked())
			// p.keepLocked = true;
			// if (!p.lockSelected) {
			// p.unlock();
			// p.keepLocked = false;
			// }
			// p.hasBeenDragged = false;
			p.isSelected = false;
			// particlesSelected.remove(p);
		}
		particlesSelected.clear();
	}
	
	void deselectSprings() {
		for (int i = 0; i < this.springsSelected.size(); i++) {
			Spring s = (Spring) springsSelected.get(i);
			s.isSelected = false;
		}
		particlesSelected.clear();
	}

	void lockSelectParticles() {
		for (int i = 0; i < particles.size(); i++) {
			Particle p = (Particle) particles.get(i);
			if (p.isSelected) {
				p.lock();
				p.keepLocked = true;
				p.lockSelected = true;
			}
		}
	}

	void unlockSelectParticles() {
		for (int i = 0; i < particles.size(); i++) {
			Particle p = (Particle) particles.get(i);
			if (p.isSelected) {
				if (p.keepLocked)
					p.unlock();
				p.keepLocked = false;
				p.lockSelected = false;
			}
		}
	}

	void selectAllParticles() {
		p5.mesh.selection.addVertices(p5.mesh.getVerticesAsList());
		for (int i = 0; i < particles.size(); i++) {
			Particle p = (Particle) particles.get(i);
			p.isSelected = true;
			if (!particlesSelected.contains(p))
				particlesSelected.add(p);
		}
	}

	void removeDuplicatesSprings() {
		for (int i = 0; i < springs.size(); i++) {
			Spring s1 = (Spring) springs.get(i);
			for (int j = 0; j < springs.size(); j++) {
				Spring s2 = (Spring) springs.get(j);
				if (s1 != s2) {
					if ((s1.key == s2.key)) {

						if ((s1.a.key == s2.a.key) && (s1.b.key == s2.b.key)) {
							springs.remove(s2);
							p5.physics.removeSpring(s2);
						}
						if ((s1.a.key == s2.b.key) && (s1.b.key == s2.a.key)) {
							springs.remove(s2);
							p5.physics.removeSpring(s2);
						}
					}

				}
			}
		}

	}

	void removeSpringsifNotInPhysics() {

		for (int j = 0; j < springs.size(); j++) {
			Spring s = (Spring) springs.get(j);
			if (!p5.physics.springs.contains(s)) {
				springs.remove(s);
			}
		}

	}

	void removeSpringsWithoutBoxes() {

		// // check if exist////
		for (int i = 0; i < p5.mesh.getEdgesAsList().size(); i++) {
			HE_Edge e = (HE_Edge) p5.mesh.getEdgesAsList().get(i);
			for (int j = 0; j < springs.size(); j++) {
				Spring s = (Spring) springs.get(j);
				if ((s.key == e.key())) {

					if ((s.a.key == e.getStartVertex().key())
							&& (s.b.key != e.getEndVertex().key())) {
						springs.remove(s);
						p5.physics.removeSpring(s);
					}
					if ((s.b.key == e.getStartVertex().key())
							&& (s.a.key != e.getEndVertex().key())) {
						springs.remove(s);
						p5.physics.removeSpring(s);
					}
					if ((s.a.key == e.getEndVertex().key())
							&& (s.b.key != e.getStartVertex().key())) {
						springs.remove(s);
						p5.physics.removeSpring(s);
					}
					if ((s.b.key == e.getEndVertex().key())
							&& (s.a.key != e.getStartVertex().key())) {
						springs.remove(s);
						p5.physics.removeSpring(s);
					}

				}
			}
		}
	}

	void recomputeAllSpringsToPhysics() {
		p5.physics.springs.clear();
		for (int i = 0; i < springs.size(); i++) {
			Spring s = (Spring) springs.get(i);
			p5.physics.addSpring(s);
		}
	}

	void recomputeSpringsKeys() {

		for (int h = 0; h < p5.mesh.getEdgesAsList().size(); h++) {
			HE_Edge e = (HE_Edge) p5.mesh.getEdgesAsList().get(h);
			HE_Vertex va = e.getStartVertex();
			HE_Vertex vb = e.getEndVertex();

			for (int i = 0; i < p5.surface.springs.size(); i++) {
				Spring s = (Spring) p5.surface.springs.get(i);
				if (((s.a.key == va.key()) && (s.b.key == vb.key()))
						|| ((s.a.key == vb.key()) && (s.b.key == va.key()))) {
					s.key = e.key();
				}
			}
		}
	}

	// -----------------------------------------------------------------------tut014
	void killSelectParticles() {

		int indexSelected = 0;
		boolean possitive = false;
		boolean somethingSelected = false;
		Particle p;
		for (int i = 0; i < particles.size(); i++) {
			p = (Particle) particles.get(i);
			if (p.isSelected) {
				indexSelected = i;
				somethingSelected = true;
			}
		}
		// /---clear Springs---///
		p5.println("indexSelected = " + indexSelected);
		p = (Particle) particles.get(indexSelected);
		Spring s = null;

		for (int k = 0; k < springs.size(); k++) {
			s = (Spring) springs.get(k);
			if ((s.a == p) || (s.b == p)) {
				p5.physics.removeSpring(s);
				springs.remove(s);
				possitive = true;
			}
		}
		HE_Vertex v = p5.mesh.getVertexByKey(p.key);
		// // /---clear Faces---///
		// List facesP = (List) v.getFaceStar();
		// for (int i = 0; i < facesP.size(); i++) {
		// HE_Face f = (HE_Face) facesP.get(i);
		// mesh.deleteFace(f);
		// }
		// /---clear Edges---///
		List edgesP = (List) v.getEdgeStar();
		for (int i = 0; i < edgesP.size(); i++) {
			HE_Edge e = (HE_Edge) edgesP.get(i);
			p5.mesh.deleteEdge(e);
		}
		p5.mesh.remove(v);
		// /---clear Particles---///
		this.particles.remove(indexSelected);
		p5.physics.removeParticle(p);
		particles.remove(p);
	}
	// -----------------------------------------------------------------------tut014
}// endClass