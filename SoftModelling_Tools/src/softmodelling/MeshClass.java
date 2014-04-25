package softmodelling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import toxi.geom.Vec3D;
import wblut.geom.WB_Normal3d;
import wblut.geom.WB_Point3d;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HEM_Extrude;
import wblut.hemesh.HEM_Lattice;
import wblut.hemesh.HES_CatmullClark;
import wblut.hemesh.HES_DooSabin;
import wblut.hemesh.HES_Planar;
import wblut.hemesh.HES_PlanarMidEdge;
import wblut.hemesh.HES_Smooth;
import wblut.hemesh.HES_Subdividor;
import wblut.hemesh.HES_TriDec;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render;

class MeshClass {

	HE_Mesh mesh;
	SoftModelling p5;
	ArrayList boxArrayVertices = new ArrayList<BoxClass>();
	ArrayList boxArrayEdges = new ArrayList<BoxClass>();
	ArrayList boxArrayFaces = new ArrayList<BoxClass>();
	HE_Selection selection;
	// ////////////////CONSTRUCTOR
	MeshClass(SoftModelling _p5) {
		p5 = _p5;
		initmesh();
		createFacesBoxes();
		createEdgesBoxes();
		createVerticesBoxes();
	}
	// /////////////////////////

	void run() {

		if (p5.updatePhysics) {
			updatemesh();
		}
		if (p5.displayMesh) {
			rendermesh();
		}
		updateBoxes(true, true, true);
	}

	void updatemesh() {

		for (int h = 0; h < mesh.getVerticesAsList().size(); h++) {
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				HE_Vertex vv = (HE_Vertex) mesh.getVerticesAsList().get(h);
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
		p5.render.drawFaces(mesh);
		p5.fill(255, 0, 255, 200);
		p5.render.drawFaces(selection);
		p5.strokeWeight(1);
		p5.stroke(50);
		p5.render.drawEdges(mesh);
		renderKeys();
	}

	void renderKeys() {
		p5.fill(255, 0, 255);
		if (p5.displayVertexKey) {
			if (p5.selectionMode == 0) {
				for (int h = 0; h < mesh.getVerticesAsList().size(); h++) {
					HE_Vertex vv = (HE_Vertex) mesh.getVerticesAsList().get(h);
					p5.pushMatrix();
					p5.translate((float) vv.x - 5, (float) vv.y, (float) vv.z + 5);
					p5.rotateX(p5.radians(-90));
					p5.text("" + (vv.key() - 0), 0, 0, 0);
					p5.popMatrix();
				}
			}
			if (p5.selectionMode == 1) {
				for (int h = 0; h < mesh.getEdgesAsList().size(); h++) {
					HE_Edge ee = (HE_Edge) mesh.getEdgesAsList().get(h);
					p5.pushMatrix();
					p5.translate((float) ee.getEdgeCenter().x, (float) ee.getEdgeCenter().y, (float) ee.getEdgeCenter().z + 5);
					p5.rotateX(p5.radians(-90));
					p5.text("" + (ee.key() - 0), 0, 0, 0);
					p5.popMatrix();
				}
			}
			if (p5.selectionMode == 2) {
				for (int h = 0; h < mesh.getFacesAsList().size(); h++) {
					HE_Face ff = (HE_Face) mesh.getFacesAsList().get(h);
					p5.pushMatrix();
					p5.translate((float) ff.getFaceCenter().x - 5, (float) ff.getFaceCenter().y, (float) ff.getFaceCenter().z + 5);
					p5.rotateX(p5.radians(-90));
					p5.text("" + (ff.key() - 0), 0, 0, 0);
					p5.popMatrix();
				}
			}
		}
	}

	void initmesh() {
		float totalLength = 700;
		int initCols = 4;
		int initRows = 4;
		int initRowsXCols = initCols * initRows;
		float edgelength = totalLength / (initCols - 1);

		// Array of all vertices
		float[][] vertices = new float[initRowsXCols][3];
		int index = 0;
		for (int j = 0; j < initRows; j++) {
			for (int i = 0; i < initCols; i++) {
				vertices[index][0] = -totalLength / 4 + i * edgelength / 2;
				vertices[index][1] = -totalLength / 4 + j * edgelength / 2;
				vertices[index][2] = 0 + 0;
				index++;
			}
		}
		// Array of faces. Each face is an arry of vertex indices;
		index = 0;
		int[][] faces = new int[(initCols - 1) * (initRows - 1)][];

		for (int j = 0; j < initRows - 1; j++) {
			for (int i = 0; i < initCols - 1; i++) {
				faces[index] = new int[4];
				faces[index][0] = i + initCols * j;
				faces[index][1] = i + 1 + initCols * j;
				faces[index][2] = i + 1 + initCols * (j + 1);
				faces[index][3] = i + initCols * (j + 1);
				index++;
			}
		}

		HEC_FromFacelist facelistCreator = new HEC_FromFacelist().setVertices(vertices).setFaces(faces).setDuplicate(false);
		mesh = new HE_Mesh(facelistCreator);
		mesh.validate(true, true);
		mesh.collapseDegenerateEdges();
		selection = new HE_Selection(mesh);
	}
	void createVerticesBoxes() {
		for (int i = 0; i < mesh.getVerticesAsList().size(); i++) {
			HE_Vertex vv = (HE_Vertex) mesh.getVerticesAsList().get(i);
			BoxClass b = new BoxClass(p5, p5.boxSize, p5.boxSize, p5.boxSize, 0, (vv.key()));
			b.fill(255);
			this.boxArrayVertices.add(b);
			// p5.println("new box " + vv.key());
		}
	}
	void createEdgesBoxes() {
		for (int i = 0; i < mesh.getEdgesAsList().size(); i++) {
			HE_Edge ee = (HE_Edge) mesh.getEdgesAsList().get(i);
			WB_Point3d wbp = ee.getEdgeCenter();
			BoxClass b = new BoxClass(p5, p5.boxSize, p5.boxSize, p5.boxSize, 1, (ee.key()));
			b.fill(255);
			b.moveTo((float) wbp.x, (float) wbp.y, (float) wbp.z);
			this.boxArrayEdges.add(b);
			// p5.println("new box " + ee.key());
		}
	}
	void createFacesBoxes() {
		this.boxArrayFaces.clear();
		for (int i = 0; i < mesh.getFacesAsList().size(); i++) {
			HE_Face ff = (HE_Face) mesh.getFacesAsList().get(i);
			WB_Point3d wbp = ff.getFaceCenter();
			BoxClass b = new BoxClass(p5, p5.boxSize, p5.boxSize, p5.boxSize, 2, (ff.key()));
			b.fill(255);
			b.moveTo((float) wbp.x, (float) wbp.y, (float) wbp.z);
			this.boxArrayFaces.add(b);
			// p5.println("new box " + ff.key());
		}
	}
	void updateBoxes(boolean v, boolean e, boolean f) {
		if (v) {
			for (int i = 0; i < this.boxArrayVertices.size(); i++) {
				BoxClass b = (BoxClass) boxArrayVertices.get(i);
				HE_Vertex vv = (HE_Vertex) mesh.getVertexByKey(b.key);
				b.moveTo((float) vv.x, (float) vv.y, (float) vv.z);
				b.run();
			}
		}
		if (e) {
			for (int i = 0; i < this.boxArrayEdges.size(); i++) {
				BoxClass b = (BoxClass) boxArrayEdges.get(i);
				// HE_Edge ee = (HE_Edge) mesh.getEdgesAsList().get(i);
				HE_Edge ee = (HE_Edge) mesh.getEdgeByKey(b.key);
				WB_Point3d wbp = ee.getEdgeCenter();
				b.moveTo((float) wbp.x, (float) wbp.y, (float) wbp.z);
				b.run();
			}
		}
		if (f) {
			for (int i = 0; i < boxArrayFaces.size(); i++) {
				BoxClass b = (BoxClass) boxArrayFaces.get(i);
				HE_Face ff = (HE_Face) mesh.getFaceByKey(b.key);
				WB_Point3d wbp = ff.getFaceCenter();
				b.moveTo((float) wbp.x, (float) wbp.y, (float) wbp.z);
				b.run();
			}
		}
	}

	BoxClass getBoxeswithKey(List<BoxClass> boxlist, int key) {
		int index = 0;
		for (int i = 0; i < boxlist.size(); i++) {
			BoxClass b = (BoxClass) boxlist.get(i);
			if (b.key == key) {
				index = i;
			}
		}
		BoxClass b2 = (BoxClass) boxlist.get(index);
		return b2;
	}

	void selectPickedEdges(BoxClass b) {

		HE_Edge e = mesh.getEdgeByKey(b.key);
		selection.add(e);
		p5.println("edge " + e.key() + " selected");
		// ////--- select Edges Particles----////
		HE_Vertex v1 = e.getStartVertex();
		HE_Vertex v2 = e.getEndVertex();
		HE_Vertex[] vertices = {v1, v2};
		// --selectParticles
		for (int j = 0; j < vertices.length; j++) {
			HE_Vertex v = (HE_Vertex) vertices[j];
			Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v.key());
			p.isSelected = true;
			BoxClass bv = (BoxClass) getBoxeswithKey(boxArrayVertices, v.key());
			bv.isSelected = true;
		}
	}

	void selectPickedFaces(BoxClass b) {

		HE_Face f = mesh.getFaceByKey(b.key);
		// --selectParticles
		if (f != null) {
		if ((b.isSelected) && (!selection.contains(f))) {

				selection.add(f);
				p5.println("face " + f.key() + " selected");
				// ////--- select Faces Particles----////
				List vertices = f.getFaceVertices();
				for (int j = 0; j < vertices.size(); j++) {
					HE_Vertex v = (HE_Vertex) vertices.get(j);
					Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v.key());
					p.isSelected = true;
					BoxClass bv = (BoxClass) getBoxeswithKey(boxArrayVertices, v.key());
					bv.isSelected = true;
				}
				selection=selection.cleanSelection();
			}
		}
	}
	void deselectBoxes() {
		for (int i = 0; i < this.boxArrayFaces.size(); i++) {
			BoxClass b = (BoxClass) boxArrayFaces.get(i);
			b.isSelected = false;
		}
		for (int i = 0; i < this.boxArrayEdges.size(); i++) {
			BoxClass b = (BoxClass) boxArrayEdges.get(i);
			b.isSelected = false;
		}
		for (int i = 0; i < this.boxArrayVertices.size(); i++) {
			BoxClass b = (BoxClass) boxArrayVertices.get(i);
			b.isSelected = false;
		}
	}
	void lockSelectedFaces(boolean negativelocking) {

		selection.collectVertices();
		for (int i = 0; i < this.selection.getVerticesAsList().size(); i++) {
			HE_Vertex sv = (HE_Vertex) selection.getVerticesAsList().get(i);
			HE_Vertex v = (HE_Vertex) mesh.getVertexByKey(sv.key());
			Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v.key());
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

		for (int i = 0; i < this.boxArrayEdges.size(); i++) {
			BoxClass b = (BoxClass) boxArrayEdges.get(i);
			if (b.isSelected) {
				HE_Edge e = (HE_Edge) mesh.getEdgeByKey(b.key);
				HE_Vertex v1 = (HE_Vertex) e.getStartVertex();
				HE_Vertex v2 = (HE_Vertex) e.getEndVertex();

				Particle p1 = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v1.key());
				Particle p2 = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v2.key());
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
		selectBoxesWithFaces();
		selection.collectVertices();
		selectParticlesInSelection();
	}

	void selectBoxesWithFaces() {
		for (int h = 0; h < selection.getFacesAsList().size(); h++) {
			HE_Face ff = (HE_Face) selection.getFacesAsList().get(h);
			BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayFaces, ff.key());
			b.isSelected = true;
		}
	}

	void selectBoxesWithEdges() {
		for (int h = 0; h < selection.getEdgesAsList().size(); h++) {
			HE_Edge ee = (HE_Edge) selection.getEdgesAsList().get(h);
			BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayEdges, ee.key());
			b.isSelected = true;
		}
	}

	void selectParticlesInSelection() {
		for (int i = 0; i < selection.getVerticesAsList().size(); i++) {
			HE_Vertex v = (HE_Vertex) selection.getVerticesAsList().get(i);
			Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v.key());
			p.isSelected = true;
			BoxClass bv = (BoxClass) getBoxeswithKey(boxArrayVertices, v.key());
			bv.isSelected = true;
		}
	}

	void shrinkMeshSelection() {
		deselectBoxes();
		p5.surface.deselectParticles();
		selection.shrink();
		// ////--- deselect Faces Particles----////
		selection.collectVertices();
		for (int i = 0; i < selection.getFacesAsList().size(); i++) {
			HE_Face f = (HE_Face) selection.getFacesAsList().get(i);
			for (int j = 0; j < f.getFaceVertices().size(); j++) {
				HE_Vertex v = (HE_Vertex) f.getFaceVertices().get(j);
				Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, v.key());
				p.isSelected = true;
				BoxClass bv = (BoxClass) getBoxeswithKey(boxArrayVertices, v.key());
				bv.isSelected = true;
			}
		}
	}

	void selectAllFaces() {
		selection.addFaces(mesh.getFacesAsList());
		for (int i = 0; i < boxArrayFaces.size(); i++) {
			BoxClass b = (BoxClass) boxArrayFaces.get(i);
			b.isSelected = true;
		}
		p5.surface.selectAllParticles();
	}

	void selectAllEdges() {
		selection.addEdges(mesh.getEdgesAsList());
		for (int i = 0; i < boxArrayEdges.size(); i++) {
			BoxClass b = (BoxClass) boxArrayEdges.get(i);
			b.isSelected = true;
		}
		p5.surface.selectAllParticles();
	}

	void subdivideMesh() {

		printCheck();

		List prevmeshvertices = (List) mesh.getVerticesAsList();
		List prevmeshedges = (List) mesh.getEdgesAsList();
		List prevmeshfaces = (List) mesh.getFacesAsList();

		removeUnusedSprings();

		HES_CatmullClark subdiv0 = new HES_CatmullClark();
		HES_DooSabin subdiv1 = new HES_DooSabin();
		HES_Planar subdiv2 = new HES_Planar();
		HES_PlanarMidEdge subdiv3 = new HES_PlanarMidEdge();
		HES_Smooth subdiv4 = new HES_Smooth();
		HES_TriDec subdiv5 = new HES_TriDec().setRep(24);

		if (selection.getFacesAsList().size() > 0) {
			switch (p5.subdivType) {
				case (0) :
					mesh.subdivideSelected(subdiv0.setKeepBoundary(p5.keepBoundBool).setKeepEdges(p5.keepEdgesBool), selection, p5.subdivLevel);
					break;
				case (1) :
					mesh.subdivideSelected(subdiv1, selection, p5.subdivLevel);
					break;
				case (2) :
					mesh.subdivideSelected(subdiv2, selection, p5.subdivLevel);
					break;
				case (3) :
					mesh.subdivideSelected(subdiv3, selection, p5.subdivLevel);
					break;
				case (4) :
					mesh.subdivideSelected(subdiv4.setKeepBoundary(p5.keepBoundBool).setKeepEdges(p5.keepEdgesBool), selection, p5.subdivLevel);
					break;
				case (5) :
					mesh.simplify(subdiv5);
					break;
			}
		} else {
			switch (p5.subdivType) {
				case (0) :
					mesh.subdivide(subdiv0, p5.subdivLevel);
					break;
				case (1) :
					mesh.subdivide(subdiv1, p5.subdivLevel);
					break;
				case (2) :
					mesh.subdivide(subdiv2, p5.subdivLevel);
					break;
				case (3) :
					mesh.subdivide(subdiv3, p5.subdivLevel);
					break;
				case (4) :
					mesh.subdivide(subdiv4, p5.subdivLevel);
					break;
				case (5) :
					mesh.simplify(subdiv5);
					break;
			}
		}

		p5.surface.createNewParticlesFromMesh(prevmeshvertices.size());
		p5.surface.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
		updateBoxes(true, true, true);
		updatemesh();
		selectBoxesWithFaces();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.recomputeAllSpringsToPhysics();
		p5.surface.removeSpringsWithoutBoxes();

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
				HE_Edge e = (HE_Edge) mesh.getEdgeByKey(ee.key());
				Spring s = (Spring) p5.surface.getSpringswithKey(p5.surface.springs, e.key());
				BoxClass b = (BoxClass) getBoxeswithKey(boxArrayEdges, e.key());
				p5.physics.removeSpring(s);
				p5.surface.springs.remove(s);
				boxArrayEdges.remove(b);
			}
		}
	}

	List checkModifiedEdges(List prevmeshfaces) {

		List listToCheck = new ArrayList<HE_Edge>();

		if (selection.getFacesAsList().size() == 0) {
			listToCheck = mesh.getEdgesAsList();
		} else {
			listToCheck.clear();
			List faces = new ArrayList<HE_Face>();

			for (int i = 0; i < mesh.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) mesh.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					if (!faces.contains(ff)) faces.add(ff);
					if (p5.extrSelectNeighbour) selection.add(ff);
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
			p5.println(".....................listToCheck.size() = " + listToCheck.size());
		}

		return listToCheck;
	}

	void extrudeFaces() {
		printCheck();

		List prevmeshvertices = (List) mesh.getVerticesAsList();
		List prevmeshedges = (List) mesh.getEdgesAsList();
		List prevmeshfaces = (List) mesh.getFacesAsList();
		p5.surface.deselectParticles();
		removeUnusedSprings();
		mesh.modifySelected(new HEM_Extrude().setRelative(true).setFuse(true).setDistance(p5.extrDistance).setChamfer(p5.extrChanfer).setPeak(false), selection);
		p5.surface.createNewParticlesFromMesh(prevmeshvertices.size());
		selection.clearEdges();
		selection.collectEdges();
		// p5.surface.removeSpringsWithoutBoxes();

		p5.surface.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
		updatemesh();
		selectBoxesWithFaces();
		// surface.deselectParticles();
		selection.clearVertices();
		selection.collectVertices();
		selectParticlesInSelection();
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
		p5.println("MESH EXTRUDED!!");
		p5.println("mesh.getVerticesAsList().size() = " + mesh.getVerticesAsList().size());
		p5.println("boxArrayVertices.size() = " + boxArrayVertices.size());
		p5.println("surface.particles.size() = " + p5.surface.particles.size());
		p5.println("physics.particles.size() = " + p5.physics.particles.size());
		p5.println("mesh.getEdgesAsList().size() = " + mesh.getEdgesAsList().size());
		p5.println("boxArrayEdges.size() = " + boxArrayEdges.size());
		p5.println("springs.size() = " + p5.surface.springs.size());
		p5.println("physics.springs.size() = " + p5.physics.springs.size());
		p5.println("mesh.getFacesAsList().size() = " + mesh.getFacesAsList().size());
		p5.println("boxArrayFaces.size() = " + boxArrayFaces.size());
	}

	void lattice() {
		List prevmeshvertices = (List) mesh.getVerticesAsList();
		List prevmeshedges = (List) mesh.getEdgesAsList();
		List prevmeshfaces = (List) mesh.getFacesAsList();

		// mesh.fuse(mesh);
		HEM_Lattice modifier = new HEM_Lattice();
		modifier.setWidth(p5.latticeWidth);
		modifier.setDepth(p5.latticeDepth);
		modifier.setThresholdAngle(1.5f * p5.HALF_PI);
		modifier.setFuse(true);
		modifier.setFuseAngle(0.05f * p5.HALF_PI);

		if (selection.getFacesAsList().size() > 0) mesh.modifySelected(modifier, selection);
		else mesh.modify(modifier);
		recomputeMesh(prevmeshvertices, prevmeshedges, prevmeshfaces);

		if (p5.lattSelOldFaces) {
			for (int i = 0; i < prevmeshfaces.size(); i++) {
				HE_Face ff = (HE_Face) prevmeshfaces.get(i);
				selection.add(ff);
			}
		}
		if (p5.lattSelNewFaces) {
			for (int i = 0; i < mesh.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) mesh.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					selection.add(ff);
				}
			}
		}

		selection.clearEdges();
		selection.collectEdges();
		// p5.surface.removeSpringsWithoutBoxes();
		p5.surface.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
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
		
		selectBoxesWithFaces();
		
		printCheck();

		if (p5.lattLockExtrudeParticles) lockSelectedFaces(false);
	}

	void recomputeMesh(List prevmeshvertices, List prevmeshedges, List prevmeshfaces) {
		p5.surface.createNewParticlesFromMesh(prevmeshvertices.size());
		p5.surface.createSpringsFromMesh(this.checkModifiedEdges(prevmeshfaces));
		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
		p5.surface.recomputeSpringsKeys();
	}

	// -----------------------------------------------------------------------tut014

	void killSelectedFaces() {

		printCheck();

		if (p5.killspringsActive) {
			List innerEdges = selection.getInnerEdges();
			for (int i = 0; i < innerEdges.size(); i++) {
				HE_Edge e = (HE_Edge) innerEdges.get(i);
				HE_Edge ee = (HE_Edge) mesh.getEdgeByKey(e.key());
				BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayEdges, ee.key());
				mesh.remove(ee);
				boxArrayEdges.remove(b);
				Spring s = (Spring) p5.surface.getSpringswithKey(p5.surface.springs, ee.key());
				p5.physics.removeSpring(s);
				p5.surface.springs.remove(s);
				selection.remove(ee);
			}

			List innerVertices = selection.getInnerVertices();
			for (int i = 0; i < innerVertices.size(); i++) {
				HE_Vertex vv = (HE_Vertex) innerVertices.get(i);
				BoxClass b = (BoxClass) getBoxeswithKey(boxArrayVertices, vv.key());
				boxArrayVertices.remove(b);
				Particle p = (Particle) p5.surface.getParticleswithKey(p5.surface.particles, vv.key());
				p5.physics.removeParticle(p);
				p5.surface.particles.remove(p);
			}

			List outerEdges = selection.getOuterEdges();
			for (int i = 0; i < outerEdges.size(); i++) {
				HE_Edge ee = (HE_Edge) outerEdges.get(i);
				if (ee.isBoundary()) {
					mesh.remove(ee);
					BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayEdges, ee.key());
					boxArrayEdges.remove(b);
					Spring s = (Spring) p5.surface.getSpringswithKey(p5.surface.springs, ee.key());
					p5.physics.removeSpring(s);
					p5.surface.springs.remove(s);
					selection.remove(ee);
				}
			}
		}

		for (int h = 0; h < selection.getFacesAsList().size(); h++) {
			HE_Face ff = (HE_Face) selection.getFacesAsList().get(h);
			mesh.getFacesAsList().remove(ff);
			BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayFaces, ff.key());
			boxArrayFaces.remove(b);
		}
		if (p5.killspringsActive) {
			mesh.delete(selection);
			p5.surface.recomputeSpringsKeys();
		} else mesh.removeFaces(selection.getFacesAsList());
		selection.clear();

		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
		updatemesh();
		selectBoxesWithFaces();
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

		List prevmeshvertices = (List) mesh.getVerticesAsList();
		List prevmeshedges = (List) mesh.getEdgesAsList();
		List prevmeshfaces = (List) mesh.getFacesAsList();

		for (int h = 0; h < selection.getEdgesAsList().size(); h++) {
			HE_Edge ee = (HE_Edge) selection.getEdgesAsList().get(h);
			// if (ee.isBoundary()) {
			BoxClass b = (BoxClass) getBoxeswithKey(this.boxArrayEdges, ee.key());
			Spring s = (Spring) p5.surface.getSpringswithKey(p5.surface.springs, ee.key());
			p5.physics.removeSpring(s);
			p5.surface.springs.remove(s);
			boxArrayEdges.remove(b);
			mesh.deleteEdge(ee);
			// }
		}

		if (mesh.getFacesAsList().size() > prevmeshfaces.size()) {
			// p5.println(".prevmeshfaces.size() = " + prevmeshfaces.size());
			// p5.println(".meshfaces.size() = " +
			// mesh.getFacesAsList().size());
			for (int i = 0; i < mesh.getFacesAsList().size(); i++) {
				HE_Face ff = (HE_Face) mesh.getFacesAsList().get(i);
				if (!prevmeshfaces.contains(ff)) {
					mesh.deleteFace(ff);
				}
			}
		}

		// ////------ REMOVE UNUSED FACES -----///////
		for (int h = 0; h < prevmeshfaces.size(); h++) {

			HE_Face ff = (HE_Face) prevmeshfaces.get(h);

			if ((prevmeshfaces.contains(ff)) && (!mesh.getFacesAsList().contains(ff))) {
				BoxClass b = (BoxClass) getBoxeswithKey(boxArrayFaces, ff.key());
				boxArrayFaces.remove(b);
			}

		}

		selection.clear();
		boxArrayVertices.clear();
		boxArrayEdges.clear();
		boxArrayFaces.clear();
		createVerticesBoxes();
		createEdgesBoxes();
		createFacesBoxes();
		p5.println("MESH SUBDIVIDED ACTIVE!!");
		updatemesh();
	}
	// -----------------------------------------------------------------------tut014//

	void deselectAll() {
		p5.surface.deselectParticles();
		deselectBoxes();
		selection.clearEdges();
		selection.clearFaces();
		selection.clearVertices();
		selection.clear();
		selection.cleanSelection();
	}
}// end-class