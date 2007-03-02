//ElementBranch.java
//@author Joel Ross

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A class that represents a GameElement as a branch of the Java3D tree.
 Contains references to stuff in the tree we may want to change so we can
 sync with the World.
***/

public class ElementBranch //it doesn't like if we extend BranchGroup, so just make that a member variable and fetch it later
{
	//member variables - anything we'd want to change later (Element level)
	private TransformGroup coord; //transformed coordinates for this branch
	private Appearance appear; //an appearance node reference
	private BranchGroup broot; //the root of the branch.
	
	//constructor
	public ElementBranch(GameElement e)
	{
		broot = new BranchGroup(); //the root of this branch

		Transform3D posi = new Transform3D(); //make the new coordinate system
	System.out.println("Element at={"+e.position[0]+","+e.position[1]+","+e.position[2]+"}");
		posi.setTranslation(new Vector3f(e.position)); //move to the element's position
		//posi.setRotation(); //set to something for orientation??
		coord = new TransformGroup(posi); //create the coordinate node
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime
		broot.addChild(coord); //add the transformation to the root.
		
		//setting an appearance so our objects look like objects.
		//This stuff should probably be taken directly from the Element.
		//Or adjust the appearance based on Shape inside the loop
		appear = new Appearance(); //will need to specify how to get this appearance from the Element
		Material mat = new Material();
		mat.setDiffuseColor(1.0f,0.0f,0.0f);
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
				Transform3D local = new Transform3D(); //the local coordinates for the shape
				local.setTranslation(new Vector3f(((VirtualBox)s).getCenter())); //move to the shape's center
				//add any rotation stuff here
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				//Box p = new Box(((VirtualBox)s).getDimX(), ((VirtualBox)s).getDimY(), ((VirtualBox)s).getDimZ(), Primitive.GENERATE_NORMALS, appear);
				TransformGroup rot = createSpinningBehavior(localg);
				rot.addChild(new ColorCube(0.2));
				
				//localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualSphere)
			{
				Transform3D local = new Transform3D(); //the local coordinates for the shape
				local.setTranslation(new Vector3f(((VirtualSphere)s).getCenter())); //move to the shape's center
				//add any rotation stuff here
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				Sphere p = new Sphere(((VirtualSphere)s).getRadius(), Primitive.GENERATE_NORMALS, appear);
				//TransformGroup rot = createSpinningBehavior(localg);
				//rot.addChild(new ColorCube(0.2));
				
				localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualCylinder)
			{
				Transform3D local = new Transform3D(); //the local coordinates for the shape
				//local.setTranslation(new Vector3f(((VirtualCylinder)s).getCenter())); //move to the shape's center
				//add any rotation stuff here
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				//Cylinder p = new Cylinder(((VirtualCylinder)s).getRadius(), ((VirtualCylinder)s).getHeight(), Primitive.GENERATE_NORMALS, appear);
				TransformGroup rot = createSpinningBehavior(localg);
				rot.addChild(new ColorCube(0.2));
				
				//localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else if(s instanceof VirtualCone)
			{
				Transform3D local = new Transform3D(); //the local coordinates for the shape
				//local.setTranslation(new Vector3f(((VirtualCone)s).getCenter())); //move to the shape's center
				//add any rotation stuff here
				TransformGroup localg = new TransformGroup(local);
				
				//make the primitive
				//Cone p = new Cone(((VirtualCone)s).getRadius(), ((VirtualCone)s).getHeight(), Primitive.GENERATE_NORMALS, appear);
				TransformGroup rot = createSpinningBehavior(localg);
				rot.addChild(new ColorCube(0.2));
				
				//localg.addChild(p); //add the primitive to the TransformGroup
				sroot.addChild(localg); //add the TransformGroup to the shape root node			
			}
			else
			{
				//print error message
				System.out.println("unrecognizable shape");
			}
			
			//we could probably break up the code some, but will deal with that later.
		}
		
	}

	public BranchGroup getBranchScene()
	{
		return broot;
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

	//a method to scale measurements of Elements into J3D measurements.
	//We decided that I shouldn't need this, but I'll leave it here anyway
	public Vector3f scale(Vector3f v, float s)
	{
		return new Vector3f(v.x/s, v.y/s, v.z/s);	
	}

	public void setTranslation(float[] p)
	{
		Transform3D t = new Transform3D();
		//coord.getTransform(t); //fetch our old state. Don't need this since we're only storing translation
		t.setTranslation(new Vector3f(p)); //reset our translation
		coord.setTransform(t); //set our new state
	}
		
	//member functions
		//gets/sets??
			//If we make public, then Representation takes care of moving stuff. If we make private, then calls methods that move stuff.
			//I think I like doing it privately--make take an extra step, but hides implementation (so can have only a single TransformGroup, for example)
}