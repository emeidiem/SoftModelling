package softmodelling;

import toxi.geom.Vec3D;
import toxi.physics.behaviors.GravityBehavior;
import wblut.hemesh.HET_Export;
import controlP5.Button;
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
	Button b1, b2, b3, b4;
	Toggle t1;
	Slider2D slider2d;
	Slider2D sliderZ;
	Knob spl;
	RadioButton radB;

	// ////////////////CONSTRUCTOR
	Gui(SoftModelling _p5) {
		p5 = _p5;
		cp5 = new ControlP5(p5);

		size2dSlider = 100;
		sizeZSlider = size2dSlider;
		buttonDefWidth = size2dSlider + offsetSliders - 50 + size2dSlider / 10;
		createButtons();
	}
	// /////////////////////////

	void run() {
		p5.hint(p5.DISABLE_DEPTH_TEST);
		p5.cam.beginHUD();
		cp5.draw();
		p5.cam.endHUD();
		p5.hint(p5.ENABLE_DEPTH_TEST);
		updateButtons();
	}


void createButtons() {

  size2dSlider = 100;
  sizeZSlider = size2dSlider;
  buttonDefWidth = size2dSlider + offsetSliders - 50 + size2dSlider / 10;

  float rad = 30;
  spl = cp5.addKnob("SP_LENGTH").setValue(100f).setRange(0f, 200f).setPosition(50, 50).setRadius(rad).setResolution(100f);
  cp5.addKnob("GRAVITY").setValue(9.80f).setRange(-100f, 100f).setPosition(50 + 35 + rad, 50).setRadius(rad).setResolution(100f);

  slider2d = cp5.addSlider2D("PARTICLE-XY").setPosition(50, 160).setSize(size2dSlider, size2dSlider).setArrayValue(new float[] {
    size2dSlider / 2, size2dSlider / 2
  }
  );// .disableCrosshair();
  sliderZ = cp5.addSlider2D("PARTICLE-Z").setPosition(offsetSliders + size2dSlider, 160).setSize(size2dSlider / 10, size2dSlider).setArrayValue(new float[] {
    size2dSlider / 2, size2dSlider / 2
  }
  )
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
    b3.setPosition(114, 340 + size2dSlider);///114
    g2.setPosition(50, 480);
    if (g2.isOpen()) g3.setPosition(50, 660);
    else g3.setPosition(50, 660 - g2.getBackgroundHeight());
    if ((g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780);
    if ((g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g3.getBackgroundHeight());
    if ((!g2.isOpen()) && (g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight());
    if ((!g2.isOpen()) && (!g3.isOpen())) g4.setPosition(50, 780 - g2.getBackgroundHeight() - g3.getBackgroundHeight());
  } 
  else {
    b1.setPosition(50, 310 + size2dSlider - g1.getBackgroundHeight());
    t1.setPosition(50 + buttonDefWidth * 2 / 3, 310 + size2dSlider - g1.getBackgroundHeight());
    b2.setPosition(50, 340 + size2dSlider - g1.getBackgroundHeight());
    b3.setPosition(114, 340 + size2dSlider - g1.getBackgroundHeight());///114
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