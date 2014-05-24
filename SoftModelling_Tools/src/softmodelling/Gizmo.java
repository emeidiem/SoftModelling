package softmodelling;

import java.util.ArrayList;

import toxi.geom.Vec3D;
import wblut.hemesh.HE_Vertex;

public class Gizmo {

	SoftModelling p5;
	Vec3D pos;
	boolean isSelected = false;

	// ////////////////CONSTRUCTOR
	Gizmo(SoftModelling _p5, Vec3D _pos) {
		p5 = _p5;
		pos = _pos;
	}
	// /////////////////////////

	void run() {
		render();
	}

	void render() {
		if (this.isSelected) {
			p5.strokeWeight(47);
			p5.stroke(255, 255, 0);
		} else {
			p5.strokeWeight(10);
			p5.stroke(150, 150);
		}
		p5.point(pos.x, pos.y, pos.z);
	}

	void calculateCentroidSelection() {
		ArrayList boxes = new ArrayList<BoxClass>();
		// if (p5.selectionMode == 0) boxes = p5.mesh.boxArrayVertices;
		// if (p5.selectionMode == 1) boxes = p5.mesh.boxArrayEdges;
		// if (p5.selectionMode == 2) boxes = p5.mesh.boxArrayFaces;

		boxes = p5.boxesSelected;
		Vec3D sum = new Vec3D();
		int numbBoxed = 0;

		for (int i = 0; i < boxes.size(); i++) {
			BoxClass bb = (BoxClass) boxes.get(i);
			// if (bb.isSelected) {
			sum.addSelf(bb.pos);
			numbBoxed++;
			// }
		}
		pos = new Vec3D(sum.x / numbBoxed, sum.y / numbBoxed, sum.z / numbBoxed);

	}

}
