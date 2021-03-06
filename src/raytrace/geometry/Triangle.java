package raytrace.geometry;

import java.util.ArrayList;

import math.Vector3;
import math.ray.Ray;
import raytrace.bounding.BoundingBox;
import raytrace.data.BakeData;
import raytrace.data.IntersectionData;
import raytrace.data.RayData;
import raytrace.surfaces.GeometrySurface;

public class Triangle extends GeometrySurface {

	/*
	 * A simple triangle surface
	 */
	
	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected Vertex[] vertices;
	

	/* *********************************************************************************************
	 * Cosntructors
	 * *********************************************************************************************/
	public Triangle()
	{
		vertices = new Vertex[3];
	}
	
	public Triangle(Vertex v0, Vertex v1, Vertex v2)
	{
		vertices = new Vertex[3];
		vertices[0] = v0;
		vertices[1] = v1;
		vertices[2] = v2;
	}
	

	/* *********************************************************************************************
	 * Surface Methods
	 * *********************************************************************************************/
	/**
	 * 
	 */
	public IntersectionData intersects(RayData data)
	{
		Ray ray = data.getRay();
		
		double[] d = ray.getDirection().getArray();

		double[] a = vertices[0].getPosition().getArray();
		double[] b = vertices[1].getPosition().getArray();
		double[] c = vertices[2].getPosition().getArray();

		double va = b[0] - a[0];
		double vb = b[1] - a[1];
		double vc = b[2] - a[2];
		double vd = c[0] - a[0];
		double ve = c[1] - a[1];
		double vf = c[2] - a[2];
		double vg = d[0];
		double vh = d[1];
		double vi = d[2];
		
		double ei_hf = ve * vi - vh * vf;
		double gf_di = vg * vf - vd * vi;
		double dh_eg = vd * vh - ve * vg;
		
		double m = va * ei_hf + vb * gf_di + vc * dh_eg;
		
		//If the determinant m is 0, then the ray is parallel
		if(m == 0.0 || m != m)
			return null;

		
		double[] e = ray.getOrigin().getArray();
		
		double vj = e[0] - a[0];
		double vk = e[1] - a[1];
		double vl = e[2] - a[2];
		
		double ak_jb = va * vk - vj * vb;
		double jc_al = vj * vc - va * vl;
		double bl_kc = vb * vl - vk * vc;
		
		
		double t = (vf * ak_jb + ve * jc_al + vd * bl_kc) / m;
		double t0 = data.getTStart();
		double t1 = data.getTEnd();

		//Test if t is in the given time range
		if(t <= t0 || t > t1)
			return null;
		
		double gamma = (vi * ak_jb + vh * jc_al + vg * bl_kc) / m;
		
		//If the gamma coord is not in the triangle, no intersection
		if(gamma < 0 || gamma > 1)
			return null;
		
		double beta = (vj * ei_hf + vk * gf_di + vl * dh_eg) / m;

		//If beta is outside of the triangle, or exceeds the allowable difference of 1 and gamma, no intersection
		if(beta < 0 || beta > 1 - gamma)
			return null;
		
		
		//Interpolate the normals
		double[] n0 = vertices[0].getNormal().getArray();
		double[] n1 = vertices[1].getNormal().getArray();
		double[] n2 = vertices[2].getNormal().getArray();
		
		double alpha = 1.0 - (gamma + beta);
		
		double nx = n0[0] * alpha + n1[0] * beta + n2[0] * gamma;
		double ny = n0[1] * alpha + n1[1] * beta + n2[1] * gamma;
		double nz = n0[2] * alpha + n1[2] * beta + n2[2] * gamma;
		
		Vector3 normal = new Vector3(nx, ny, nz);
		normal.normalizeM();
		
		
		//Point
		Vector3 point = ray.evaluateAtTime(t);
		
		
		//Interpolate the TexCoords
		double[] tex0 = vertices[0].getTexCoord().getArray();
		double[] tex1 = vertices[1].getTexCoord().getArray();
		double[] tex2 = vertices[2].getTexCoord().getArray();
		
		double uCoord = tex0[0] * alpha + tex1[0] * beta + tex2[0] * gamma;
		double vCoord = tex0[1] * alpha + tex1[1] * beta + tex2[1] * gamma;
		
		Vector3 texcoord = new Vector3(uCoord, vCoord, 0);
		
		
		//Return data about the intersection
		IntersectionData idata = new IntersectionData();
		idata.setTime(t);
		idata.setRay(ray);
		idata.setPoint(point);
		idata.setDistance(ray.getDirection().magnitude() * t);
		idata.setNormal(normal);
		idata.setMaterial(material);

		idata.setSurface(this);
		idata.setTexcoord(texcoord);
		idata.setLocalPoint(new Vector3(point));
		idata.setSurfaceID(surfaceID);
		
		return idata;
	}
	
	/**
	 * 
	 */
	public void bake(BakeData data)
	{
		//TODO: Bake
		//normal = vertices[0].subtract3(vertices[1]).cross3(vertices[2].subtract3(vertices[1])).normalize3();
	}
	
	@Override
	public BoundingBox getBoundingBox()
	{
		BoundingBox boundingBox = new BoundingBox();
		boundingBox.min.minimizeM(vertices[0].getPosition()).minimizeM(vertices[1].getPosition()).minimizeM(vertices[2].getPosition());
		boundingBox.max.maximizeM(vertices[0].getPosition()).maximizeM(vertices[1].getPosition()).maximizeM(vertices[2].getPosition());
		return boundingBox;
	}
	
	public void generateFaceNormal()
	{
		Vector3 normal = vertices[0].position.subtract(vertices[1].position).cross(
				vertices[2].position.subtract(vertices[1].position)).normalizeM();
		for(Vertex v : vertices)
			v.normal = normal;
	}
	
	public ArrayList<Triangle> tessellateByAreaConstraint(double areaConstraint)
	{
		//If the triangle's area is larger than the constraint we have to tessellate it until its small enough
		if(getArea() <= areaConstraint)
		{
			ArrayList<Triangle> thisTriangle = new ArrayList<Triangle>(1);
			thisTriangle.add(this);
			return thisTriangle;
		}
		
		//Call split
		Triangle[] tessellated = splitDownLongestSide();

		//Recurse
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		triangles.addAll(tessellated[0].tessellateByAreaConstraint(areaConstraint));
		triangles.addAll(tessellated[1].tessellateByAreaConstraint(areaConstraint));
		
		return triangles;
	}
	
	public ArrayList<Triangle> tessellateByLongestSideConstraint(double lengthConstraint)
	{
		//If the triangle's area is larger than the constraint we have to tessellate it until its small enough
		if(getLongestSide() <= lengthConstraint)
		{
			ArrayList<Triangle> thisTriangle = new ArrayList<Triangle>(1);
			thisTriangle.add(this);
			return thisTriangle;
		}
		
		//Call split
		Triangle[] tessellated = splitDownLongestSide();

		//Recurse
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		triangles.addAll(tessellated[0].tessellateByLongestSideConstraint(lengthConstraint));
		triangles.addAll(tessellated[1].tessellateByLongestSideConstraint(lengthConstraint));
		
		return triangles;
	}
	
	protected Triangle[] splitDownLongestSide()
	{
		//Since the tringle is too large, tessellate recursively by splitting along the longest edge
		double d01 = vertices[0].position.distance(vertices[1].position);
		double d02 = vertices[0].position.distance(vertices[2].position);
		double d12 = vertices[1].position.distance(vertices[2].position);
		
		Triangle[] tessellated = null;
		if(d01 >= d02 && d01 >= d12)
		{
			//Edge 01 is the longest
			tessellated = makeTessellatedTriangles(vertices[0], vertices[2], vertices[1]);
			
		}else if(d02 >= d01 && d02 >= d12)
		{
			//Edge 02 is the longest
			tessellated = makeTessellatedTriangles(vertices[0], vertices[1], vertices[2]);
			
		}else{
			//Edge 12 is the longest
			tessellated = makeTessellatedTriangles(vertices[1], vertices[0], vertices[2]);
		}
		
		return tessellated;
	}
	
	protected Triangle[] makeTessellatedTriangles(Vertex left, Vertex center, Vertex right)
	{
		Vertex subL0, subS0, subC0, subR1, subS1, subC1;
		
		//Generate for sub vertices
		subL0 = left;
		
		subR1 = right;
		
		subC0 = center;
		
		subC1 = center.copy();
		
		subS0 = new Vertex(left.position.add(right.position.subtract(left.position).multiplyM(right.position.distance(left.position)/2.0)),
				left.normal.multiply(0.5).addM(right.normal.multiply(0.5)).normalizeM(),
				left.texCoord.multiply(0.5).addM(right.texCoord.multiply(0.5))
				);
		
		subS1 = subS0.copy();
		
		Triangle[] triangles = {
			new Triangle(subC0, subS0, subL0),
			new Triangle(subR1, subS1, subC1)
		};
		
		return triangles;
	}

	
	/* *********************************************************************************************
	 * Print Methods
	 * *********************************************************************************************/
	public void print()
	{
		System.out.println("Triangle[" + this.toString() + "]");
		for(Vertex v : vertices)
		{
			System.out.print("\tPosition: ");
			v.getPosition().print();
			System.out.print("\tNormal: ");
			v.getNormal().print();
			System.out.print("\tTexCoord: ");
			v.getTexCoord().print();
		}
	}
	

	/* *********************************************************************************************
	 * Getter/Setter Methods
	 * *********************************************************************************************/
	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}
	
	public Vertex getVertex(int element) {
		return vertices[element];
	}
	
	public void setVertex(int element, Vertex v) {
		vertices[element] = v;
	}
	
	public double getArea()
	{
		Vector3 a = vertices[0].getPosition().subtract(vertices[1].getPosition());
		Vector3 b = vertices[2].getPosition().subtract(vertices[1].getPosition());
		
		return a.cross(b).magnitude()/2.0;
	}
	
	public double getLongestSide()
	{
		double d01 = vertices[0].position.distance(vertices[1].position);
		double d02 = vertices[0].position.distance(vertices[2].position);
		double d12 = vertices[1].position.distance(vertices[2].position);
		
		return Math.max(d01, Math.max(d02, d12));
	}
	
}
