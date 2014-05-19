/* 
 * Sketch Copyright (c) 2014 Manuel Jimenez Garcia
 * HEMesh (c) Frederik Vanhoutte
 * Toxiclibs library (c) 2009 Karsten Schmidt
 * ControlP5 (c) 2009 Andreas Schlegel
 * PeasyCam (c) Jonathan Feinberg
 */

package softmodelling;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import peasy.PeasyCam;
import toxi.geom.Rect;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;
import toxi.physics.behaviors.GravityBehavior;
import toxi.physics.behaviors.ParticleBehavior;
import toxi.physics.VerletParticle;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.WETriangleMesh;

import java.util.ArrayList;
import java.util.Iterator;

import controlP5.*;
import shapes3d.utils.*;
import shapes3d.*;
import toxi.processing.*;
import wblut.hemesh.HET_Export;
import wblut.processing.WB_Render;

public class SoftModelling extends PApplet {

	PFont f;
	PeasyCam cam;
	float gravityValue = 9.8f;

	int myColor = color(255, 0, 0);

	boolean displaySelectors = true;
	boolean displayVertexKey = false;
	boolean mouseClicked = false;
	public boolean displayMesh = true;
	boolean updatePhysics = false;
	boolean unlockParticle = false;

	PVector pickedPos = new PVector();
	Vec3D pickedPos3D = new Vec3D();
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
	boolean lattSelNewFaces = false;
	boolean lattLockExtrudeParticles = false;

	// ---mouse dragging objects---//
	boolean mouseStillPressed = false;

	int exportIndex = 106;

	Gui gui;
	MeshClass mesh;
	WB_Render render;
	VerletPhysics physics;
	ToxiclibsSupport gfx;
	Surface surface;
	ArrayList boxesSelected = new ArrayList<BoxClass>();
	Gizmo gizmo;

	public void setup() {
		// size(1900, 980, P3D);
		size(1900, 1080, P3D);
		smooth();
		cursor(CROSS);

		cam = new PeasyCam(this, 600);
		cam.lookAt(0, 0, 0);

		gui = new Gui(this);
		mesh = new MeshClass(this);
		render = new WB_Render(this);
		physics = new VerletPhysics();
		physics.addBehavior(new GravityBehavior(new Vec3D(0, 0, 9.8f)));
		surface = new Surface(this);
		gizmo = new Gizmo(this, new Vec3D(0, 0, 0));
		println("github check");
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
		if (mouseClicked) {
			pickBox();
			mouseClicked = false;
		}
		renderPickedBox();
		gui.run();
		if (updatePhysics) {
			physics.update();
		}
		surface.run();
		mesh.run();
		gizmo.run();

	}
	void pickBox() {
		BoxClass picked = (BoxClass) Shape3D.pickShape(this, mouseX, mouseY);
		if (picked != null) {
			picked.isSelected = true;
			picked.fill(color(255, 0, 0));
			pickedPos = picked.getPosVec();
			pickedPos3D = new Vec3D(pickedPos.x, pickedPos.y, pickedPos.z);
			if (!boxesSelected.contains(picked)) boxesSelected.add(picked);

			if (this.selectionMode == 0) surface.getParticleswithKey(surface.particles, picked.key).isSelected = true;
			if (this.selectionMode == 1) mesh.selectPickedEdges(picked);
			if (this.selectionMode == 2) mesh.selectPickedFaces(picked);
			gizmo.calculateCentroidSelection();
		}
	}

	void renderPickedBox() {
		strokeWeight(30);
		stroke(255, 0, 255, 100);
		point(pickedPos3D.x, pickedPos3D.y, pickedPos3D.z);
	}

	public void mouseClicked() {
		mouseClicked = true;
		if (mouseButton == RIGHT) {
			mesh.deselectAll();
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
			physics.addBehavior(new GravityBehavior(new Vec3D(0, 0, gravityValue)));
			println("a slider event. setting GRAVITY to " + gravityValue);
		}
	}
	// -----------------------------------------------------------------------tut014
	void REMOVE_ELEMENT(float theValue) {
		if (selectionMode == 0) surface.killSelectParticles();
		if (selectionMode == 2) mesh.killSelectedFaces();
		if (selectionMode == 1) mesh.killSelectedEdges();
	}

	void KILL_SPRINGS(boolean theFlag) {
		killspringsActive = theFlag;
	}
	// -----------------------------------------------------------------------tut014//

	void LOCK_ELEMENT(float theValue) {
		if (selectionMode == 0) surface.lockSelectParticles();
		if (selectionMode == 1) mesh.lockSelectedEdges(false);
		if (selectionMode == 2) mesh.lockSelectedFaces(false);
	}

	void UNLOCK_ELEMENT(float theValue) {
		// unlockParticle = true;
		if (selectionMode == 0) surface.unlockSelectParticles();
		if (selectionMode == 1) mesh.lockSelectedEdges(true);
		if (selectionMode == 2) mesh.lockSelectedFaces(true);
	}
	void GROW() {
		mesh.growMeshSelection();
	}

	void SHRINK() {
		mesh.shrinkMeshSelection();
	}

	void SELECT_ALL() {
		if (selectionMode == 0) surface.selectAllParticles();
		if (selectionMode == 1) mesh.selectAllEdges();
		if (selectionMode == 2) mesh.selectAllFaces();
	}

	void DESELECT(float theValue) {
		mesh.deselectAll();
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
	}
	void EXPORT_STL() {
		HET_Export.saveToSTL(mesh.mesh, dataPath("mesh" + exportIndex + ".stl"), 1.0);
		exportIndex++;
	}

	void EXPORT_OBJ() {
		// Sting meshName =
		//
		// String meshName = ("Meshes/mesh_" + this.year() + "-" + this.month()
		// + "-" + this.day() + "_" + this.hour() + "-" + this.minute() + "-" +
		// this.second() + "_" + frameCount);
		// HET_Export.saveToOBJ(mesh.mesh, dataPath(meshName + ".obj"));

		HET_Export.saveToOBJ(mesh.mesh, dataPath("Meshes/SoftModelling_mesh_" + this.year() + "-" + this.month() + "-" + this.day() + "_" + this.hour() + "-" + this.minute() + "-" + this.second()
				+ "_" + frameCount + ".obj"));
		// HET_Export.saveToOBJ(mesh.mesh, dataPath("mesh" + exportIndex +
		// ".obj"));

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

	void RESET(float theValue) {}

	void DISPLAY_SELECTORS(boolean theFlag) {
		displaySelectors = theFlag;
	}

	void DISPLAY_KEY(boolean theFlag) {
		displayVertexKey = theFlag;
	}

	void DISPLAY_MESH(boolean theFlag) {
		displayMesh = theFlag;
	}

	// /---SIMPLEGUI---////
	void FACEMODE(boolean theFlag) {
		if (theFlag) {
			this.selectionMode = 2;
			if (gui.bEdges.getState()) gui.bEdges.setState(false);
			if (gui.bVertex.getState()) gui.bVertex.setState(false);
		}
	}
	void EDGEMODE(boolean theFlag) {
		if (theFlag) {
			this.selectionMode = 1;
			if (gui.bVertex.getState()) gui.bVertex.setState(false);
			if (gui.bFaces.getState()) gui.bFaces.setState(false);
		}
	}
	void VERTEXMODE(boolean theFlag) {
		if (theFlag) {
			this.selectionMode = 0;
			if (gui.bFaces.getState()) gui.bFaces.setState(false);
			if (gui.bEdges.getState()) gui.bEdges.setState(false);
		}
	}

	public void mousePressed() {}

	public void mouseDragged() {}

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
			if (selectionMode == 0) surface.killSelectParticles();
			if (selectionMode == 1) mesh.killSelectedEdges();
			if (selectionMode == 2) mesh.killSelectedFaces();
		}
		if (key == 's' || key == 'S') {
			saveFrames();
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
					mouseDisShortest = sqrt(sq(mouseX - scrX) + sq(mouseY - scrY));
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
		PicName = ("Images/frame_" + this.year() + "-" + this.month() + "-" + this.day() + "_" + this.hour() + "-" + this.minute() + "-" + this.second() + "_" + frameCount + ".png");
		saveFrame(PicName);
	}

	public static void main(String _args[]) {
		PApplet.main(new String[]{softmodelling.SoftModelling.class.getName()});
	}
}
