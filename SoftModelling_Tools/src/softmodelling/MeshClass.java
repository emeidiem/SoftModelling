package softmodelling;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import toxi.geom.Line3D;
import toxi.geom.ReadonlyVec3D;
import toxi.geom.Vec3D;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Vector3d;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HEC_FromObjFile;
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
	ArrayList beziersArrayList;

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
		if (p5.showIndex) {
			renderKeys();
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
		if (p5.showAlphaBlending)
			p5.fill(1, 0, 1, .2f);
		else
			p5.fill(255, 0, 255, 200);
		p5.render.drawFaces(selection);
		p5.strokeWeight(1);
		p5.stroke(50);
		p5.render.drawEdges(this);
		// if (p5.showIndex)
		// renderKeys();
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

			if (p5.showAlphaBlending) {
				int c = p5.color(1.0f, 1.0f, 1.0f);
				float radius = 20;
				p5.renderImageAB(p5.particleImg,
						new Vec3D(fc.xf(), fc.yf(), fc.zf()), radius, c, 1.0f);

				if (selection.contains(e)) {
					p5.stroke(1, 1, 1, .5f);
					p5.strokeWeight(2);

					p5.pushMatrix();
					p5.translate(fc.xf(), fc.yf(), fc.zf());
					p5.point(0, 0);
					p5.ellipse(0, 0, 10, 10);
					p5.popMatrix();

				}
			}

			else {

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

	}

	void renderSelectorsFaces() {

		String[] bezierlines = new String[this.getFacesAsList().size()]; //

		for (int i = 0; i < this.getFacesAsList().size(); i++) {
			HE_Face f = (HE_Face) getFacesAsList().get(i);
			WB_Point3d fc = (WB_Point3d) f.getFaceCenter();
			p5.strokeWeight(15);
			if (!selection.contains(f)) {
				p5.stroke(100, 255);

			} else {
				if (!p5.showAlphaBlending)
					p5.stroke(255, 0, 0, 255);
				else
					p5.stroke(1, 0, 1, .3f);
			}
			p5.point(fc.xf(), fc.yf(), fc.zf());
			Vec3D faceCenter = new Vec3D(fc.xf(), fc.yf(), fc.zf());
			Vec3D faceNormal = new Vec3D(f.getFaceNormal().xf(), f
					.getFaceNormal().yf(), f.getFaceNormal().zf());

			Vec3D projectedNormal = new Vec3D(f.getFaceNormal().xf(), f
					.getFaceNormal().yf(), 0); // Vec3D

			// float angleZ = (float) Math.atan(f.getFaceNormal().yf() /
			// f.getFaceNormal().xf()); // float, atan
			// // minuscula
			// float angleY = (float) Math.atan(f.getFaceNormal().zf()/
			// projectedNormal.magnitude()); // susituir
			// // .Length

			float angleX = PApplet.atan(f.getFaceNormal().zf()
					/ f.getFaceNormal().yf());
			float angleY = PApplet.atan(f.getFaceNormal().zf()
					/ -f.getFaceNormal().xf());

			// angleX *= (f.getFaceNormal().yf()>0)? 1 : -1;
			// angleY *= (f.getFaceNormal().xf()>0)? 1 : -1;

			// if (f.getFaceNormal().xf()>0)angleX +=p5.PI;
			// if (f.getFaceNormal().yf()>0)angleY +=p5.PI;

			// Vec3D faceNormalX = new Vec3D(f.getFaceNormal().xf(), 0, f
			// .getFaceNormal().zf());
			// Vec3D faceNormalY = new Vec3D(0, f.getFaceNormal().yf(), f
			// .getFaceNormal().zf());

			// WB_R xz = new Vec3D (f.getFaceNormal.x,0,f.getFaceNormal.z);
			// float angleNormalX =
			// f.getFaceCenter().angleBetween(f.getFaceNormal())

			if (!p5.showAlphaBlending) {
				if (!selection.contains(f))
					p5.stroke(255, 255);
				else
					p5.stroke(255, 0, 0, 255);
				p5.point(fc.xf(), fc.yf(), fc.zf());

			}

			else {
				int col;
				if (!selection.contains(f))
					col = p5.color(1.0f, 1.0f, 1.0f);
				else
					col = p5.color(1.0f, 0.0f, 0.0f);

				float radius = (float) (p5.sqrt((float) f.getFaceArea()) / 1);

				// Draw normals
				p5.strokeWeight(1);
				p5.pushMatrix();
				p5.translate(fc.xf(), fc.yf(), fc.zf());
				p5.stroke(1);
				p5.line(0, 0, 0, f.getFaceNormal().scale(radius / 6 + 1).xf(),
						f.getFaceNormal().scale(radius / 6 + 1).yf(), f
								.getFaceNormal().scale(radius / 6 + 1).zf());
				p5.stroke(1, 0, 1, .4f);
				p5.strokeWeight(4);
				p5.line(0, 0, 0, f.getFaceNormal().scale(radius / 9 + 1).xf(),
						f.getFaceNormal().scale(radius / 9 + 1).yf(), f
								.getFaceNormal().scale(radius / 9 + 1).zf());

				// p5.text(("(" + p5.nf(fnx, 2, 2) + "," + p5.nf(fny, 2, 2) +
				// ","
				// + p5.nf(fnz, 2, 2) + ")"), 0, 0, 0);
				p5.popMatrix();

				// draw ellipse

				p5.pushMatrix();
				p5.translate(fc.xf(), fc.yf(), fc.zf());
				p5.rotateX(angleX);
				Vec3D vertical = new Vec3D(1, 0, 0);
				float angleZ = faceNormal.angleBetween(vertical);
				angleZ *= (f.getFaceNormal().yf() > 0) ? 1 : -1;
				p5.rotateZ(angleZ);
				p5.rotateY(p5.PI / 2);

				p5.renderImageAB(p5.particleImg, new Vec3D(), radius, col, .3f);

				p5.stroke(1, .2f);
				p5.strokeWeight(10);
				p5.noFill();
				// p5.rotateY(angleY);
				// p5.rectMode(p5.CENTER);
				// p5.rect(0, 0, radius / 4, radius / 4);

				p5.ellipse(0, 0, radius / 3, radius / 3);
				p5.stroke(1, .8f);
				p5.strokeWeight(1);
				p5.ellipse(0, 0, radius / 3, radius / 3);

				p5.popMatrix();

				// ////////////////////////////////////////////////////////////////////
				ArrayList<Line3D> l = new ArrayList<Line3D>();
				ArrayList<Vec3D> segments = new ArrayList<Vec3D>();
				ArrayList<WB_Point3d> crvPoints = new ArrayList<WB_Point3d>();
				int numbSegments = 6;

				for (int j = 0; j < f.getFaceVertices().size(); j++) {

					WB_Point3d va = new WB_Point3d();
					WB_Point3d vb = new WB_Point3d();

					va = f.getFaceVertices().get(j);

					if (j < f.getFaceVertices().size() - 1)
						vb = f.getFaceVertices().get(j + 1);
					else
						vb = f.getFaceVertices().get(0);

					WB_Point3d centerAB = interpolate(va, vb, .5f);
					WB_Point3d tan1 = interpolate(va, vb, .25f);
					WB_Point3d tan2 = interpolate(va, vb, .75f);
					crvPoints.add(tan1);
					crvPoints.add(centerAB);
					crvPoints.add(tan2);

				}
				WB_Point3d p1, p2, p3, p4;
				// fullcircle
				p5.strokeWeight(1);
				p5.stroke(1, .3f);
				for (int j = 0; j < f.getFaceVertices().size(); j++) {
					p1 = crvPoints.get(j * 3 + 1);
					p2 = crvPoints.get(j * 3 + 2);
					if (j < f.getFaceVertices().size() - 1) {
						p3 = crvPoints.get(j * 3 + 3);
						p4 = crvPoints.get(j * 3 + 4);
					} else {
						p3 = crvPoints.get(0);
						p4 = crvPoints.get(1);
					}
					p5.bezier((float) p1.x, (float) p1.y, (float) p1.z,
							(float) p2.x, (float) p2.y, (float) p2.z,
							(float) p3.x, (float) p3.y, (float) p3.z,
							(float) p4.x, (float) p4.y, (float) p4.z);
				}
				// incompletecircle
				p5.strokeWeight(3);
				p5.stroke(1, 1f);
				for (int j = 0; j < f.getFaceVertices().size() - 1; j++) {
					p1 = crvPoints.get(j * 3 + 1);
					p2 = crvPoints.get(j * 3 + 2);
					p3 = crvPoints.get(j * 3 + 3);
					p4 = crvPoints.get(j * 3 + 4);

					p5.bezier((float) p1.x, (float) p1.y, (float) p1.z,
							(float) p2.x, (float) p2.y, (float) p2.z,
							(float) p3.x, (float) p3.y, (float) p3.z,
							(float) p4.x, (float) p4.y, (float) p4.z);

					if (p5.exportBeziersOn) {
						bezierlines[i] = p1.x + "," + p1.y + "," + p1.z + "/"
								+ p2.x + "," + p2.y + "," + p2.z + "/" + p3.x
								+ "," + p3.y + "," + p3.z + "/" + p4.x + ","
								+ p4.y + "," + p4.z;
					}
				}

			}

		}
		if (p5.exportBeziersOn) {
			p5.saveStrings("Bezierlines/beziers.txt", bezierlines);
			p5.exportBeziersOn = false;
		}

	}

	WB_Point3d interpolate(WB_Point3d va, WB_Point3d vb, float percentage) {
		WB_Vector3d va2 = va.toVector();
		WB_Vector3d vb2 = vb.toVector();
		WB_Vector3d va_vb = vb2.sub(va2);
		va_vb.scale(percentage);
		va_vb.moveBy(va);

		return va_vb;
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
				p.checkNeighborstoAddPhysics();

			} else {
				p.lock();
				p.keepLocked = true;
				p.lockSelected = true;
				p.checkNeighborstoRemovePhysics();
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
					p1.checkNeighborstoAddPhysics();
					p2.checkNeighborstoAddPhysics();

				} else {
					p1.lock();
					p1.keepLocked = true;
					p1.lockSelected = true;
					p2.lock();
					p2.keepLocked = true;
					p2.lockSelected = true;
					p1.checkNeighborstoRemovePhysics();
					p2.checkNeighborstoRemovePhysics();

				}
			}
		}

	}

	void growMeshSelection() {
		selection.grow();
		List<HE_Vertex> vvs;
		List<HE_Edge> edges;
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
			edges = f.getFaceEdges();
			for (int j = 0; j < edges.size(); j++) {
				HE_Edge ee = (HE_Edge) edges.get(j);
				this.selection.addEdges(edges);
				// Particle p = (Particle) surface.particles.get(j);
				Spring s = p5.surface.getSpringswithKey(p5.surface.springs,
						ee.key());
				s.isSelected = true;
				if (!p5.surface.springsSelected.contains(s)) {
					p5.surface.springsSelected.add(s);
				}

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

		// printCheck();

		List<HE_Vertex> prevmeshvertices = (List<HE_Vertex>) this
				.getVerticesAsList();
		List<HE_Edge> prevmeshedges = (List<HE_Edge>) this.getEdgesAsList();
		List<HE_Face> prevmeshfaces = (List<HE_Face>) this.getFacesAsList();

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
			p5.surface.createNewParticlesFromMesh(
					this.selection.getVerticesAsList(), prevmeshvertices);
			p5.surface.createSpringsFromMesh(this.selection.getInnerEdges());
			p5.surface.createSpringsFromMesh(this.selection.getOuterEdges());
		} else {
			p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(),
					prevmeshvertices);
			p5.surface.createSpringsFromMesh(this.getEdgesAsList());
		}
		// printCheck();
	}

	void cleanUnusedSprings() {

		for (int i = 0; i < p5.surface.springs.size(); i++) {
			Spring s = (Spring) p5.surface.springs.get(i);
			HE_Edge e = (HE_Edge) this.getEdgeByKey(s.key);
			if (((s.a.key == e.getEndVertex().key()) && (s.b.key == e
					.getStartVertex().key()))
					|| ((s.b.key == e.getEndVertex().key()) && (s.a.key == e
							.getStartVertex().key()))) {

			} else {
				p5.physics.removeSpring(s);
				p5.surface.springs.remove(s);
			}

		}

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
		// printCheck();

		List<HE_Vertex> prevmeshvertices = this.getVerticesAsList();
		List<HE_Edge> prevmeshedges = this.getEdgesAsList();
		List<HE_Face> prevmeshfaces = this.getFacesAsList();
		p5.surface.deselectParticles();
		removeUnusedSprings();
		this.modifySelected(new HEM_Extrude().setRelative(true).setFuse(true)
				.setDistance(p5.extrDistance).setChamfer(p5.extrChanfer)
				.setPeak(false), selection);
		p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(),
				prevmeshvertices);
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
		// p5.surface.removeSpringsWithoutBoxes();
		// p5.surface.recomputeAllSpringsToPhysics();
		// p5.surface.recomputeAllSpringsToPhysics();
		// p5.surface.removeSpringsWithoutBoxes();
		// p5.surface.removeSpringsifNotInPhysics();
		// p5.surface.removeDuplicatesSprings();
		// // printCheck();

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

		// printCheck();

		if (p5.lattLockExtrudeParticles)
			lockSelectedFaces(false);
	}

	void selectParticlesInSelection() {
		for (int i = 0; i < this.selection.getVerticesAsList().size(); i++) {
			HE_Vertex v = (HE_Vertex) this.selection.getVerticesAsList().get(i);
			Particle p = (Particle) p5.surface.getParticleswithKey(
					p5.surface.particles, v.key());
			p.isSelected = true;
			if (!p5.surface.particlesSelected.contains(p))
				p5.surface.particlesSelected.add(p);
		}
	}

	void recomputeMesh(List prevmeshvertices, List prevmeshedges,
			List prevmeshfaces) {
		p5.surface.createNewParticlesFromMesh(this.getVerticesAsList(),
				prevmeshvertices);
		p5.surface
				.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		p5.surface.recomputeSpringsKeys();
	}

	// -----------------------------------------------------------------------tut014

	void killSelectedFaces() {

		// printCheck();

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
		// printCheck();

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
		p5.surface.deselectParticles();
		p5.surface.deselectSprings();
	}
}// end-class