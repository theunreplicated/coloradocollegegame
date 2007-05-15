//Representation3D.java
//@author Joel Ross

import java.applet.Applet;
import java.awt.*;
import java.util.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Text2D;
import javax.swing.JOptionPane;

/***
 A 3D Representation of the game using the Java3D API
***/

public class Representation3D extends Applet implements Representation
{
	static final long serialVersionUID = -7743175550804657967L;

	//a hashmap for converting between the World and the Java3D tree.
	HashMap<GameElement,ElementBranch> elementsToNodes = new HashMap<GameElement,ElementBranch>();
	
	GameElement elementStart; //for checking the list
	BranchGroup scene; //the root of the scene--lets us add more elements
	BranchGroup bbroot; //where to attach the messages
	Canvas3D canvas3D;
	ViewElementBranch veb;
	int viewMode;
	boolean testing;
	
	//constructor
	public Representation3D(int _viewMode)
	{
		viewMode = _viewMode;
		testing = false;
	}

	public Representation3D(int _viewMode, boolean _testing)
	{
		viewMode = _viewMode;
		testing = _testing;	
	}

	//initializor (all code previously in constructor)
	public void initialize(World w, Logger myLogger)
	{
		setLayout(new BorderLayout()); //set the Applet's layout
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //how does SimpleUniverse want to draw stuff?
		canvas3D = new Canvas3D(config); //make a new canvas, as SimpleUniverse likes it
		add("Center",canvas3D); //add the canvas to the Applet
		Viewer viewer = new Viewer(canvas3D); //a Viewer to go with the canvas

		BranchGroup superRoot = new BranchGroup(); //the ultimate root of the entire scene. Created here so we can add stuff later	

		scene = new BranchGroup();
		scene.setCapability(Group.ALLOW_CHILDREN_WRITE); //let us modify the children at runtime
		scene.setCapability(Group.ALLOW_CHILDREN_EXTEND); //allow us to add more Elements to the scene during runtime

		elementStart = w.getFirstElement(); //get the Elements to start building the tree 
		elementStart.attribute("isClient", true); //mark the first element as the Client (Representation-dependent attribute)
		veb = createCamera(elementStart, scene, viewMode); //build the camera FIRST
		ViewingPlatform vp = veb.getViewingPlatform();
		TransformGroup vpt = vp.getMultiTransformGroup().getTransformGroup(0);
		//cameraRoot = new BranchGroup(); //do we need this reference?
		//vpt.addChild(cameraRoot);
		
		/*tie the lighting to the camera, so that objects are always lit as if from the front.*/
		BranchGroup candleStick = new BranchGroup();
		addDefaultLights(candleStick);
		vpt.addChild(candleStick);
		//addDefaultLights(superRoot); //add default lighting to world--doesn't move

		/*create the bulletin board*/
		bbroot = new BranchGroup();
		bbroot.setCapability(Group.ALLOW_CHILDREN_WRITE);
		bbroot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		Transform3D bbt = new Transform3D();
		//bbt.setTranslation(new Vector3f(-.3f,.25f,-.75f)); //sized for 300x300
		bbt.setTranslation(new Vector3f(-.3f,.15f,-.75f)); //sized for fullscreen
		TransformGroup bbtg = new TransformGroup(bbt);
		bbtg.addChild(bbroot);
		vpt.addChild(bbtg);
		BranchGroup wrapper = new BranchGroup(); //a placeholder, containing nothing
		wrapper.setCapability(BranchGroup.ALLOW_DETACH);
		bbroot.addChild(wrapper);
		
		
		createSceneGraph(elementStart, scene); //initialize the scene based on the Client's world
		superRoot.addChild(scene); //add the scene to the tree

		SimpleUniverse simpleU = new SimpleUniverse(vp, viewer);
		//simpleU.getViewingPlatform().setNominalViewingTransform(); //set default eye's locathe Eye's location
		simpleU.addBranchGraph(createBackground()); //set the background
		
		superRoot.compile(); //let Java3D optimize the tree
		simpleU.addBranchGraph(superRoot); //add the scene to the tree. THIS ALSO TELLS IT TO BEGIN RENDERING!
	}
		
	//creates an returns a "view" or camera based on the given element
	private ViewElementBranch createCamera(GameElement e, BranchGroup vscene, int viewMode)
	{
		ElementBranch geb = elementsToNodes.get(e);
		if(geb != null) //check if we already added this element
		{ 
			elementsToNodes.remove(e); //remove the camera-less element
			geb.detach(); //remove branch from the tree
		}

		ViewElementBranch _veb = new ViewElementBranch(e,vscene,viewMode,testing); //create the camera (effectively)
		elementsToNodes.put(e,_veb); //add the camera to the hashmap
		return _veb;
	}

	//create the bulk of the Java3D tree based on e, attaching it to gscene
	private void createSceneGraph(GameElement e, BranchGroup gscene)
	{
		GameElement first = e; //for looping
		do
		{
			if(!elementsToNodes.containsKey(e)) //check that we haven't already added the element
			{			
				GameElementBranch bg = new GameElementBranch(e, testing); //make a new branch for the element
				elementsToNodes.put(e,bg); //make a conversion entry so we can find the branch later
				
				gscene.addChild(bg.getBranchScene()); //add the branch to the root.
			}
			e = e.next; //loop
		} while(e != first);
	
		//Representation-level objects
		if(testing)
			gscene.addChild(createGrid(40)); //could automatically determine the size of the proper grid if we wanted
	}
	
	//create and return a background for the world
	private BranchGroup createBackground()
	{
		BranchGroup root = new BranchGroup(); // a root node for the background
		
		Background back = new Background(0.0f,0.0f,0.0f); //a background node! Set the background's color here	
		back.setApplicationBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0)); //set the bounds of the area to make a background (very very big)
	
		root.addChild(back); //add the background to the branch
		root.compile(); //just in cases
		return root; //return the branch
	}

	//add default lighting to the BranchGroup
	//currently light bounds are HUGE for testing
	private void addDefaultLights(BranchGroup bg)
	{
		DirectionalLight keyLight = new DirectionalLight(true,
			new Color3f(1.0f, 1.0f, 1.0f), 
			new Vector3f(.433f, -.5f, -.75f));
		keyLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(keyLight);
		
		DirectionalLight fillLight = new DirectionalLight(true,
			new Color3f(0.125f, 0.125f, 0.125f), 
			new Vector3f(-.259f, .25f, -.933f));
		fillLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(fillLight);		

		DirectionalLight backLight = new DirectionalLight(true,
			new Color3f(1.0f, 1.0f, 1.0f), 
			new Vector3f(0.0f, -0.259f, .966f));
		backLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(backLight);			
	}

	//fetches the canvas we're drawing this on
	public Component getComponent()
	{
		return canvas3D;
	}

	//cycles through the views - pass to the camera
	public void changeView()
	{
		veb.changeView();
	}
	
	//changes to the specified view - pass to the camera
	public void changeView(int to)
	{
		veb.changeView(to);
	}

	//CHANGES the position of the camera BY the specified translation and rotation
	public void adjustCamera(float[] translation, float[] rotation)
	{
		veb.transformCamera(translation, rotation);	
	}

	//SETS the position of the camera TO the specified translation and rotation
	public void setCamera(float[] translation, float[] rotation)
	{
		veb.setCameraTransform(translation, rotation);
	}

	//an update method for updating a particular element's location
	public void updateLocation(GameElement e)
	{
		ElementBranch bg = elementsToNodes.get(e); //get the corresponding GameElementBranch
		if(bg != null) //just in case
		{
			bg.setTransform(e.getPosition(),e.getFacing(), e.getScale());
		}
	}

	//an update method for updating a particular element's presence in the game
	public void updatePresence(GameElement e)
	{
		GameElement next;
		synchronized(e)
		{
  			next = e.next; //in case someone else is accessing
		}

		ElementBranch bg = elementsToNodes.get(e); //fetch the branch
		if(bg!=null) //if exists
		{
			if(next == null) //check if it shouldn't
			{
				elementsToNodes.remove(e); //remove from the hash
				bg.detach(); //remove branch from the tree
			}
		}
		else //if it doesn't exist
		{
			if(next != null) //check if it should
			{
				GameElementBranch nbg = new GameElementBranch(e, testing); //make a new branch for the element
				elementsToNodes.put(e,nbg); //make a conversion entry so we can find the branch later
				scene.addChild(nbg.getBranchScene()); //add the branch to the scene
			}
		}	
	}

	//returns the position of the camera
	public float[] getCameraPosition()
	{
		return veb.getTranslation();
	}

	//returns the facing of the camera
	public float[] getCameraFacing()
	{
		return veb.getRotation();
	}

	public void displayMessage(String msg, int flags)
	{
		if(flags==1) //Option 1: a simple JOptionPane to show the message
			JOptionPane.showMessageDialog(null,msg,"Message",JOptionPane.PLAIN_MESSAGE);
		else //Option 2: Create and add a Text2D object
		{
			bbroot.removeAllChildren();
			//bbroot.removeChild(0);
			
			BranchGroup wrapper = new BranchGroup();
			wrapper.setCapability(BranchGroup.ALLOW_DETACH);
			Text2D nbb = new Text2D(msg, new Color3f(0,1,0), "Arial", 9, Font.BOLD);  
			wrapper.addChild(nbb);
			if(!msg.equals("")) //as long as we weren't a blank message
			{
				Appearance bga = new Appearance();
				bga.setColoringAttributes(new ColoringAttributes(0,0,0,ColoringAttributes.SHADE_FLAT));
				Shape3D background = new Shape3D(nbb.getGeometry(), bga);
				wrapper.addChild(background);
			}
			bbroot.addChild(wrapper);
		}	
	}


//replace this with a texturemap or something. May help with speed issues some	
	//creates a Representation-level grid to display as the ground. For testing mostly
	private Shape3D createGrid(int gridSize)
	{
		int entries = 4*((2*gridSize)+1);
		LineArray grid = new LineArray(entries, LineArray.COORDINATES | LineArray.COLOR_3);
		
		int index = 0;
		for(int i=-gridSize; i<=gridSize; i++)
		{
			grid.setCoordinate(index, new Point3f(-gridSize-1.0f, -3.0f, i));
			index++;
			grid.setCoordinate(index, new Point3f( gridSize+1.0f, -3.0f, i));
			index++; 
		}
		for(int i=-gridSize; i<=gridSize; i++)
		{
			grid.setCoordinate(index, new Point3f(i, -3.0f,-gridSize-1.0f));
			index++;
			grid.setCoordinate(index, new Point3f(i, -3.0f, gridSize+1.0f));
			index++; 
		}
		
		Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		for(int i=0; i<entries; i++)
			grid.setColor(i, green);	
		
		Shape3D gridShape = new Shape3D(grid);
		return gridShape;
	}

	//a method for printing out J3D info
	public String getJ3DInfo()
	{
		//fill this in. VirtualUniverse.getProperties() will tell us about OpenGL vs Direct3D, etc		
		return "";
	}

	//this looks familiar...
	public static void main(String[] args)
	{
		Representation3D me = new Representation3D(ViewElementBranch.OFFSET_VIEW,false);

		Client.initialize(args, me); //create a Client for the game
		
		new MainFrame(me,300,300); //run the applet inside a Frame
	} 
}
