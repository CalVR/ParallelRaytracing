package raytrace.surfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import math.Ray;
import math.Vector4;

import raytrace.bounding.BoundingBox;
import raytrace.data.IntersectionData;
import raytrace.data.RayData;
import raytrace.data.UpdateData;
import raytrace.framework.Composite;
import raytrace.framework.Node;
import raytrace.framework.Surface;
import raytrace.material.Material;

public abstract class CompositeSurface implements Node, Composite<CompositeSurface>, Surface
{
	/*
	 * A base class for nodes of a surface graph
	 */

	/* *********************************************************************************************
	 * Instance Vars
	 * *********************************************************************************************/
	protected ArrayList<CompositeSurface> children;
	protected Material material;
	protected BoundingBox boundingBox = new BoundingBox();
	

	/* *********************************************************************************************
	 * Surface-related Methods
	 * *********************************************************************************************/
	//No longer supported
	/*
	public TraceData trace(RayData data)
	{
		return trace(ray, 0, Double.POSITIVE_INFINITY);
	}
	*/

	@Override
	public IntersectionData intersects(RayData data)
	{
		//TOOD: check against bounding box first
		
		IntersectionData idata;
		IntersectionData closest = null;
		
		for(CompositeSurface cs : this)
		{
			idata = cs.intersects(data);
			//If idata isn't null, and either closest is null, or idata is closer than closest
			if(idata != null && (closest == null || idata.getDistance() < closest.getDistance()))
			{
				closest = idata;
			}
		}
		
		return closest;
	}
	
	@Override
	public void update(UpdateData data)
	{
		//Update all children
		for(CompositeSurface cs : this)
		{
			cs.update(data);
		}
	}
	
	@Override
	public void updateBoundingBox()
	{
		//Clear the current bounding box
		boundingBox.clear();
		
		//Temp Storage
		Vector4 min;
		Vector4 max;
		BoundingBox bb;
		
		//Loop through all children bounding boxes and set this to bound them
		for(CompositeSurface cs : this)
		{
			cs.updateBoundingBox();
			bb = cs.getBoundingBox();
			
			min = bb.min;
			max = bb.max;

			boundingBox.min.minimize3(min);
			boundingBox.max.maximize3(max);
		}
	}
	
	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}

	/* *********************************************************************************************
	 * Composite-related Methods
	 * *********************************************************************************************/
	@Override
	public Iterator<CompositeSurface> iterator()
	{
		if(children == null)
			return null;
		return children.iterator();
	}
	
	public void addChild(CompositeSurface cs)
	{
		//By default children is not initialized so that leaf nodes don't incur the memory penalty of empty structures
		if(children == null)
			children = new ArrayList<CompositeSurface>();
		
		//Having the same surface added twice uses unnecessary resources.
		//This contains method is slow
		if(cs != null && !children.contains(cs))
			children.add(cs);
	}
	
	public void addChildren(Collection<CompositeSurface> set)
	{	
		//Add each child, one by one
		for(CompositeSurface cs : set)
			addChild(cs);
	}
	
	public void addChildrenUnsafe(Collection<CompositeSurface> set)
	{	
		//Add all children at once
		//This does not check for null or duplicate surfaces
		if(children != null)
			children.addAll(set);
	}
	
	public CompositeSurface removeChild(CompositeSurface cs)
	{
		//By default children is not initialized so that leaf nodes don't incur the memory penalty of empty structures
		if(children == null) {
			children = new ArrayList<CompositeSurface>();
			return null;
		}
		
		//Return the item removed only if it actually existed, else return null
		if(children.remove(cs))
			return cs;
		return null;
	}
	
	public ArrayList<CompositeSurface> getChildren()
	{
		return children;
	}
	

	/* *********************************************************************************************
	 * Getters/Setters
	 * *********************************************************************************************/
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}
