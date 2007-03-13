//GameElementBranch.java
//@author Joel Ross

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A class that represents a GameElement as a branch of the Java3D tree.
 Contains references to stuff in the tree we may want to change so we can
 sync with the World.
***/

public class GameElementBranch implements ElementBranch //it doesn't like if we extend BranchGroup, so just make that a member variable and fetch it later
{
	//member variables - anything we'd want to change later (Element level)
	private TransformGroup coord; //transformed coordinates for this branch
	private Appearance appear; //an appearance node reference
	private BranchGroup broot; //the root of the branch.
	
	//constructor
	public GameElementBranch(GameElement e)
	{
		broot = new BranchGroup(); //the root of this branch
		broot.setCapability(BranchGroup.ALLOW_DETACH); //let us remove the branch at runtime

		Transform3D posi = new Transform3D(new Quat4f(e.getFacing()),new Vector3f(e.getPosition()),1); //make the new coordinate system
		coord = new TransformGroup(posi); //create the coordinate node
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime
		broot.addChild(coord); //add the transformation to the root.
		
		//setting an appearance so our objects look like objects.
		//This stuff should probably be taken directly from the Element.
		//Or adjust the appearance based on Shape inside the loop
		appear = new Appearance(); //will need to specify how to get this appearance from the Element
		Material mat = new Material();
		if(e.attribute("isClient") != null) //make client a different color!
		{
			mat.setDiffuseColor(1.0f,0.0f,0.0f);
		}
		else
		{
			mat.setDiffuseColor(0.0f,0.0f,1.0f);
		}
		mat.setSpecularColor(1.0f,1.0f,1.0f);
		mat.setShininess(64.0f); //I swear to god: "shininess - the material's shininess in the range [1.0, 128.0] with 1.0 being not shiny and 128.0 being very shiny."
		appear.setMaterial(mat);

		BranchGroup sroot = new BranchGroup(); //a root for the shapes. In case we want to do other stuff to them (if it's redundant then J3D will get rid of it anyway)
		coord.addChild(sroot); //add sroot to the tree
		
		for(VirtualShape s : e.shapes) //run through the shapes!
		{
			//now see what kind of shapes we're using. 
			//instanceof allows us to deal with extensions of the virtual primitives,
			//but do we want that? If not, then change to be a getClassName (or whatever)
			if(s instanceof VirtualBox)
			{
				Transform3D local = new Transform3D(new Quat4f(s.getRotation()), new Vector3f(s.getCenter()),1); //the local coordinates for the shape
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				Box p = new Box(((VirtualBox)s).getDimX(), ((VirtualBox)s).getDimY(), ((VirtualBox)s).getDimZ(), Primitive.GENERATE_NORMALS, appear);
					//TransformGroup rot = createSpinningBehavior(localg);
					//rot.addChild(new ColorCube(0.2));
				
				localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualSphere)
			{
				Transform3D local = new Transform3D(new Quat4f(s.getRotation()), new Vector3f(s.getCenter()),1); //the local coordinates for the shape
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				Sphere p = new Sphere(((VirtualSphere)s).getRadius(), Primitive.GENERATE_NORMALS, appear);
				
				localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualCylinder)
			{
				Transform3D local = new Transform3D(new Quat4f(s.getRotation()), new Vector3f(s.getCenter()),1); //the local coordinates for the shape
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				Cylinder p = new Cylinder(((VirtualCylinder)s).getRadius(), ((VirtualCylinder)s).getHeight(), Primitive.GENERATE_NORMALS, appear);
				
				localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualCone)
			{
				Transform3D local = new Transform3D(new Quat4f(s.getRotation()), new Vector3f(s.getCenter()),1); //the local coordinates for the shape
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				Cone p = new Cone(((VirtualCone)s).getRadius(), ((VirtualCone)s).getHeight(), Primitive.GENERATE_NORMALS, appear);
				
				localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else
			{
				//print error message
				System.out.println("unrecognizable shape");
			}
			
			//we could probably break up the code some, but will deal with that later.
		}
		broot.compile(); //let J3D optimize the branch
	}//constructor

	public BranchGroup getBranchScene()
	{
		return broot;
	}
	
	//an accessor for detaching (and thereby deleting) the branch
	public void detach()
	{
		broot.detach();
	}

	public void setTranslation(float[] p)
	{
		Transform3D t = new Transform3D(); //a new Transform
		coord.getTransform(t); //fill the transform with our current settings
		t.setTranslation(new Vector3f(p)); //set the new translation
		coord.setTransform(t); //set as our new state
	}
		
	public void setRotation(float[] f)
	{
		Transform3D t = new Transform3D(); //a new Transform
		coord.getTransform(t); //fill the transform with our current settings
		t.setRotation(new Quat4f(f)); //set our current rotation
		coord.setTransform(t);
	}
	
	public void setTransform(float[] p, float[] f)
	{
		Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
		coord.setTransform(t);
	}

	//This is just for fun. Though might be neat to include it even once we aren't using cubes.
	//Basically pass it a transform group of the tree, and it will return a transform group for you to continue adding to
	public TransformGroup createSpinningBehavior(TransformGroup bg)
	{
		TransformGroup spinx = new TransformGroup(); //another node for coordinate system transformation
		spinx.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime
		TransformGroup spiny = new TransformGroup(); //as the above lines
		spiny.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		bg.addChild(spinx); //add the first coordinate system transformation to the root
		spinx.addChild(spiny); //add the other coordinate system transformtions to the tree
		
		Transform3D yAxis = new Transform3D(); //a transformation-defined coordinate system. We don't touch it initially
		Transform3D xAxis = new Transform3D(); //another change in the coordinate system
		xAxis.rotZ(Math.PI/4.0d); //make xAxis actually change the y-axis (the default) into the x-axis by rotating around the z-axis
		Alpha rotationAlpha = new Alpha(-1, 6000); //create an Alpha (timer)
		Alpha rotationAlpha2 = new Alpha(-1, 9000); //create a slower Alpha (timer)
		//create rotation behaviors. Everytime Alpha fires, they rotate the given coordinate system around the give axis
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, spinx, yAxis, 0.0f, (float)Math.PI*2.0f);
		RotationInterpolator rotator2 = new RotationInterpolator(rotationAlpha, spiny, xAxis, 0.0f, (float)Math.PI*2.0f);
		
		//yay bounding spheres!
		BoundingSphere bounds = new BoundingSphere(); //creating a bounding sphere (for clipping)
		rotator.setSchedulingBounds(bounds); //only perform the behavior for the sphere
		rotator2.setSchedulingBounds(bounds); //ditto
		spinx.addChild(rotator); //add the behavior to the tree
		spiny.addChild(rotator2); //ditto
		
		return spiny;
	}	
	
	//member functions
		//gets/sets??
			//If we make public, then Representation takes care of moving stuff. If we make private, then calls methods that move stuff.
			//I think I like doing it privately--make take an extra step, but hides implementation (so can have only a single TransformGroup, for example)
}
