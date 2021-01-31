import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL3;

public class Node extends LeafNode
{
	private LinkedList<LeafNode> children = new LinkedList<>();
	
	public Node( String name )
	{
		super( name );
	}
	
	public List<LeafNode> children() 
	{
		return children;
	}

	public void add( LeafNode node ) 
	{
		if( node == null ) return;
		children.add( node );
		node.setParent( this );
	}
	
	public void sort()
	{
		children.sort( (n1, n2) -> Integer.compare( priorityOf( n1 ) , priorityOf( n2 ) ) );
	}
	
	@Override
	public boolean isCompiled()
	{
		boolean compiled = isCompiled;
		for( LeafNode node : children() )
		{
			compiled &= node.isCompiled();
		}
		
		return compiled;
	}
	
	@Override
	public void bind( GL3 gl )
	{
		for( LeafNode node : children )
			node.bind( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		for( LeafNode node : children )
			node.render( gl );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		for( LeafNode node : children )
			node.dispose( gl );
		super.dispose( gl );
	}
	
	/**
	 * All children are rebuilt if one child changes
	 */
	public boolean build( GL3 gl )
	{
		int idx = 0;
		for( LeafNode node : children )
		{
			if( !node.isCompiled() ) break;
			
			// If already compiled, then just bind the component
			node.bind( gl );
			idx++;
		}
		
		for( ; idx < children.size(); idx++ )
		{
			if ( !children.get( idx ).compile( gl ) )
				return false;
		}
		
		return true;
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		for( LeafNode node : children )
		{
			if ( !node.compile( gl ) )
				return false;
		}
		return super.compile( gl );
	}
	
	@Override
	public void upload( GL3 gl )
	{
		for( LeafNode node : children )
			node.upload( gl );
		super.upload( gl );
	}
	
	private static int priorityOf( LeafNode node )
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
