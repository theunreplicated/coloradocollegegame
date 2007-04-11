//ViewElementBranch.java
//@author Joel Ross

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A type of ElementBranch that defines a J3D branch for a view--a camera.
 This class can define a Client's GameElement which is either a first-person
 camera, a third-person element with a following camera, or a third-person
 element with a static camera (or hopefully any kind of camera we want).
 
 Currently the methods are set to define a first-person camera.
***/

public class ViewElementBranch implements ElementBranch
{
	public static final int STATIC_VIEW = 0;
	public static final int FIRST_PERSON_VIEW = 1;
	public static final int OFFSET_VIEW = 2;
	public static final int FOLLOWING_VIEW = 3;

	public static final float[] OFFSET = new float[] {0.0f, 2.0f, 5.0f};

	//member variables
	private ViewingPlatform camera;
	private TransformGroup coord; //transformed coordinates for this branch
	private GameElementBranch avatar;
	private BranchGroup avatarRoot; //where the avatar branch is attached
	
	private int viewMode; //what kind of view we're using

	//constructor
	public ViewElementBranch(GameElement e, int v)
	{
		viewMode = v;
		camera = new ViewingPlatform();
		coord = camera.getMultiTransformGroup().getTransformGroup(0); //coord is the VP's transformation
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime

		Transform3D posi;
		if(viewMode == FIRST_PERSON_VIEW)
		{
			posi = new Transform3D(new Quat4f(e.getFacing()), new Vector3f(e.getPosition()), 1); //set us to the element's transform
		}
		else if(viewMode == OFFSET_VIEW)
		{
			Vector3f p = new Vector3f(e.getPosition());
			p.add(new Vector3f(OFFSET));
			posi = new Transform3D(new Quat4f(e.getFacing()), p, 1);
		}
		else
		{
			posi = new Transform3D(new Quat4f(Constants.DEFAULT_FACING), new Vector3f(0f,0f,5f), 1);
		}
		coord.setTransform(posi); //set our transform group to the default position

		avatar = new GameElementBranch(e); //the avatar object for this view
	}

	//cycles through the available view models.
	public void changeView()
	{
		changeView((viewMode%2)+1); //cycle between 1 and 2 only
	}

	//change to specified viewMode
	public void changeView(int to)
	{
		//add "from" checking here?

		if(to == FIRST_PERSON_VIEW)
		{
			viewMode = FIRST_PERSON_VIEW; //set the new mode
			if(avatar.getBranchScene().isLive()) //if is attached
				detach(); //remove the avatar

			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setTranslation(avatar.getTranslation()); //set to our avatar's translation
			coord.setTransform(t); //set as our new state
		}
		else if(to == OFFSET_VIEW)
		{
			viewMode = OFFSET_VIEW; //set the new mode
			if(!avatar.getBranchScene().isLive()) //if is NOT attached
				attachAvatar();	//add the avatar

			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			Vector3f pp = avatar.getTranslation(); //get the avatar's current translation
			float[] f = new float[4];
			avatar.getRotation().get(f); //get a float[] of the avatar's rotation
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f))); //add the spun offset to the translation
			t.setTranslation(pp); //set the new translation
			coord.setTransform(t); //set as our new state
		}
		//add support for other views
	}

	//a method to fetch the ViewingPlatform in order to construct the view branch
	public ViewingPlatform getViewingPlatform()
	{
		return camera;
	}

	public int getViewMode()
	{
		return viewMode;
	}
	
	public void createAvatar(BranchGroup bg)
	{
		avatarRoot = bg;
		if(viewMode != FIRST_PERSON_VIEW) //if we're not in FP view
			avatarRoot.addChild(avatar.getBranchScene()); //add the avatar to the scene graph.
	}
	
	public void attachAvatar()
	{
		avatarRoot.addChild(avatar.getBranchScene()); //add the avatar to the scene graph.
	}
	
	public GameElementBranch getAvatar()
	{
		return avatar;	
	}

	public void setTranslation(float[] p)
	{
		avatar.setTranslation(p); //always move the avatar Element

		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setTranslation(new Vector3f(p)); //set the new translation
			coord.setTransform(t); //set as our new state
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//Do what here? We NEED facing and position to do an offset!
				//which we can get from the AVATAR now. Fill in later	
			/*
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			t.setTranslation(pp); //set the new translation
			coord.setTransform(t); //set as our new state
			*/
			
		}
		else
		{
			
		}
	}
		
	public void setRotation(float[] f)
	{
		avatar.setRotation(f); //always move the avatar Element 

		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setRotation(new Quat4f(f)); //set our current rotation
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//Do what here? We NEED facing and position to do an offset!
			
			/*
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			t.setTranslation(pp); //set the new translation
			coord.setTransform(t); //set as our new state
			*/
		}
		else
		{
			
		}
	}

	public void setScale(float[] s)
	{
		avatar.setScale(s); //always move the avatar Element

		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//should we have the offset be based on the scale? I think that's what it does atm
			
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else
		{
		
		}
	}
	
	public void setTransform(float[] p, float[] f, float[] s)
	{
		avatar.setTransform(p,f,s); //always move the avatar Element

		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			Transform3D t = new Transform3D(new Quat4f(f), pp, 1);
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else
		{
		
		}
	}

	/**
	 **Methods that get passed along to the avatar
	 **/

	public BranchGroup getBranchScene()
	{
		return avatar.getBranchScene();
	}
	
	public void detach()
	{
		avatar.detach();
	}
	
/*	
	public void setAppearance(Appearance a)
	{
		avatar.setAppearance(a);	
	}

	public Appearance getAppearance()
	{
		return avatar.getAppearance();
	}
*/

}