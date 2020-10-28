import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL3;

public class Node
{
	private String name;
	private Node parent = null;
	private LinkedList<Node> children = new LinkedList<>();
	
	public Node( String name )
	{
		this.name = name;
	}
	
	public EditorView createEditor()
	{
		return null;
	}
	
	public String toString()
	{
		return name;
	}
	
	public String getPath()
	{
		return parent == null ? name : parent.getPath() + "\\" + name;
	}
	
	public List<Node> children() 
	{
		return children;
	}

	public void add( Node node ) 
	{
		if( node == null ) return;
		children.add( node );
		node.setParent( this );
	}

	public void update() 
	{ 
		sort();
		for( Node node : children )
			node.update();
	}
	
	private void sort()
	{
		children.sort( (n1, n2) -> {
			return Integer.compare( priorityOf( n1 ) , priorityOf( n2 ) );
		} );
	}
	
	public boolean initialize( GL3 gl, CompileStatus status )
	{
		for( Node node : children )
		{
			if ( !node.initialize( gl, status ) )
				return false;
		}
		return true;
	}
	
	public void render( GL3 gl )
	{
		for( Node node : children )
			node.render( gl );
	}
	
	public void dispose( GL3 gl )
	{
		for( Node node : children )
			node.dispose( gl );
	}
	
	public Node parent() 
	{
		return parent;
	}
	
	public void setParent( Node node ) 
	{
		parent = node;
	}
	
	public Node findAbove( Class<?> type )
	{
		if( parent == null || parent.getClass().equals( type ) ) return parent;

		for( Node sibling : parent.children() )
		{
			if( sibling.getClass().equals( type ) )
			{
				return sibling;
			}
		}
		
		return parent.findAbove( type );
	}
	
	public Node findBelow( Class<?> type )
	{
		if( getClass().equals( type ) ) return this;

		for( Node sibling : parent.children() )
		{
			if( sibling.getClass().equals( type ) )
			{
				return sibling;
			}
		}
		
		return parent.findAbove( type );
	}
	
	private static int priorityOf( Node node )
	{
		if( node instanceof Shader ) return -2;
		if( node instanceof ShaderProgram ) return -1;
		if( node instanceof Material ) return 1;
		if( node instanceof VertexBuffer ) return -3;
		if( node instanceof ElementBuffer ) return 2;
		if( node instanceof Model ) return 3;
		if( node instanceof BodyGroup ) return 4;
		if( node instanceof UniformList ) return 0;
		return 100;
	}
}
