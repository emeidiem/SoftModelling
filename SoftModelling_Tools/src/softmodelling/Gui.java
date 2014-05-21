package softmodelling;

import processing.core.PImage;
import toxi.geom.Vec3D;
import toxi.physics.behaviors.GravityBehavior;
import wblut.hemesh.HET_Export;
import controlP5.Button;
import controlP5.CColor;
import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Knob;
import controlP5.MultiList;
import controlP5.MultiListButton;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Slider2D;
import controlP5.Toggle;

public class Gui {
	SoftModelling p5;
	ControlP5 cp5;

	int size2dSlider, sizeZSlider;
	int offsetSliders = 60;
	int buttonDefWidth;
	float prevMoveX, prevMoveY, prevMoveZ;
	float valueZ;
	Group g1, g2, g3, g4, g5;
	Button b1, b2, b3, b4, bExtrude, bGrow, bShrink, bSubdivide, bDeselect, bLock, bUnlock, bc1, bc2, bc3, bc4, bc5, bSaveFile;
	Toggle bVertex, bEdges, bFaces, gravityOn;
	Toggle t1;
	Button bMove;
	Button bMoveDown, bMoveUp, bMoveLeft, bMoveRight, bMoveZUp, bMoveZDown;
	Slider2D slider2d;
	Slider2D sliderZ;
	Knob spl, gravityKnob, extrudeDistKnob, extrudeChanferKnob;
	RadioButton radB;
	int sizeIcons = 100;
	int margin = 50;
	int space = 20;

	// ////////////////CONSTRUCTOR
	Gui(SoftModelling _p5) {
		p5 = _p5;
		cp5 = new ControlP5(p5);

		size2dSlider = 100;
		sizeZSlider = size2dSlider;
		buttonDefWidth = size2dSlider + offsetSliders - 50 + size2dSlider / 10;
		// createButtons();
		createButtonsSimple();
	}
	// /////////////////////////

	void run() {
		p5.hint(p5.DISABLE_DEPTH_TEST);
		p5.cam.beginHUD();
		cp5.draw();
		p5.cam.endHUD();
		p5.hint(p5.ENABLE_DEPTH_TEST);
		// updateButtons();
		updateButtonsSimple();
	}

	void createButtonsSimple() {

		// CColor c = #0xff660000;
		// // println(c); // Prints "-13312"
		// // println(hex(c)); // Prints "FFFFCC00"
		// // println(hex(c, 6)); // Prints "FFCC00"
		// String mag = p5.hex(#f600ff);

		size2dSlider = 100;
		sizeZSlider = size2dSlider;
		buttonDefWidth = size2dSlider + offsetSliders - 50 + size2dSlider / 10;
		// ////////////////////////////////////
		bFaces = cp5.addToggle("FACEMODE").setPosition(margin, margin)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_SelFace_A.png"), p5.loadImage("icons/SoftModelling_Icon_SelFace_B.png"), p5.loadImage("icons/SoftModelling_Icon_SelFace_B.png"))
				.updateSize().setValue(false);
		bEdges = cp5.addToggle("EDGEMODE").setPosition(margin, margin + sizeIcons + space)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_SelEdge_A.png"), p5.loadImage("icons/SoftModelling_Icon_SelEdge_B.png"), p5.loadImage("icons/SoftModelling_Icon_SelEdge_B.png"))
				.updateSize().setValue(false);
		bVertex = cp5
				.addToggle("VERTEXMODE")
				.setPosition(margin, margin + sizeIcons * 2 + space * 2)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_SelVertex_A.png"), p5.loadImage("icons/SoftModelling_Icon_SelVertex_B.png"), p5.loadImage("icons/SoftModelling_Icon_SelVertex_B.png"))
				.updateSize().setValue(false);
		// ////////////////////////////////////
		gravityOn = cp5.addToggle("UPDATE_PHYSICS").setPosition(margin, p5.height - margin - sizeIcons)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Gravity_A.png"), p5.loadImage("icons/SoftModelling_Icon_Gravity_B.png"), p5.loadImage("icons/SoftModelling_Icon_Gravity_B.png"))
				.updateSize().setValue(false);
		bc1 = cp5.addButton("circle1").setPosition(margin + sizeIcons + space, p5.height - margin - sizeIcons)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Gravity_A.png"), p5.loadImage("icons/SoftModelling_Icon_Gravity_B.png"), p5.loadImage("icons/SoftModelling_Icon_Gravity_B.png"))
				.updateSize();
		gravityKnob = cp5.addKnob("GRAVITY").setValue(9.80f).setRange(-100f, 100f).setPosition(margin + sizeIcons + space + 3, p5.height - margin - sizeIcons + 3).setRadius(sizeIcons / 2 - 3)
				.setResolution(100f).setColorBackground(-16777216).setColorForeground(-1).setColorActive(-65281);
		// ////////////////////////////////////
		
		// cp5.addButton("GROW").setPosition(10, 30).setSize(47,
		// 9).setGroup(g1);
		// cp5.addButton("SHRINK").setPosition(62, 30).setSize(47,
		// 9).setGroup(g1);
		// cp5.addButton("SELECT_ALL").setPosition(10,
		// 45).setWidth(buttonDefWidth - 20).setId(9).setGroup(g1);
		
		bSaveFile = cp5.addButton("EXPORT_OBJ").setPosition(p5.width-sizeIcons-margin, p5.height - margin - sizeIcons)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_SaveFile_A.png"), p5.loadImage("icons/SoftModelling_Icon_SaveFile_B.png"), p5.loadImage("icons/SoftModelling_Icon_SaveFile_B.png"))
				.updateSize();
		
		bGrow = cp5.addButton("GROW").setPosition(margin + sizeIcons + space, margin)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Grow_A.png"), p5.loadImage("icons/SoftModelling_Icon_Grow_B.png"), p5.loadImage("icons/SoftModelling_Icon_Grow_B.png")).updateSize();
		bShrink = cp5.addButton("SHRINK").setPosition(margin + sizeIcons * 2 + space * 2, margin)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Shrink_A.png"), p5.loadImage("icons/SoftModelling_Icon_Shrink_B.png"), p5.loadImage("icons/SoftModelling_Icon_Shrink_B.png"))
				.updateSize();
		bDeselect = cp5.addButton("DESELECT").setPosition(p5.width - margin - sizeIcons * 1, margin)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Deselect_A.png"), p5.loadImage("icons/SoftModelling_Icon_Deselect_B.png"), p5.loadImage("icons/SoftModelling_Icon_Deselect_B.png"))
				.updateSize();

		// ////////////////////////////////////
		// bMove = cp5.addToggle("ACTIVATE_MOVE").setPosition(margin + sizeIcons
		// + space, margin + sizeIcons+space)
		// .setImages(p5.loadImage("SoftModelling_Icon_Move_A.png"),
		// p5.loadImage("SoftModelling_Icon_Move_B.png"),
		// p5.loadImage("SoftModelling_Icon_Move_B.png")).updateSize()
		// .setValue(false);
		bMove = cp5.addButton("ACTIVATE_MOVE").setPosition(margin + sizeIcons + space, margin + sizeIcons + space)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Move_A.png"), p5.loadImage("icons/SoftModelling_Icon_Move_B.png"), p5.loadImage("icons/SoftModelling_Icon_Move_B.png")).updateSize();

		bMoveUp = cp5.addButton("MOVE_UP").setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + sizeIcons + space)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowUp_A.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowUp_B.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowUp_B.png"))
				.updateSize();

		bMoveDown = cp5
				.addButton("MOVE_DOWN")
				.setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 + 4)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowDown_A.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowDown_B.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowDown_B.png"))
				.updateSize();

		bMoveLeft = cp5.addButton("MOVE_LEFT").setPosition(margin + sizeIcons * 2 + space * 2 - 45 - 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 - 45 / 2)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowLeft_A.png"), p5.loadImage("icons/SoftModelling_ArrowLeft_B.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowLeft_B.png"))
				.updateSize();

		bMoveRight = cp5
				.addButton("MOVE_RIGHT")
				.setPosition(margin + sizeIcons * 2 + space * 2 + 45 + 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 - 45 / 2)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowRight_A.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowRight_B.png"),
						p5.loadImage("icons/SoftModelling_Icon_ArrowRight_B.png")).updateSize();

		bMoveZUp = cp5.addButton("MOVE_UPZ").setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + sizeIcons + space)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowUp_A.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowUp_B.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowUp_B.png"))
				.updateSize();

		bMoveZDown = cp5
				.addButton("MOVE_DOWNZ")
				.setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 + 4)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_ArrowDown_A.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowDown_B.png"), p5.loadImage("icons/SoftModelling_Icon_ArrowDown_B.png"))
				.updateSize();

		// ////////////////////////////////////
		bSubdivide = cp5
				.addButton("SUBDIVIDE_RUN")
				.setPosition(margin + sizeIcons + space, margin + sizeIcons * 2 + space * 2)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Subdivide2_A.png"), p5.loadImage("icons/SoftModelling_Icon_Subdivide2_B.png"),
						p5.loadImage("icons/SoftModelling_Icon_Subdivide2_B.png")).updateSize();

		// ////////////////////////////////////
		bLock = cp5.addButton("LOCK_ELEMENT").setPosition(margin + sizeIcons + space, margin + sizeIcons * 3 + space * 3)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Lock_A.png"), p5.loadImage("icons/SoftModelling_Icon_Lock_B.png"), p5.loadImage("icons/SoftModelling_Icon_Lock_B.png")).updateSize();
		bUnlock = cp5.addButton("UNLOCK_ELEMENT").setPosition(margin + sizeIcons * 2 + space * 2, margin + sizeIcons * 3 + space * 3)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_UnLock_A.png"), p5.loadImage("icons/SoftModelling_Icon_UnLock_B.png"), p5.loadImage("icons/SoftModelling_Icon_UnLock_B.png"))
				.updateSize();

		// ////////////////////////////////////
		bExtrude = cp5.addButton("EXTRUDE_RUN").setPosition(margin + sizeIcons + space, margin + sizeIcons * 4 + space * 4)
				.setImages(p5.loadImage("icons/SoftModelling_Icon_Extrude_A.png"), p5.loadImage("icons/SoftModelling_Icon_Extrude_B.png"), p5.loadImage("icons/SoftModelling_Icon_Extrude_B.png"))
				.updateSize();
		bc2 = cp5.addButton("circle2").setPosition(margin + sizeIcons * 2 + space * 2, margin + sizeIcons * 4 + space * 4)
				.setImages(p5.loadImage("icons/SoftModelling_smallCircle76_A.png"), p5.loadImage("icons/SoftModelling_smallCircle76_B.png"), p5.loadImage("icons/SoftModelling_smallCircle76_B.png"))
				.updateSize();
		extrudeDistKnob = cp5.addKnob("EXTRUSION_DISTANCE").setValue(.2f).setRange(-100, 100).setValue(30).setPosition(margin + sizeIcons * 2 + space * 2 + 3, margin + 3 + sizeIcons * 4 + space * 4)
				.setRadius(38 - 3).setResolution(100f).setColorBackground(-16777216).setColorForeground(-1).setColorActive(-65281).setLabel("dist");
		bc3 = cp5.addButton("circle3").setPosition(margin + sizeIcons * 2 + space * 3 + 75, margin + sizeIcons * 4 + space * 4)
				.setImages(p5.loadImage("icons/SoftModelling_smallCircle76_A.png"), p5.loadImage("icons/SoftModelling_smallCircle76_B.png"), p5.loadImage("icons/SoftModelling_smallCircle76_B.png"))
				.updateSize();
		extrudeChanferKnob = cp5.addKnob("EXTRUSION_CHANFER").setValue(.2f).setRange(0f, 1).setPosition(margin + sizeIcons * 2 + space * 3 + 75 + 3, margin + 3 + sizeIcons * 4 + space * 4)
				.setRadius(38 - 3).setResolution(100f).setColorBackground(-16777216).setColorForeground(-1).setColorActive(-65281).setLabel("chanfer");
		// ////////////////////////////////////


		cp5.setAutoDraw(false);
	}

	void updateSelector() {
		if (p5.selectionMode == 2) bFaces.setValue(true);
		if (p5.selectionMode == 1) bEdges.setValue(true);
		if (p5.selectionMode == 0) bVertex.setValue(true);

		// if (bFaces.getState()){
		// if (bEdges.getState())bEdges.setState(false);
		// if (bVertex.getState())bVertex.setState(false);
		// p5.selectionMode = 2;
		// p5.println("selectionMode"+p5.selectionMode);
		// }
		// if (bEdges.getState()){
		// if (bVertex.getState())bVertex.setState(false);
		// if (bFaces.getState())bFaces.setState(false);
		// p5.selectionMode = 1;
		// p5.println("selectionMode"+p5.selectionMode);
		// }
		// if (bVertex.getState()){
		// if (bFaces.getState())bFaces.setState(false);
		// if (bEdges.getState())bEdges.setState(false);
		// p5.selectionMode = 0;
		// p5.println("selectionMode"+p5.selectionMode);
		// }

	}

	void updateMover() {

		if (bMoveUp.isMousePressed()) {
			// bMoveUp.setImage(this.iconMoveUpB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.y -= 1;
				}
			}
		}

		if (bMoveDown.isMousePressed()) {
			// bMoveDown.setImage(this.iconMoveDownB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.y += 1;
				}
			}
		}
		if (bMoveLeft.isMousePressed()) {
			// bMoveDown.setImage(this.iconMoveDownB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.x -= 1;
				}
			}
		}
		if (bMoveRight.isMousePressed()) {
			// bMoveDown.setImage(this.iconMoveDownB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.x += 1;
				}
			}
		}

		if (bMoveZUp.isMousePressed()) {
			// bMoveUp.setImage(this.iconMoveUpB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.z += 1;
				}
			}
		}

		if (bMoveZDown.isMousePressed()) {
			// bMoveDown.setImage(this.iconMoveDownB);
			for (int i = 0; i < p5.surface.particles.size(); i++) {
				Particle p = (Particle) p5.surface.particles.get(i);
				if (p.isSelected) {
					p.lock();
					p.z -= 1;
				}
			}
		}
	}

	void reArrangebuttons() {

	}

	void updateButtonsSimple() {

		updateMover();
		reArrangebuttons();

		if (p5.activeMover) {
			bMove.setImage(p5.loadImage("icons/SoftModelling_Icon_Move_B.png"));
			bMoveUp.show();
			bMoveDown.show();
			bMoveLeft.show();
			bMoveRight.show();
			bMoveZUp.show();
			bMoveZDown.show();

		} else {
			bMove.setImage(p5.loadImage("icons/SoftModelling_Icon_Move_A.png"));
			bMoveUp.hide();
			bMoveDown.hide();
			bMoveLeft.hide();
			bMoveRight.hide();
			bMoveZUp.hide();
			bMoveZDown.hide();
		}

		if (bFaces.isActive() || (bFaces.getState()  || (bFaces.isMouseOver( )))) {
			bMove.setPosition(margin + sizeIcons + space, margin + sizeIcons + space);
			bLock.setPosition(margin + sizeIcons + space, margin + sizeIcons * 3 + space * 3);
			bUnlock.setPosition(margin + sizeIcons * 2 + space * 2, margin + sizeIcons * 3 + space * 3);
			
			bMoveUp.setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + sizeIcons + space);
			bMoveDown.setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 + 4);
			bMoveLeft.setPosition(margin + sizeIcons * 2 + space * 2 - 45 - 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 - 45 / 2);
			bMoveRight.setPosition(margin + sizeIcons * 2 + space * 2 + 45 + 2 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 - 45 / 2);
			bMoveZUp.setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + sizeIcons + space);
			bMoveZDown.setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + sizeIcons + space + sizeIcons / 2 + 4);
			
			bGrow.show();
			bShrink.show();
			bSubdivide.show();
			bExtrude.show();
			bc2.show();
			bc3.show();
			extrudeDistKnob.show();
			extrudeChanferKnob.show();

			bMove.show();
			bLock.show();
			bUnlock.show();

		} else {
			bGrow.hide();
			bShrink.hide();
			bSubdivide.hide();
			bExtrude.hide();
			bc2.hide();
			bc3.hide();
			extrudeDistKnob.hide();
			extrudeChanferKnob.hide();
			if ((!bEdges.isActive() && (!bFaces.getState())) && (!bVertex.isActive() && (!bVertex.getState()))) {
				bMove.hide();
				bLock.hide();
				bUnlock.hide();
				// test
			}

		}

		if ((!bEdges.isActive()) && (!bVertex.isActive() && (!bFaces.isActive()))) {

		}

		if ((bEdges.isActive() || (bEdges.getState())) || (bVertex.isActive() || (bVertex.getState())) || ((bEdges.isMouseOver() || (bVertex.isMouseOver() )))){
			if ((!bFaces.isMouseOver( ))){
			bMove.setPosition(margin + sizeIcons + space, margin);
			bLock.setPosition(margin + sizeIcons + space, margin + sizeIcons * 1 + space * 1);
			bUnlock.setPosition(margin + sizeIcons * 2 + space * 2, margin + sizeIcons * 1 + space * 1);
			
			bMoveUp.setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + 0 + 0);
			bMoveDown.setPosition(margin + sizeIcons * 2 + space * 2 + sizeIcons / 2, margin + 0 + 0 + sizeIcons / 2 + 4);
			bMoveLeft.setPosition(margin + sizeIcons * 2 + space * 2 - 45 - 2 + sizeIcons / 2, margin + 0 + 0 + sizeIcons / 2 - 45 / 2);
			bMoveRight.setPosition(margin + sizeIcons * 2 + space * 2 + 45 + 2 + sizeIcons / 2, margin + 0 + 0 + sizeIcons / 2 - 45 / 2);
			bMoveZUp.setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + 0 + 0);
			bMoveZDown.setPosition(margin + sizeIcons * 3 + space * 3 + sizeIcons / 2, margin + 0 + 0 + sizeIcons / 2 + 4);
			
			bMove.show();
			bLock.show();
			bUnlock.show();
			
			bGrow.hide();
			bShrink.hide();
			bSubdivide.hide();
			bExtrude.hide();
			
			extrudeDistKnob.hide();
			extrudeChanferKnob.hide();
			bc2.hide();
			bc3.hide();
			
			}
		}

	}

	void createButtons() {

		size2dSlider = 100;
		sizeZSlider = size2dSlider;
		buttonDefWidth = size2dSlider + offsetSliders - 50 + size2dSlider / 10;

		float rad = 30;
		spl = cp5.addKnob("SP_LENGTH").setValue(100f).setRange(0f, 200f).setPosition(50, 50).setRadius(rad).setResolution(100f);
		cp5.addKnob("GRAVITY").setValue(9.80f).setRange(-100f, 100f).setPosition(50 + 35 + rad, 50).setRadius(rad).setResolution(100f);

		slider2d = cp5.addSlider2D("PARTICLE-XY").setPosition(50, 160).setSize(size2dSlider, size2dSlider).setArrayValue(new float[]{size2dSlider / 2, size2dSlider / 2});// .disableCrosshair();
		sliderZ = cp5.addSlider2D("PARTICLE-Z").setPosition(offsetSliders + size2dSlider, 160).setSize(size2dSlider / 10, size2dSlider).setArrayValue(new float[]{size2dSlider / 2, size2dSlider / 2})
				.setLabelVisible(false).setLabel("");// .disableCrosshair();

		b1 = cp5.addButton("REMOVE_ELEMENT", 10, 50, 310 + size2dSlider, (int) buttonDefWidth * 2 / 3 - 2, 20).setId(7);
		t1 = cp5.addToggle("KILL_SPRINGS").setPosition(50 + buttonDefWidth * 2 / 3, 310 + size2dSlider).setSize(buttonDefWidth / 3, 20).setValue(true).setMode(ControlP5.SWITCH).setLabelVisible(false);
		b2 = cp5.addButton("LOCK_ELEMENT", 10, 50, 340 + size2dSlider, 120 / 2 - 3, 20).setId(8).setLabel("lock");
		b3 = cp5.addButton("UNLOCK_ELEMENT", 10, 114, 340 + size2dSlider, 120 / 2 - 3, 20).setId(6).setLabel("unlock");

		// /---Group selection----////
		g1 = cp5.addGroup("SELECTION_OPTIONS").setPosition(50, 300).setWidth(buttonDefWidth).activateEvent(true).setBackgroundColor(p5.color(255, 80)).setBackgroundHeight(100).setLabel("selection");
		MultiList l = cp5.addMultiList("myList", 10, 10, buttonDefWidth, 12).setWidth(buttonDefWidth - 20).setGroup(g1);
		MultiListButton b = l.add("selectionMode", 1);
		// add items to a sublist of button "SUBDIVIDE_KEEPEDGES1"
		b.add("Vertex", 0).setLabel("Vertex mode");
		b.add("Edge", 1).setLabel("Edge mode");
		b.add("Face", 2).setLabel("Face mode");
		cp5.addButton("GROW").setPosition(10, 30).setSize(47, 9).setGroup(g1);
		cp5.addButton("SHRINK").setPosition(62, 30).setSize(47, 9).setGroup(g1);
		cp5.addButton("SELECT_ALL").setPosition(10, 45).setWidth(buttonDefWidth - 20).setId(9).setGroup(g1);
		cp5.addButton("DESELECT").setPosition(10, 70).setWidth(buttonDefWidth - 20).setId(9).setGroup(g1);

		// /---Group subdivision----////
		g2 = cp5.addGroup("SUBDIVIDE_OPTIONS").setPosition(50, 480).setWidth(buttonDefWidth).activateEvent(true).setBackgroundColor(p5.color(255, 80)).setBackgroundHeight(160).setLabel("subdivide");
		radB = cp5.addRadioButton("SUBDIVIDE_RADIO").setPosition(10, 10).setSize(20, 9).addItem("CatmullClark", 0).addItem("DooSabin", 1).addItem("Planar", 2).addItem("PlanarMidEdge", 3)
				.addItem("Smooth", 4).addItem("Tridec", 5).setGroup(g2);
		cp5.addSlider("SUBDIVIDE_LEVEL").setPosition(10, 110).setSize(73, 9).setGroup(g2).show().setRange(1, 4).setDecimalPrecision(0).setNumberOfTickMarks(4).setLabel("level")
				.setSliderMode(Slider.FLEXIBLE);
		cp5.addToggle("SUBDIVIDE_KEEPEDGES").setPosition(10, 80).setSize(47, 9).setValue(true).setMode(ControlP5.SWITCH).setLabel("keepEdges").setGroup(g2);
		cp5.addToggle("SUBDIVIDE_KEEPBOUND").setPosition(62, 80).setSize(47, 9).setValue(true).setMode(ControlP5.SWITCH).setLabel("keepBound").setGroup(g2);
		cp5.addButton("SUBDIVIDE_RUN").setPosition(10, 132).setWidth(buttonDefWidth - 20).setId(9).setLabel("subdivide").setGroup(g2);

		// /---Group extrusion----////
		g3 = cp5.addGroup("ENTRUDE_OPTIONS").setPosition(50, 660).setWidth(buttonDefWidth).activateEvent(true).setBackgroundColor(p5.color(255, 80)).setBackgroundHeight(100).setLabel("extrude");
		cp5.addSlider("EXTRUSION_CHANFER").setPosition(10, 10).setSize(60, 9).setRange(0, 1).setLabel("chanfer").setGroup(g3);
		cp5.addSlider("EXTRUSION_DISTANCE").setPosition(10, 20).setSize(60, 9).setRange(-100, 100).setValue(0).setNumberOfTickMarks(3).snapToTickMarks(false).setSliderMode(Slider.FLEXIBLE)
				.setLabel("distance").setGroup(g3);
		cp5.addToggle("EXTRUDE_SELECTNEIGHBOUR").setPosition(10, 45).setSize(47, 9).setValue(false).setMode(ControlP5.SWITCH).setGroup(g3).setLabel("selNeigh.");
		cp5.addToggle("EXTRUDE_LOCKPARTICLES").setPosition(62, 45).setSize(47, 9).setValue(false).setMode(ControlP5.SWITCH).setGroup(g3).setLabel("lockPart.");
		cp5.addButton("EXTRUDE_RUN").setPosition(10, 70).setWidth(buttonDefWidth - 20).setId(9).setLabel("extrude").setGroup(g3);

		// /---Group lattice----////
		g4 = cp5.addGroup("LATTICE_GROUP").setPosition(50, 780).setWidth(buttonDefWidth).activateEvent(true).setBackgroundColor(p5.color(255, 80)).setBackgroundHeight(110).setLabel("lattice");
		cp5.addSlider("LATTICE_DEPTH").setPosition(10, 20).setSize(60, 9).setRange(-100, 100).setValue(0).setNumberOfTickMarks(3).snapToTickMarks(false).setSliderMode(Slider.FLEXIBLE)
				.setLabel("distance").setGroup(g4);
		cp5.addSlider("LATTICE_WIDTH").setPosition(10, 10).setSize(60, 9).setRange(0, 150).setLabel("width").setGroup(g4);
		cp5.addToggle("LATTICE_SELOLD").setPosition(10, 45).setSize(30, 9).setValue(false).setMode(ControlP5.SWITCH).setGroup(g4).setLabel("s_old");
		cp5.addToggle("LATTICE_SELNEW").setPosition(45, 45).setSize(30, 9).setValue(false).setMode(ControlP5.SWITCH).setGroup(g4).setLabel("s_new");
		cp5.addToggle("LATTICE_LOCKPARTICLES").setPosition(80, 45).setSize(30, 9).setValue(false).setMode(ControlP5.SWITCH).setGroup(g4).setLabel("lock");
		cp5.addButton("LATTICE_RUN").setPosition(10, 70).setWidth(buttonDefWidth - 20).setId(9).setLabel("lattice").setGroup(g4);

		// /---Group export----////180
		g5 = cp5.addGroup("EXPORT_OPTIONS").setPosition(50, this.p5.height - 150).setWidth(buttonDefWidth).activateEvent(true).setBackgroundColor(p5.color(255, 80)).setBackgroundHeight(40);
		cp5.addButton("EXPORT_STL").setPosition(10, 10).setSize(47, 20).setLabel("STL").setGroup(g5);
		cp5.addButton("EXPORT_OBJ").setPosition(62, 10).setSize(47, 20).setLabel("OBJ").setGroup(g5);

		cp5.addToggle("UPDATE_PHYSICS").setPosition(50, p5.height - 100).setSize(buttonDefWidth, 20).setValue(false).setMode(ControlP5.SWITCH);
		cp5.addButton("RESET", 10, 50, p5.height - 50, buttonDefWidth, 20).setId(3);

		cp5.addToggle("DISPLAY_SELECTORS").setPosition(p5.width - 120, p5.height - 150 - 20).setSize(80, 20).setValue(true).setMode(ControlP5.SWITCH);
		cp5.addToggle("DISPLAY_KEY").setPosition(p5.width - 120, p5.height - 100 - 20).setSize(80, 20).setValue(false).setMode(ControlP5.SWITCH);
		cp5.addToggle("DISPLAY_MESH").setPosition(p5.width - 120, p5.height - 50 - 20).setSize(80, 20).setValue(true).setMode(ControlP5.SWITCH);

		cp5.setAutoDraw(false);
	}

	void updateButtons() {
		if (g1.isOpen()) {
			b1.setPosition(50, 310 + size2dSlider);
			t1.setPosition(50 + buttonDefWidth * 2 / 3, 310 + size2dSlider);
			b2.setPosition(50, 340 + size2dSlider);
			b3.setPosition(114, 340 + size2dSlider);// /114
			g2.setPosition(50, 480);
			if (g2.isOpen()) g3.setPosition(50, 660);
			else g3.setPosition(50, 660 - g2.getBackgroundHeight());
			if ((g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780);
			if ((g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g3.getBackgroundHeight());
			if ((!g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight());
			if ((!g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight() - g3.getBackgroundHeight());
		} else {
			b1.setPosition(50, 310 + size2dSlider - g1.getBackgroundHeight());
			t1.setPosition(50 + buttonDefWidth * 2 / 3, 310 + size2dSlider - g1.getBackgroundHeight());
			b2.setPosition(50, 340 + size2dSlider - g1.getBackgroundHeight());
			b3.setPosition(114, 340 + size2dSlider - g1.getBackgroundHeight());// /114
			g2.setPosition(50, 480 - g1.getBackgroundHeight());
			if (g2.isOpen()) g3.setPosition(50, 660 - g1.getBackgroundHeight());
			else g3.setPosition(50, 660 - g1.getBackgroundHeight() - g2.getBackgroundHeight());
			if ((g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780 - g1.getBackgroundHeight());
			if ((g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g3.getBackgroundHeight() - g1.getBackgroundHeight());
			if ((!g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight() - g1.getBackgroundHeight());
			if ((!g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight() - g3.getBackgroundHeight() - g1.getBackgroundHeight());
		}
	}

}// endClass