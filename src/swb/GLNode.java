package swb;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.JPopupMenu;

import com.jogamp.opengl.GL3;

import swb.editors.EditorView;

/**
 * This class implements a Tree data structure where each
 * node has one parent and multiple children.
 */
public class GLNode implements Collection<GLNode>
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
	
	/** List of children */
	private LinkedList<GLNode> children = new LinkedList<>();
	
	/** Name of the node */
	protected String name;
	/** The node's direct parent */
	protected GLNode parent = null;
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
	 * Sets the parent of this node
	 * @param node - New parent node
	 */
	public void setParent( GLNode node ) 
	{
		if( parent != null ) parent.children.remove( this );
		parent = node;
	}
	
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
	 * Returns the parent of this node
	 * @return The parent of this node
	 */
	public GLNode parent() 
	{
		return parent;
	}
	
	/**
	 * Returns the direct descendants of this node.
	 * @return The children of this node
	 */
	public List<GLNode> children() 
	{
		return children;
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
	 * Calculates the height of the tree
	 * @return The height of the tree
	 */
	public int height()
	{
		int size = 1;
		GLNode current = this;
		while( !current.isEmpty() )
			size++;
		return size;
	}
	
	public void childEventNotify( GLNode source )
	{
		parent.childEventNotify( source );
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
	
	@Override
	public boolean addAll( Collection<? extends GLNode> c ) 
	{
		boolean changed = false;
		for( GLNode node : c )
		{
			changed |= add( node );
		}
		return changed;
	}
	
	/**
	 * Removes the last child of this node and the entire branch connected to node
	 * @return the removed child node
	 */
	public GLNode removeLast()
	{
		GLNode child = children.removeLast();
		child.setParent( null );
		return child;
	}
	
	@Override
	public boolean remove( Object o ) 
	{
		boolean changed = false;
		for( GLNode node : children )
		{
			if( node == o )
			{
				node.setParent( null );
				changed |= children.remove( o );
			}
			
			changed |= node.remove( o );
		}
		return changed;
	}

	@Override
	public boolean removeAll( Collection<?> c ) 
	{
		boolean changed = false;
		for( GLNode node : children )
		{
			if( c.contains( node ) )
			{
				node.setParent( null );
				changed |= children.remove( node );
			}
			else
				changed |= node.removeAll( c );
		}
		return changed;
	}

	@Override
	public boolean retainAll( Collection<?> c ) 
	{
		boolean changed = false;
		for( GLNode node : children )
		{
			if( !c.contains( node ) )
			{
				node.setParent( null );
				changed |= children.remove( node );
			}
			else
				changed |= node.retainAll( c );
		}
		return changed;
	}
	
	@Override
	public void clear() 
	{
		children.clear();
	}
	
	@Override
	public boolean isEmpty() 
	{
		return children.isEmpty();
	}
	
	@Override
	public int size() 
	{
		int size = 0;
		for( GLNode node : children )
			size += node.size();
		return size;
	}
	
	@Override
	public boolean contains(Object o) 
	{
		if( this == o ) return true;
		for( GLNode node : children )
			if( node.contains( o ) )
				return true;
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) 
	{
		for( Object o : c )
			if( !contains( o ) )
				return false;
		return true;
	}
	
	@Override
	public Object[] toArray() 
	{
		Object[] arr = new Object[ size() ];
		return toArray( arr );
	}

	@Override
	public <T> T[] toArray( T[] arr ) 
	{
		toArray( arr, 0 );
		return arr;
	}
	
	private <T> int toArray( T[] arr, int offset )
	{
		for( GLNode node : children )
			offset = node.toArray( arr, offset );
		return offset;
	}
	
	@Override
	public Iterator<GLNode> iterator() 
	{
		return new TreeIterator();
	}
	
	/** 
	 * Iterator that can navigate all elements in the Tree
	 * */
	private class TreeIterator implements Iterator<GLNode>
	{
		/** Stack of remaining nodes to traverse */
		private Stack<GLNode> stack = new Stack<>();

		/**
		 * Creates an iterator with the first node being the root node
		 */
		public TreeIterator()
		{
			stack.add( GLNode.this );
		}
		
		@Override
		public boolean hasNext() {
			
			return !stack.isEmpty();
		}

		@Override
		public GLNode next() 
		{
			GLNode current = stack.pop();
			for( int i = current.children.size() - 1; i >= 0; i-- )
				stack.push( current.children.get( i ) );
			return current;
		}
	}
}
