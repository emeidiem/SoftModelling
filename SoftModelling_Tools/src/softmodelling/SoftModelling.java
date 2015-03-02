/* 
 * Sketch Copyright (c) 2014 Manuel Jimenez Garcia
 * HEMesh (c) Frederik Vanhoutte
 * Toxiclibs library (c) 2009 Karsten Schmidt
 * ControlP5 (c) 2009 Andreas Schlegel
 * PeasyCam (c) Jonathan Feinberg
 */

// import OBJ version

package softmodelling;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;
import processing.opengl.PGL;
import peasy.PeasyCam;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;

import java.util.ArrayList;
import java.util.List;

//
//import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileNameExtensionFilter;

import controlP5.*;
import toxi.processing.*;
import wblut.geom.WB_Point3d;
import wblut.hemesh.HEC_FromFacelist;
import wblut.hemesh.HEC_FromObjFile;
import wblut.hemesh.HET_Export;
import wblut.hemesh.HET_Selector;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;
import wblut.processing.WB_Render;

public class SoftModelling extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PGL pgl;
	PImage coronaImg;
	PImage emitterImg;
	PImage particleImg;
	PImage reflectionImg;

	PFont f;
	PeasyCam cam;
	float gravityValue = 9.8f;

	int myColor = color(255, 0, 0);

	boolean displaySelectors = true;
	boolean mouseClicked = false;
	public boolean displayMesh = true;
	public boolean displayPhysics = true;
	boolean updatePhysics = false;
	boolean unlockParticle = false;

	PVector pickedPos;
	Vec3D pickedPos3D;
	int boxSize = 5;

	boolean addingRowsOrCols = false;

	int selectionMode = 0;
	float springlengthScale = 1;

	int subdivLevel = 1;
	boolean keepEdgesBool = true;
	boolean keepBoundBool = true;
	int subdivType = 0;
	boolean killspringsActive = true;

	// ---Extrude---//
	float extrDistance = 0;
	float extrChanfer = 0;
	boolean extrSelectNeighbour = false;
	boolean extrLockExtrudeParticles = false;

	// ---Lattice---//
	float latticeDepth = 0;
	float latticeWidth = 0;
	boolean lattSelOldFaces = false;
	boolean lattSelNewFaces = true;
	boolean lattLockExtrudeParticles = false;

	// ---mouse dragging objects---//
	boolean mouseStillPressed = false;
	boolean activeMover = false;
	boolean activeSPLen = false;
	boolean modeSelected = false;
	boolean showIndex = false;
	boolean showAlphaBlending = false;

	int exportIndex = 106;

	Gui gui;
	MeshClass mesh;
	WB_Render render;
	VerletPhysics physics;
	ToxiclibsSupport gfx;
	Surface surface;
	Gizmo gizmo;
	HET_Selector selector;
	float moveUpValue = 0;
	HEC_FromObjFile meshimport;

	// Create a file chooser
	// final JFileChooser fc = new JFileChooser();

	public void setup() {

		// size(1900, 980, P3D);
		size(1920, 1200, P3D);
		smooth(4);
		pgl = beginPGL();
		coronaImg = loadImage(this.dataPath("AlphaBlending/corona.png"));
		emitterImg = loadImage(this.dataPath("AlphaBlending/emitter.png"));
		particleImg = loadImage(this.dataPath("AlphaBlending/particlePink.png"));
		reflectionImg = loadImage(this.dataPath("AlphaBlending/reflection.png"));

		// size(1920, 1080, P3D); //////

		// size(1920, 1080, P3D);

		// size(1920, 1100, P3D);
		// size(2560, 1440, P3D);
		// size(1040, 900, P3D);

		smooth();
		cursor(CROSS);
		cam = new PeasyCam(this, 600);
		cam.lookAt(0, 0, 0);
		physics = new VerletPhysics();
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0, gravityValue)));
		physics.springs.clear();
		physics.particles.clear();
		gui = new Gui(this);
		initAll();
		// importMesh();

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

		HEC_FromFacelist facelistCreator = new HEC_FromFacelist()
				.setVertices(vertices).setFaces(faces).setDuplicate(false);
		mesh = new MeshClass(this, facelistCreator);
		mesh.validate(true, true);
		mesh.collapseDegenerateEdges();
		mesh.selection = new HE_Selection(mesh);
	}

	void importMesh() {
		// JFileChooser chooser = new JFileChooser();
		// FileNameExtensionFilter filter = new FileNameExtensionFilter(
		// "OBJ mesh", "obj");
		// chooser.setFileFilter(filter);
		// int returnVal = chooser.showOpenDialog(this);
		// if(returnVal == JFileChooser.APPROVE_OPTION) {
		// System.out.println("You chose to open this file: " +
		// chooser.getSelectedFile().getName());
		// }
		// meshimport = new
		// HEC_FromObjFile(chooser.getSelectedFile().getPath());
		meshimport = new HEC_FromObjFile(
				this.dataPath("Meshes/MickeyMouse_superreduced.obj"));

		// meshimport = new
		// HEC_FromObjFile(this.dataPath("Meshes/MickeyMouse_fromRhinoHD_lowres3.obj"));

		// meshimport = new
		// HEC_FromObjFile(this.dataPath("Meshes/MickeyMouse_reduced.obj"));
		// meshimport = new
		// HEC_FromObjFile(this.dataPath("Meshes/MickeyMouse_reduced_quad.obj"));

		meshimport.create();
		HE_Mesh mesh2 = new HE_Mesh(meshimport);
		mesh.clean();
		mesh.clear();
		mesh.add(mesh2);
		mesh.validate(true, true);
		mesh.collapseDegenerateEdges();
		this.physics.particles.clear();
		this.physics.springs.clear();
		surface.particles.clear();
		surface.springs.clear();
		mesh.selection = new HE_Selection(mesh);
		surface.initSurface();
		this.updatePhysics = false;
		gui.gravityOn.setValue(false);
	}

	void initAll() {

		pickedPos = new PVector();
		pickedPos3D = new Vec3D();

		initmesh();
		render = new WB_Render(this);
		surface = new Surface(this);
		gizmo = new Gizmo(this, new Vec3D(0, 0, 0));
		// selector = new HET_Selector(this); // initialize the selector
		this.mesh.printCheck();
		println("....................LAUNCHED...................");
	}

	public void draw() {
		background(0);

		if (gui.cp5.window(this).isMouseOver()) {
			cam.setActive(false);
		} else {
			cam.setActive(true);
		}
		moveGizmo();
		gui.run();
		if (updatePhysics) {
			physics.update();
		}
		surface.run();
		mesh.run();
		if (this.showAlphaBlending)
			drawAlphaBlending();
		// gizmo.run();
		// renderImportmesh();

	}

	void drawAlphaBlending() {
		pgl.depthMask(false);
		pgl.enable(PGL.BLEND);
		pgl.blendFunc(PGL.SRC_ALPHA, PGL.ONE);
	}
	
	void renderImageAB(PImage img, Vec3D _loc, float _diam, int _col, float _alpha ) {
		  pushMatrix();
		  translate( _loc.x, _loc.y, _loc.z );
		  tint(red(_col), green(_col), blue(_col), _alpha);
		  imageMode(CENTER);
		  image(img,0,0,_diam,_diam);
		  popMatrix();
		  tint(1,1,1,1);
		}


	// void renderImportmesh() {
	// noStroke();
	// fill(200);
	// this.render.drawFaces(mesh2);
	// fill(255, 0, 255, 200);
	// //render.drawFaces(selection);
	// strokeWeight(1);
	// stroke(50);
	// render.drawEdges(mesh2);
	// //if (p5.showIndex)
	// //renderKeys();
	// }
	void hitDetect() {

		int precision = 15;
		WB_Point3d[] locators = new WB_Point3d[0];
		if (this.selectionMode == 0)
			locators = mesh.getVerticesAsPoint();
		if (this.selectionMode == 1) {
			HE_Edge[] edgesArray = mesh.getEdgesAsArray();
			locators = new WB_Point3d[edgesArray.length];
			for (int i = 0; i < edgesArray.length; i++) {
				WB_Point3d b = (WB_Point3d) edgesArray[i].getEdgeCenter();
				locators[i] = b;
			}
		}
		if (this.selectionMode == 2)
			locators = mesh.getFaceCenters();

		if (locators.length > 0) {

			for (int i = 0; i < locators.length; i++) {
				WB_Point3d b = (WB_Point3d) locators[i];
				int x = (int) (screenX((float) b.xf(), (float) b.yf(),
						(float) b.zf()));
				int y = (int) (screenY((float) b.xf(), (float) b.yf(),
						(float) b.zf()));
				if (x > mouseX - precision && x < mouseX + precision
						&& y > mouseY - precision && y < mouseY + precision) {

					if (this.selectionMode == 0) {
						HE_Vertex v = mesh.getVerticesAsList().get(i);
						Particle p = surface.getParticleswithKey(
								surface.particles, v.key());
						p.isSelected = true;
						if (!surface.particlesSelected.contains(p)) {
							surface.particlesSelected.add(p);
						}
						if (!surface.particlesSelected.contains(p)) {
							surface.particlesSelected.add(p);
						}
						this.println("PSelect");

					}

					if (this.selectionMode == 1) {

						HE_Edge e = mesh.getEdgesAsArray()[i];
						mesh.selection.add(e);

						Spring s = surface.getSpringswithKey(surface.springs,
								e.key());
						if (!surface.springsSelected.contains(s)) {
							surface.springsSelected.add(s);
							s.isSelected = true;
						}

						Particle p = surface.getParticleswithKey(
								surface.particles, e.getEndVertex().key());
						p.isSelected = true;
						if (!surface.particlesSelected.contains(p))
							surface.particlesSelected.add(p);

						Particle p2 = surface.getParticleswithKey(
								surface.particles, e.getStartVertex().key());
						p2.isSelected = true;
						if (!surface.particlesSelected.contains(p2))
							surface.particlesSelected.add(p2);

					}

					if (this.selectionMode == 2) {

						HE_Face f = mesh.getFacesAsArray()[i];
						mesh.selection.add(f);
						// mesh.selection.addHalfedges(f.getFaceEdges());
						// for (int k = 0; k < f.getFaceEdges().size(); k++) {
						// HE_Halfedge e = mesh.getEdgesAsArray()[i];
						// }
						List<HE_Vertex> vertices = f.getFaceVertices();
						for (int j = 0; j < vertices.size(); j++) {
							HE_Vertex vv = (HE_Vertex) vertices.get(j);
							// Particle p = (Particle) surface.particles.get(j);
							Particle p = surface.getParticleswithKey(
									surface.particles, vv.key());
							p.isSelected = true;
							if (!surface.particlesSelected.contains(p)) {
								surface.particlesSelected.add(p);
							}

						}
						List<HE_Edge> edges = f.getFaceEdges();

						for (int j = 0; j < edges.size(); j++) {
							HE_Edge ee = (HE_Edge) edges.get(j);
							mesh.selection.addEdges(edges);
							// Particle p = (Particle) surface.particles.get(j);
							Spring s = surface.getSpringswithKey(
									surface.springs, ee.key());
							s.isSelected = true;
							if (!surface.springsSelected.contains(s)) {
								surface.springsSelected.add(s);
							}

						}

					}

				}
			}
		}//

		// mesh.renderSelectorsFaces();
	}

	public boolean sketchFullScreen() {
		return false;
	}

	public void mousePressed() {
		mouseClicked = true;
		if (mouseButton == RIGHT) {
			mesh.deselectAll();
			gui.spl.setValue(100f);

		} else {
			hitDetect();
		}
	}

	public void controlEvent(ControlEvent theEvent) {

		if (theEvent.isFrom(gui.radB)) {
			print("got an event from " + theEvent.getName() + "\t");
			for (int i = 0; i < theEvent.getGroup().getArrayValue().length; i++) {
				print((int) (theEvent.getGroup().getArrayValue()[i]));
			}
			println("subdivType = " + (int) theEvent.getValue());
			subdivType = (int) theEvent.group().value();
		}

		else {
			// gui.updateSelector();
			//
			// if (theEvent.controller().name().equals("Vertex")) selectionMode
			// = 0;
			// if (theEvent.controller().name().equals("Edge")) selectionMode =
			// 1;
			// if (theEvent.controller().name().equals("Face")) selectionMode =
			// 2;

		}
		if ((!theEvent.isFrom(gui.bMove))) {
			if ((!theEvent.isFrom(gui.bMoveLeft))
					&& (!theEvent.isFrom(gui.bMoveRight))) {
				if ((!theEvent.isFrom(gui.bMoveUp))
						&& (!theEvent.isFrom(gui.bMoveDown))) {
					if ((!theEvent.isFrom(gui.bMoveZDown))
							&& (!theEvent.isFrom(gui.bMoveZUp))) {
						this.activeMover = false;
					}
				}
			}
		}
		if ((!theEvent.isFrom(gui.bSPLen))) {
			if ((!theEvent.isFrom(gui.spl))) {
				this.activeSPLen = false;
			}
		}
		// if ((!theEvent.isFrom(gui.bMoveUp))) {
		// gui.bMoveUp.setImage(loadImage("icons/SoftModelling_Icon_Icon_ArrowUp_B.png"));
		// }
	}

	void SP_LENGTH(int theValue) {
		if (frameCount > 0) {
			springlengthScale = theValue;
			surface.resizeSprings();
		}
	}

	void GRAVITY(float gravitySlider) {
		if (frameCount > 0) {
			gravityValue = gravitySlider;
			physics.behaviors.clear();
			physics.addBehavior(new GravityBehavior(new Vec3D(0, 0,
					gravityValue)));
			println("a slider event. setting GRAVITY to " + gravityValue);
		}
	}

	// -----------------------------------------------------------------------tut014
	void REMOVE_ELEMENT(float theValue) {
		if (selectionMode == 0)
			surface.killSelectParticles();
		if (selectionMode == 2)
			mesh.killSelectedFaces();
		if (selectionMode == 1)
			mesh.killSelectedEdges();
	}

	void KILL_SPRINGS(boolean theFlag) {
		killspringsActive = theFlag;
	}

	// -----------------------------------------------------------------------tut014//

	void LOCK_ELEMENT(float theValue) {
		if (selectionMode == 0)
			surface.lockSelectParticles();
		if (selectionMode == 1)
			mesh.lockSelectedEdges(false);
		if (selectionMode == 2)
			mesh.lockSelectedFaces(false);
	}

	void UNLOCK_ELEMENT(float theValue) {
		// unlockParticle = true;
		if (selectionMode == 0)
			surface.unlockSelectParticles();
		if (selectionMode == 1)
			mesh.lockSelectedEdges(true);
		if (selectionMode == 2)
			mesh.lockSelectedFaces(true);
	}

	void GROW() {
		mesh.growMeshSelection();
	}

	void SHRINK() {
		mesh.shrinkMeshSelection();
	}

	void SELECT_ALL() {
		if (selectionMode == 0)
			surface.selectAllParticles();
		if (selectionMode == 1)
			mesh.selectAllEdges();
		if (selectionMode == 2)
			mesh.selectAllFaces();
	}

	void DESELECT(float theValue) {
		mesh.deselectAll();
		gui.spl.setValue(100f);
	}

	void SUBDIVIDE_LEVEL(int theValue) {
		subdivLevel = theValue;
	}

	void SUBDIVIDE_KEEPEDGES(boolean theValue) {
		keepEdgesBool = theValue;
	}

	void SUBDIVIDE_KEEPBOUND(boolean theValue) {
		keepBoundBool = theValue;
	}

	void SUBDIVIDE_RUN(float theValue) {
		mesh.subdivideMesh();
		while (this.mesh.getEdgesAsList().size() != this.surface.springs.size()) {
			mesh.cleanUnusedSprings();
		}
	}

	void EXTRUSION_CHANFER(float theValue) {
		extrChanfer = theValue;
	}

	void EXTRUSION_DISTANCE(float theValue) {
		extrDistance = theValue;
	}

	void EXTRUDE_SELECTNEIGHBOUR(boolean theValue) {
		extrSelectNeighbour = theValue;
	}

	void EXTRUDE_LOCKPARTICLES(boolean theValue) {
		extrLockExtrudeParticles = theValue;
	}

	void EXTRUDE_RUN(float theValue) {
		mesh.extrudeFaces();
		// while
		// (this.mesh.getEdgesAsList().size()!=this.surface.springs.size()){
		// mesh.cleanUnusedSprings();}
	}

	// -----------------------------------------------------------------------tut013
	void LATTICE_DEPTH(float theValue) {
		latticeDepth = theValue;
	}

	void LATTICE_WIDTH(float theValue) {
		latticeWidth = theValue;
	}

	void LATTICE_SELOLD(boolean theFlag) {
		lattSelOldFaces = theFlag;
	}

	void LATTICE_SELNEW(boolean theFlag) {
		lattSelNewFaces = theFlag;
	}

	void LATTICE_LOCKPARTICLES(boolean theFlag) {
		lattLockExtrudeParticles = theFlag;
	}

	void LATTICE_RUN() {
		mesh.lattice();
		while (this.mesh.getEdgesAsList().size() != this.surface.springs.size()) {
			mesh.cleanUnusedSprings();
		}
	}

	void EXPORT_STL() {
		HET_Export
				.saveToSTL(mesh, dataPath("mesh" + exportIndex + ".stl"), 1.0);
		exportIndex++;
	}

	void EXPORT_OBJ() {

		HET_Export.saveToOBJ(mesh,
				dataPath("Meshes/SoftModelling_mesh_" + this.year() + "-"
						+ this.month() + "-" + this.day() + "_" + this.hour()
						+ "-" + this.minute() + "-" + this.second() + "_"
						+ frameCount + ".obj"));

		exportIndex++;
	}

	void UPDATE_PHYSICS(boolean theFlag) {
		if (theFlag == true) {
			updatePhysics = true;
		} else {
			updatePhysics = false;
		}
		println("a toggle event.");
	}

	void DISPLAY_SELECTORS(boolean theFlag) {
		displaySelectors = theFlag;
	}

	void DISPLAY_MESH(boolean theFlag) {
		displayMesh = theFlag;
	}

	// /---SIMPLEGUI---////
	void FACEMODE(boolean theFlag) {
		if (theFlag) {
			modeSelected = true;
			this.selectionMode = 2;
			if (gui.bEdges.getState())
				gui.bEdges.setState(false);
			if (gui.bVertex.getState())
				gui.bVertex.setState(false);
		}
	}

	void EDGEMODE(boolean theFlag) {
		if (theFlag) {
			modeSelected = true;
			this.selectionMode = 1;
			if (gui.bVertex.getState())
				gui.bVertex.setState(false);
			if (gui.bFaces.getState())
				gui.bFaces.setState(false);
		}
	}

	void VERTEXMODE(boolean theFlag) {
		if (theFlag) {
			modeSelected = true;
			this.selectionMode = 0;
			if (gui.bFaces.getState())
				gui.bFaces.setState(false);
			if (gui.bEdges.getState())
				gui.bEdges.setState(false);
		}
	}

	void ACTIVATE_MOVE() {
		this.activeMover = !activeMover;
	}

	void ACTIVATE_SPLEN() {
		this.activeSPLen = !activeSPLen;
	}

	void SHELL_RUN() {
		latticeWidth = 1000;
		lattSelNewFaces = true;
		mesh.lattice();
	}

	void SHELL_DEPTH(float theValue) {
		latticeDepth = theValue;
	}

	void RESET_SURFACE() {
		mesh.deselectAll();
		mesh.clean();
		mesh.clearHalfedges();
		mesh.clearFaces();
		mesh.clearHalfedges();
		mesh.clearVertices();
		mesh.clear();

		surface.springs.clear();
		surface.particles.clear();
		physics.springs.clear();
		physics.particles.clear();
		render = new WB_Render(this);
		surface = new Surface(this);

		initAll();
	}

	void SHOW_INDEX() {
		showIndex = !showIndex;
	}

	void SHOW_ALPHABLENDING() {
		showAlphaBlending = !showAlphaBlending;
		if (showAlphaBlending)
			colorMode(RGB, (float) 1.0);
		else
			colorMode(RGB, (float) 255);
	}

	// public void mousePressed() {}

	public void mouseDragged() {
	}

	public void mouseReleased() {
		// gui.prevMoveX += (gui.slider2d.arrayValue()[0] - (gui.size2dSlider /
		// 2)) * 10;
		// gui.prevMoveY += (gui.slider2d.arrayValue()[1] - (gui.size2dSlider /
		// 2)) * 10;
		// gui.prevMoveZ += (gui.sliderZ.arrayValue()[1] - (gui.sizeZSlider /
		// 2)) * 10;
		// gui.slider2d = gui.cp5.addSlider2D("PARTICLE-XY").setPosition(50,
		// 160).setSize(gui.size2dSlider, gui.size2dSlider).setArrayValue(new
		// float[]{gui.size2dSlider / 2, gui.size2dSlider / 2});//
		// .disableCrosshair();
		// gui.sliderZ =
		// gui.cp5.addSlider2D("PARTICLE-Z").setPosition(gui.offsetSliders +
		// gui.size2dSlider, 160).setSize(gui.size2dSlider / 10,
		// gui.size2dSlider)
		// .setArrayValue(new float[]{gui.size2dSlider / 2, gui.size2dSlider /
		// 2}).setLabel("");
	}

	public void keyPressed() {
		// -----------------------------------------------------------------------tut014
		if ((keyCode == DELETE) || (keyCode == BACKSPACE)) {
			if (selectionMode == 0)
				surface.killSelectParticles();
			if (selectionMode == 1)
				mesh.killSelectedEdges();
			if (selectionMode == 2)
				mesh.killSelectedFaces();
		}
		if (key == 's' || key == 'S') {
			saveFrames();
		}
		if (key == 'c' || key == 'C') {
			while (this.mesh.getEdgesAsList().size() != this.surface.springs
					.size()) {
				mesh.cleanUnusedSprings();
			}
		}
		if (key == 'p' || key == 'P') {
			mesh.printCheck();
		}
		if (key == 'a' || key == 'A') {
			while (mesh.selection.getFacesAsList().size() < mesh
					.getFacesAsList().size()) {
				mesh.growMeshSelection();
			}

		}
		if (key == 'e' || key == 'E') {

			HET_Export.saveToOBJ(
					mesh,
					this.sketchPath("data/Meshes/SoftModelling_mesh_"
							+ this.year() + "-" + this.month() + "-"
							+ this.day() + "_" + this.hour() + "-"
							+ this.minute() + "-" + this.second() + "_"
							+ frameCount + ".obj"));

			exportIndex++;
		}

		if (key == 'i' || key == 'I') {
			this.importMesh();
		}

		// -----------------------------------------------------------------------tut014//
		if (gui.cp5.controller("level23") != null) {
			println("removing multilist button level23.");
			gui.cp5.controller("level23").remove();
		}

	}

	public void keyReleased() {
		if (key == 'm' || key == 'M') {
			gizmo.isSelected = false;
		}

	}

	void moveGizmo() {
		if (keyPressed) {
			cam.setMouseControlled(false);
			if (key == 'm' || key == 'M') {

				float scrX = 0, scrY = 0, mouseDisShortest;
				if (!mouseStillPressed) {
					scrX = screenX(gizmo.pos.x, gizmo.pos.y, gizmo.pos.z);
					scrY = screenY(gizmo.pos.x, gizmo.pos.y, gizmo.pos.z);
					mouseDisShortest = sqrt(sq(mouseX - scrX)
							+ sq(mouseY - scrY));
				}
				gizmo.isSelected = true;

				if (mousePressed) {
					mouseStillPressed = true;
					if (mouseButton == LEFT) {

						// float dx = grid.px[indexGP] - gF[indexGF].x;
						// float dy = grid.py[indexGP] - gF[indexGF].y;
						// gF[indexGF].x += dx / 6;
						// gF[indexGF].y += dy / 6;
						float dx = mouseX - scrX;
						float dy = mouseY - scrY;
						gizmo.pos.x += dx / 60;
						gizmo.pos.y += dy / 60;
					} // end if
					if (mouseButton == RIGHT) {
						// gF[indexGF].radius += 1.0 * (pmouseY - mouseY) /
						// 10.0;
						// if (gF[indexGF].radius < 10) gF[indexGF].radius = 10;
						// if (gF[indexGF].radius > 100) gF[indexGF].radius =
						// 100;
					} // end if
				} else {
					mouseStillPressed = false;
				} // end if else

			} // end if (key == 'm' || key == 'M'){
		} else {
			// cam.setMouseControlled(true);

		}
	}

	void saveFrames() {
		String PicName;
		// PicName = ("Images/frame_" + frameCount + ".png");
		PicName = ("Images/frame_" + this.year() + "-" + this.month() + "-"
				+ this.day() + "_" + this.hour() + "-" + this.minute() + "-"
				+ this.second() + "_" + frameCount + ".png");
		saveFrame(PicName);
	}

	public static void main(String _args[]) {
		PApplet.main(new String[] { softmodelling.SoftModelling.class.getName() });
	}
}
