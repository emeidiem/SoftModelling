package softmodelling;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import wblut.geom.WB_Point3d;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HEM_Extrude;
import wblut.hemesh.HEM_Lattice;
import wblut.hemesh.HES_CatmullClark;
import wblut.hemesh.HES_DooSabin;
import wblut.hemesh.HES_Planar;
import wblut.hemesh.HES_PlanarMidEdge;
import wblut.hemesh.HES_Smooth;
import wblut.hemesh.HES_TriDec;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

class MeshClass extends HE_Mesh {

	// HE_Mesh mesh;
	SoftModelling p5;
	HE_Selection selection;

	// ////////////////CONSTRUCTOR
	MeshClass(SoftModelling _p5, HEC_FromFacelist facelistCreator) {
		super(facelistCreator);
		p5 = _p5;
		// initmesh();

	}

	// /////////////////////////

	void run() {

		if (p5.updatePhysics) {
			updatemesh();
		}
		if (p5.displayMesh) {
			rendermesh();
		}
		if (p5.displaySelectors) {
			if (p5.selectionMode == 1)
				renderSelectorsEdges();
			if (p5.selectionMode == 2)
				renderSelectorsFaces();
		}

	}

	void updatemesh() {

		for (int j = 0; j < this.getVerticesAsList().size(); j++) {
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				HE_Vertex vv = (HE_Vertex) getVerticesAsList().get(j);
				Particle p = (Particle) p5.surface.particles.get(i);
				if (vv.key() == p.key) {
					vv.set(p.x, p.y, p.z);
				}
			}
		}
	}

	void rendermesh() {
		p5.noStroke();
		p5.fill(200);
		p5.render.drawFaces(this);
		p5.fill(255, 0, 255, 200);
		p5.render.drawFaces(selection);
		p5.strokeWeight(1);
		p5.stroke(50);
		p5.render.drawEdges(this);
		if (p5.displayVertexKey)
			renderKeys();
	}

	void renderKeys() {
		p5.fill(255, 0, 255);

		if (p5.selectionMode == 0) {
			for (int h = 0; h < this.getVerticesAsList().size(); h++) {
				HE_Vertex vv = (HE_Vertex) this.getVerticesAsList().get(h);
				p5.pushMatrix();
				p5.translate((float) vv.x - 5, (float) vv.y, (float) vv.z + 5);
				p5.rotateX(PApplet.radians(-90));
				p5.text("" + (vv.key() - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
		if (p5.selectionMode == 1) {
			for (int h = 0; h < this.getEdgesAsList().size(); h++) {
				HE_Edge ee = (HE_Edge) this.getEdgesAsList().get(h);
				p5.pushMatrix();
				p5.translate((float) ee.getEdgeCenter().x,
						(float) ee.getEdgeCenter().y,
						(float) ee.getEdgeCenter().z + 5);
				p5.rotateX(PApplet.radians(-90));
				p5.text("" + (ee.key() - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}
		if (p5.selectionMode == 2) {
			for (int h = 0; h < this.getFacesAsList().size(); h++) {
				HE_Face ff = (HE_Face) this.getFacesAsList().get(h);
				p5.pushMatrix();
				p5.translate((float) ff.getFaceCenter().x - 5,
						(float) ff.getFaceCenter().y,
						(float) ff.getFaceCenter().z + 5);
				p5.rotateX(PApplet.radians(-90));
				p5.text("" + (ff.key() - 0), 0, 0, 0);
				p5.popMatrix();
			}
		}

	}

	void renderSelectorsEdges() {
		for (int i = 0; i < this.getEdgesAsList().size(); i++) {
			HE_Edge e = (HE_Edge) getEdgesAsList().get(i);
			WB_Point3d fc = (WB_Point3d) e.getEdgeCenter();
			p5.strokeWeight(15);
			if (!selection.contains(e)) {
				p5.stroke(100, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(fc.xf(), fc.yf(), fc.zf());

			p5.strokeWeight(10);
			if (!selection.contains(e)) {
				p5.stroke(255, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(fc.xf(), fc.yf(), fc.zf());

		}

	}

	void renderSelectorsFaces() {
		for (int i = 0; i < this.getFacesAsList().size(); i++) {
			HE_Face f = (HE_Face) getFacesAsList().get(i);
			WB_Point3d fc = (WB_Point3d) f.getFaceCenter();
			p5.strokeWeight(15);
			if (!selection.contains(f)) {
				p5.stroke(100, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(fc.xf(), fc.yf(), fc.zf());

			p5.strokeWeight(10);
			if (!selection.contains(f)) {
				p5.stroke(255, 255);

			} else {
				p5.stroke(255, 0, 0, 255);
			}
			p5.point(fc.xf(), fc.yf(), fc.zf());

		}

	}

	void lockSelectedFaces(boolean negativelocking) {

		selection.collectVertices();
		for (int i = 0; i < this.selection.getVerticesAsList().size(); i++) {
			HE_Vertex sv = (HE_Vertex) selection.getVerticesAsList().get(i);
			HE_Vertex v = (HE_Vertex) this.getVertexByKey(sv.key());
			Particle p = (Particle) p5.surface.getParticleswithKey(
					p5.surface.particles, v.key());
			if (negativelocking) {
				p.unlock();
				p.keepLocked = false;
				p.lockSelected = false;
			} else {
				p.lock();
				p.keepLocked = true;
				p.lockSelected = true;
			}
		}
	}

	void lockSelectedEdges(boolean negativelocking) {

		for (int i = 0; i < p5.surface.springs.size(); i++) {
			Spring s = (Spring) p5.surface.springs.get(i);
			if (s.isSelected) {
				HE_Edge e = (HE_Edge) this.getEdgeByKey(s.key);
				HE_Vertex v1 = (HE_Vertex) e.getStartVertex();
				HE_Vertex v2 = (HE_Vertex) e.getEndVertex();

				Particle p1 = (Particle) p5.surface.getParticleswithKey(
						p5.surface.particles, v1.key());
				Particle p2 = (Particle) p5.surface.getParticleswithKey(
						p5.surface.particles, v2.key());
				if (negativelocking) {
					p1.unlock();
					p1.keepLocked = false;
					p1.lockSelected = false;
					p2.unlock();
					p2.keepLocked = false;
					p2.lockSelected = false;
				} else {
					p1.lock();
					p1.keepLocked = true;
					p1.lockSelected = true;
					p2.lock();
					p2.keepLocked = true;
					p2.lockSelected = true;
				}
			}
		}
	}

	void growMeshSelection() {
		selection.grow();
		List<HE_Vertex> vvs;
		HE_Face f;
		Particle p;
		for (int i = 0; i < selection.getFacesAsList().size(); i++) {
			f = selection.getFacesAsList().get(i);
			vvs = f.getFaceVertices();
			for (int j = 0; j < vvs.size(); j++) {
				HE_Vertex v = (HE_Vertex) vvs.get(j);
				p = (Particle) p5.surface.getParticleswithKey(
						p5.surface.particles, v.key());
				p.isSelected = true;
				if (!p5.surface.particlesSelected.contains(p))
					p5.surface.particlesSelected.add(p);
			}
		}
		selection.collectHalfedges();
		selection.collectVertices();
	}

	void shrinkMeshSelection() {

		final List<HE_Halfedge> outerEdges = selection.getOuterHalfedges();

		for (int i = 0; i < outerEdges.size(); i++) {
			Particle p;
			final HE_Halfedge e = outerEdges.get(i);
			p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles,
					e.getVertex().key());
			p.isSelected = false;
			if (p5.surface.particlesSelected.contains(p))
				p5.surface.particlesSelected.remove(p);
			p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles,
					e.getEndVertex().key());
			p.isSelected = false;
			if (p5.surface.particlesSelected.contains(p))
				p5.surface.particlesSelected.remove(p);
		}
		this.selection.shrink();

	}

	void subdivideMesh() {

		printCheck();

		List <HE_Vertex>prevmeshvertices = (List<HE_Vertex>) this.getVerticesAsList();
		List <HE_Edge>prevmeshedges = (List<HE_Edge>) this.getEdgesAsList();
		List <HE_Face>prevmeshfaces = (List<HE_Face>) this.getFacesAsList();

		removeUnusedSprings();

		HES_CatmullClark subdiv0 = new HES_CatmullClark();
		HES_DooSabin subdiv1 = new HES_DooSabin();
		HES_Planar subdiv2 = new HES_Planar();
		HES_PlanarMidEdge subdiv3 = new HES_PlanarMidEdge();
		HES_Smooth subdiv4 = new HES_Smooth();
		HES_TriDec subdiv5 = new HES_TriDec().setRep(24);

		if (selection.getFacesAsList().size() > 0) {
			switch (p5.subdivType) {
			case (0):
				this.subdivideSelected(
						subdiv0.setKeepBoundary(p5.keepBoundBool).setKeepEdges(
								p5.keepEdgesBool), selection, p5.subdivLevel);
				break;
			case (1):
				this.subdivideSelected(subdiv1, selection, p5.subdivLevel);
				break;
			case (2):
				this.subdivideSelected(subdiv2, selection, p5.subdivLevel);
				break;
			case (3):
				this.subdivideSelected(subdiv3, selection, p5.subdivLevel);
				break;
			case (4):
				this.subdivideSelected(
						subdiv4.setKeepBoundary(p5.keepBoundBool).setKeepEdges(
								p5.keepEdgesBool), selection, p5.subdivLevel);
				break;
			case (5):
				this.simplify(subdiv5);
				break;
			}
		} else {
			switch (p5.subdivType) {
			case (0):
				this.subdivide(subdiv0, p5.subdivLevel);
				break;
			case (1):
				this.subdivide(subdiv1, p5.subdivLevel);
				break;
			case (2):
				this.subdivide(subdiv2, p5.subdivLevel);
				break;
			case (3):
				this.subdivide(subdiv3, p5.subdivLevel);
				break;
			case (4):
				this.subdivide(subdiv4, p5.subdivLevel);
				break;
			case (5):
				this.simplify(subdiv5);
				break;
			}
		}

		if (selection.getFacesAsList().size() > 0) {
			selection.collectHalfedges();
			selection.collectVertices();
			p5.surface.createNewParticlesFromMesh(this.selection
					.getVerticesAsList(), prevmeshvertices);
			p5.surface.createSpringsFromMesh(this.selection.getInnerEdges());
			p5.surface.createSpringsFromMesh(this.selection.getOuterEdges());
		} else {
			p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(), prevmeshvertices);
			p5.surface.createSpringsFromMesh(this.getEdgesAsList());
		}
		printCheck();
	}

	void removeUnusedSprings() {

		if (selection.getFacesAsList().size() == 0) {
			// ///---REMOVE ALL SPRINGS---////
			p5.physics.springs.clear();
			p5.surface.springs.clear();
		} else {
			selection.clearEdges();
			selection.clearHalfedges();
			List<HE_Edge> edgesSel = selection.getOuterEdges();
			for (int i = 0; i < edgesSel.size(); i++) {
				HE_Edge ee = (HE_Edge) edgesSel.get(i);
				HE_Edge e = (HE_Edge) this.getEdgeByKey(ee.key());
				Spring s = (Spring) p5.surface.getSpringswithKey(
						p5.surface.springs, e.key());
				p5.physics.removeSpring(s);
				p5.surface.springs.remove(s);
			}
		}
	}

	List checkModifiedEdges(List prevmeshfaces) {

		List listToCheck = new ArrayList<HE_Edge>();

		if (selection.getFacesAsList().size() == 0) {
			listToCheck = this.getEdgesAsList();
		} else {
			listToCheck.clear();
			List faces = new ArrayList<HE_Face>();

			for (int i = 0; i < this.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) this.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					if (!faces.contains(ff))
						faces.add(ff);
					if (p5.extrSelectNeighbour)
						selection.add(ff);
				}
			}

			for (int h = 0; h < selection.getFacesAsList().size(); h++) {
				HE_Face ff = (HE_Face) selection.getFacesAsList().get(h);
				if (!faces.contains(ff)) {
					faces.add(ff);
				}
			}

			for (int h = 0; h < faces.size(); h++) {
				HE_Face ff = (HE_Face) faces.get(h);
				List edgesSel = ff.getFaceEdges();
				for (int i = 0; i < edgesSel.size(); i++) {

					HE_Edge e = (HE_Edge) edgesSel.get(i);
					if (!listToCheck.contains(e)) {
						listToCheck.add(e);
					}
				}
			}
			p5.println(".....................listToCheck.size() = "
					+ listToCheck.size());
		}

		return listToCheck;
	}

	void extrudeFaces() {
		printCheck();

		List<HE_Vertex> prevmeshvertices = this.getVerticesAsList();
		List<HE_Edge> prevmeshedges = this.getEdgesAsList();
		List<HE_Face> prevmeshfaces = this.getFacesAsList();
		p5.surface.deselectParticles();
		removeUnusedSprings();
		this.modifySelected(new HEM_Extrude().setRelative(true).setFuse(true)
				.setDistance(p5.extrDistance).setChamfer(p5.extrChanfer)
				.setPeak(false), selection);
		p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(), prevmeshvertices);
		selection.clearEdges();
		selection.collectEdges();
		// p5.surface.removeSpringsWithoutBoxes();

		p5.surface
				.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));

		updatemesh();
		// surface.deselectParticles();
		selection.clearVertices();
		selection.collectVertices();
		if (p5.extrLockExtrudeParticles) {
			lockSelectedFaces(false);
		}
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.removeSpringsifNotInPhysics();
		p5.surface.removeDuplicatesSprings();
		printCheck();

	}

	void printCheck() {
		PApplet.println("MESH EXTRUDED!!");
		PApplet.println("this.getVerticesAsList().size() = "
				+ this.getVerticesAsList().size());
		PApplet.println("surface.particles.size() = "
				+ p5.surface.particles.size());
		PApplet.println("physics.particles.size() = "
				+ p5.physics.particles.size());
		PApplet.println("this.getEdgesAsList().size() = "
				+ this.getEdgesAsList().size());
		PApplet.println("springs.size() = " + p5.surface.springs.size());
		PApplet.println("physics.springs.size() = " + p5.physics.springs.size());
		PApplet.println("this.getFacesAsList().size() = "
				+ this.getFacesAsList().size());
	}

	void lattice() {
		List prevmeshvertices = (List) this.getVerticesAsList();
		List prevmeshedges = (List) this.getEdgesAsList();
		List prevmeshfaces = (List) this.getFacesAsList();

		// this.fuse(this);
		HEM_Lattice modifier = new HEM_Lattice();
		modifier.setWidth(p5.latticeWidth);
		modifier.setDepth(p5.latticeDepth);
		modifier.setThresholdAngle(1.5f * p5.HALF_PI);
		modifier.setFuse(true);
		modifier.setFuseAngle(0.05f * p5.HALF_PI);

		if (selection.getFacesAsList().size() > 0)
			this.modifySelected(modifier, selection);
		else
			this.modify(modifier);
		recomputeMesh(prevmeshvertices, prevmeshedges, prevmeshfaces);

		if (p5.lattSelOldFaces) {
			for (int i = 0; i < prevmeshfaces.size(); i++) {
				HE_Face ff = (HE_Face) prevmeshfaces.get(i);
				selection.add(ff);
			}
		}
		if (p5.lattSelNewFaces) {
			for (int i = 0; i < this.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) this.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					selection.add(ff);
				}
			}
		}

		selection.clearEdges();
		selection.collectEdges();
		// p5.surface.removeSpringsWithoutBoxes();
		p5.surface
				.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		updatemesh();

		// surface.deselectParticles();
		selection.clearVertices();
		selection.collectVertices();
		selectParticlesInSelection();
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.removeSpringsifNotInPhysics();
		p5.surface.removeDuplicatesSprings();

		printCheck();

		if (p5.lattLockExtrudeParticles)
			lockSelectedFaces(false);
	}

	void selectParticlesInSelection() {
		for (int i = 0; i < this.selection.getVerticesAsList().size(); i++) {
			HE_Vertex v = (HE_Vertex) this.selection.getVerticesAsList().get(i);
			Particle p = (Particle) p5.surface.getParticleswithKey(
					p5.surface.particles, v.key());
			p.isSelected = true;
		}
	}

	void recomputeMesh(List prevmeshvertices, List prevmeshedges,
			List prevmeshfaces) {
		p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(), prevmeshvertices);
		p5.surface
				.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		p5.surface.recomputeSpringsKeys();
	}

	// -----------------------------------------------------------------------tut014

	void killSelectedFaces() {

		printCheck();

		if (p5.killspringsActive) {
			List innerEdges = selection.getInnerEdges();
			for (int i = 0; i < innerEdges.size(); i++) {
				HE_Edge e = (HE_Edge) innerEdges.get(i);
				HE_Edge ee = (HE_Edge) this.getEdgeByKey(e.key());
				this.remove(ee);
				Spring s = (Spring) p5.surface.getSpringswithKey(
						p5.surface.springs, ee.key());
				p5.physics.removeSpring(s);
				p5.surface.springs.remove(s);
				selection.remove(ee);
			}

			List innerVertices = selection.getInnerVertices();
			for (int i = 0; i < innerVertices.size(); i++) {
				HE_Vertex vv = (HE_Vertex) innerVertices.get(i);
				Particle p = (Particle) p5.surface.getParticleswithKey(
						p5.surface.particles, vv.key());
				p5.physics.removeParticle(p);
				p5.surface.particles.remove(p);
			}

			List outerEdges = selection.getOuterEdges();
			for (int i = 0; i < outerEdges.size(); i++) {
				HE_Edge ee = (HE_Edge) outerEdges.get(i);
				if (ee.isBoundary()) {
					this.remove(ee);
					Spring s = (Spring) p5.surface.getSpringswithKey(
							p5.surface.springs, ee.key());
					p5.physics.removeSpring(s);
					p5.surface.springs.remove(s);
					selection.remove(ee);
				}
			}
		}

		for (int h = 0; h < selection.getFacesAsList().size(); h++) {
			HE_Face ff = (HE_Face) selection.getFacesAsList().get(h);
			this.getFacesAsList().remove(ff);

		}
		if (p5.killspringsActive) {
			this.delete(selection);
			p5.surface.recomputeSpringsKeys();
		} else
			this.removeFaces(selection.getFacesAsList());
		selection.clear();

		updatemesh();
		// surface.deselectParticles();
		// selection.clearVertices();
		// selection.collectVertices();
		// selectParticlesInSelection();
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.removeSpringsWithoutBoxes();
		p5.surface.removeSpringsifNotInPhysics();
		p5.surface.removeDuplicatesSprings();
		printCheck();

	}

	void killSelectedEdges() {

		List prevmeshvertices = (List) this.getVerticesAsList();
		List prevmeshedges = (List) this.getEdgesAsList();
		List prevmeshfaces = (List) this.getFacesAsList();

		for (int h = 0; h < selection.getEdgesAsList().size(); h++) {
			HE_Edge ee = (HE_Edge) selection.getEdgesAsList().get(h);
			// if (ee.isBoundary()) {
			Spring s = (Spring) p5.surface.getSpringswithKey(
					p5.surface.springs, ee.key());
			p5.physics.removeSpring(s);
			p5.surface.springs.remove(s);
			this.deleteEdge(ee);
			// }
		}

		if (this.getFacesAsList().size() > prevmeshfaces.size()) {
			// p5.println(".prevmeshfaces.size() = " + prevmeshfaces.size());
			// p5.println(".meshfaces.size() = " +
			// this.getFacesAsList().size());
			for (int i = 0; i < this.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) this.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					this.deleteFace(ff);
				}
			}
		}

		// ////------ REMOVE UNUSED FACES -----///////
		for (int h = 0; h < prevmeshfaces.size(); h++) {

			HE_Face ff = (HE_Face) prevmeshfaces.get(h);

			if ((prevmeshfaces.contains(ff))
					&& (!this.getFacesAsList().contains(ff))) {

			}

		}

		selection.clear();
		p5.println("MESH SUBDIVIDED ACTIVE!!");
		updatemesh();
	}

	// -----------------------------------------------------------------------tut014//

	void deselectAll() {
		p5.surface.deselectParticles();
		selection.clearEdges();
		selection.clearFaces();
		selection.clearVertices();
		selection.clear();
		selection.cleanSelection();
	}
}// end-class