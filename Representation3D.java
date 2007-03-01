//Representation3D.java
//@author Joel Ross

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A 3D Representation of the game using the Java3D API
 Interaction is currently being implemented
***/

public class Representation3D extends Applet implements Representation
{
	//a hashmap for converting between the World and the Java3D tree.
	HashMap<GameElement,ElementBranch> elementsToNodes = new HashMap<GameElement,ElementBranch>();
	
	//constructor
	public Representation3D(Client _client)
	{
		setLayout(new BorderLayout()); //set the Applet's layout
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //how does SimpleUniverse want to draw stuff?
		Canvas3D canvas3D = new Canvas3D(config); //make a new canvas, as SimpleUniverse likes it
		add("Center",canvas3D); //add the canvas to the Applet

		//ClientInput stuff
		this.setFocusable(true);
		ClientInput ci = _client.getClientInput();
		this.addMouseListener(ci);
		this.addKeyListener(ci);
		
		GameElement[] elements = _client.getWorldElements(); //get the Elements to start building the tree
		
		BranchGroup superRoot = new BranchGroup(); //the ultimate root of the entire scene. Created here so we can add stuff later	
		BranchGroup scene = createSceneGraph(elements); //initialize the scene based on the Client's world
		addDefaultLights(superRoot); //add (default)lighting to the world
		superRoot.addChild(scene); //add the scene to the tree
		
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D); //make a new SimpleUniverse object
		simpleU.getViewingPlatform().setNominalViewingTransform(); //set the Eye's location
		simpleU.addBranchGraph(createBackground()); //set the background
		
		superRoot.compile(); //let Java3D optimize the tree
		simpleU.addBranchGraph(superRoot); //add the scene to the tree. THIS ALSO TELLS IT TO BEGIN RENDERING!

		//a thread to notify us when something has changed
		RepresentationListener rl = new RepresentationListener(this, elements, _client.getLogger());
		rl.start();
	}

	//create the bulk of the Java3D tree
	public BranchGroup createSceneGraph(GameElement[] _elements)
	{
		BranchGroup root = new BranchGroup(); //A root node for this set of objects
		
		for(GameElement e : _elements)
		{
			if(e != null)
			{
				ElementBranch bg = new ElementBranch(e); //make a new branch for the element
				elementsToNodes.put(e,bg); //make a conversion entry so we can find the branch later
				
				root.addChild(bg.getBranchScene()); //add the branch to the root.
			}
		}
	
		return root; //return the branch
	}
	
	//create and return a background for the world
	public BranchGroup createBackground()
	{
		BranchGroup root = new BranchGroup(); // a root node for the background
		
		Background back = new Background(0.0f,0.0f,0.0f); //a background node! Set the background's color here	
		back.setApplicationBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0)); //set the bounds of the area to make a background (very very big)
	
		root.addChild(back); //add the background to the branch
		root.compile(); //just in cases
		return root; //return the branch
	}

	//add default lighting to the BranchGroup
	public void addDefaultLights(BranchGroup bg)
	{
		AmbientLight amLight = new AmbientLight(); //ambient light
		amLight.setInfluencingBounds(new BoundingSphere()); //within 1 of the origin
		bg.addChild(amLight); //add light to scene
		
		DirectionalLight dirLight1 = new DirectionalLight(); //directional light
		dirLight1.setInfluencingBounds(new BoundingSphere());
		dirLight1.setColor(new Color3f(1.0f, 1.0f, 1.0f)); //color of the light
		dirLight1.setDirection(new Vector3f(-1.0f, -0.5f, -1.0f)); //direction of the light
		bg.addChild(dirLight1); //add light to scene
	}

	//temp change method
	public void changeElement(GameElement e)
	{
		ElementBranch eb = elementsToNodes.get(e);
		//change eb
	}

	//an update method
	public void update()
	{
		System.out.println("update method called!");
		//now we need to figure out how to update the world based on the new element
	}

	//this looks familiar...
	public static void main(String[] args)
	{
		Client myClient = Client.initialize(args); //create a Client for the game

		Frame f = new MainFrame(new Representation3D(myClient),300,300); //run the applet inside a Frame
	} 
}