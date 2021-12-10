package swb;
import java.io.File;
import java.io.IOException;

import javax.swing.JPopupMenu;

import com.jogamp.opengl.GL3;

import swb.editors.EditorView;
import swb.utils.AbstractTree;

/**
 * This class implements a Tree data structure where each
 * node has one parent and multiple children.
 */
public class GLNode extends AbstractTree<GLNode>
{
	public static final byte ID_GENERIC = 0;
	public static final byte ID_BUFFER = 1;
	public static final byte ID_ARRAY = 2;
	public static final byte ID_PROGRAM = 4;
	public static final byte ID_SHADER = 8;
	public static final byte ID_TEXTURE = 16;
	public static final byte ID_FRAMEBUFFER = 32;
	
	public static final byte LAST_BUFFER = 0;
	public static final byte LAST_ARRAY = 1;
	public static final byte LAST_PROGRAM = 2;
	public static final byte LAST_SHADER = 3;
	public static final byte LAST_TEXTURE = 4;
	public static final byte LAST_FRAMEBUFFER = 5;
	
	public static final byte INSTANCE_CAPACITY = 6;
	
	/** Name of the node */
	protected String name;
	/** Flag that indicates the node needs to be recompile */
	protected boolean compileFlag = false;
	/** Flag that indicates the node needs to be initialized */
	protected boolean initFlag = false;
	/** Flag that indicates the node needs to be updated */
	protected boolean modifyFlag = false;
	/** Flag that indicates the node needs to be deleted */
	protected boolean deleteFlag = false;
	
	public final int renderID;
	public boolean renderFlag;
	
	public GLNode( String name, int id, boolean renderable )
	{
		this.name = name;
		renderFlag = renderable;
		renderID = id;
	}
	
	public GLNode( String name, boolean renderable )
	{
		this( name, ID_GENERIC, renderable );
	}
	
	/**
	 * Creates a new node with no parent or child
	 * @param name - The name of the node
	 */
	public GLNode( String name )
	{
		this( name, false );
	}
	
	/**
	 * Creates an editing view that can be used to modify this node
	 * @return {@link EditorView}
	 */
	public EditorView createEditor()
	{
		return null;
	}
	
	/**
	 * Populates the menu with actions that can be performed on the node
	 * @param menu - Menu source that is being displayed
	 */
	public void populate( JPopupMenu menu ) {}
	
	/**
	 * Generates the path of this node. The path is made from the
	 * node and its parents separated by '\'
	 * @return The path of this node
	 */
	public String getPath()
	{
		return parent == null ? name : parent.getPath() + "\\" + name;
	}
	
	/**
	 * Re-processes the flags on the node and checks if the node needs to
	 * be re-compiled. Usually if the node has been compiled, it should be 
	 * safe to update and render. This operation requires a instance list
	 * to check for node dependencies.
	 * @return Whether the build was successful
	 */
	public boolean build( Renderer renderer ) 
	{ 
		return true;
	}
	
	/**
	 * Checks to see if the node can be updated without error
	 * @param gl - GL3 instance
	 */
	public boolean compile( GL3 gl )
	{
		return compileFlag = true;
	}
	
	/**
	 * Initializes or updates the memory used by this node
	 * @param gl - GL3 instance
	 */
	public void update( GL3 gl )
	{
		initFlag = true;
		modifyFlag = false;
	}
	
	/**
	 * Links the node to the current GL instance
	 * @param gl - GL3 instance
	 */
	public void bind( GL3 gl ) { }
	
	/**
	 * Renders the node using its defined rendering procedure
	 * @param gl - GL3 instance
	 */
	public void render( GL3 gl ) { }
	
	/**
	 * Removes the nodes and its children from the GPU memory
	 * @param gl - GL3 instance
	 */
	public void dispose( GL3 gl )
	{
		for( GLNode node : children )
			node.dispose( gl );
		
		initFlag = false;
		modifyFlag = false;
		deleteFlag = false;
	}
	
	/**
	 * Common method that is used to save the node as a file.
	 * @param file - File to save node to
	 * @param format - Key that can be used to specify a format or extension
	 * @throws IOException - If the node cannot be saved to a file
	 */
	public void saveTo( File file, String format ) throws IOException 
	{ 
		throw new IOException( "Undefined method for writing node" );
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean add( GLNode node ) 
	{
		if( node == null ) return false;
		children.add( node );
		node.setParent( this );
		return true;
	}
}
