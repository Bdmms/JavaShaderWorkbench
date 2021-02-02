package swb;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.jogamp.opengl.GL3;

import swb.editors.EditorView;

public class GLNode
{
	private LinkedList<GLNode> children = new LinkedList<>();
	protected String name;
	protected GLNode parent = null;
	protected boolean isCompiled = false;
	protected boolean isLoaded = false;
	protected boolean isModified = false;
	
	public GLNode( String name )
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
	
	public void setParent( GLNode node ) 
	{
		parent = node;
	}
	
	public String getPath()
	{
		return parent == null ? name : parent.getPath() + "\\" + name;
	}
	
	public GLNode parent() 
	{
		return parent;
	}
	
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}
	
	public List<GLNode> children() 
	{
		return children;
	}

	public void add( GLNode node ) 
	{
		if( node == null ) return;
		children.add( node );
		node.setParent( this );
	}
	
	public boolean isCompiled()
	{
		boolean compiled = isCompiled;
		for( GLNode node : children )
		{
			compiled &= node.isCompiled();
		}
		
		return compiled;
	}
	
	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	public boolean isModified()
	{
		return isModified;
	}
	
	/**
	 * All children are rebuilt if one child changes
	 */
	public boolean build( GL3 gl )
	{
		int idx = 0;
		for( GLNode node : children )
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
	
	public boolean compile( GL3 gl )
	{
		for( GLNode node : children )
		{
			if ( !node.compile( gl ) )
				return false;
		}
		
		isCompiled = true;
		return true;
	}
	
	public void upload( GL3 gl )
	{
		for( GLNode node : children )
			node.upload( gl );
		
		isLoaded = true;
		isModified = false;
	}
	
	public void bind( GL3 gl )
	{
		for( GLNode node : children )
			node.bind( gl );
	}
	
	public void render( GL3 gl )
	{
		for( GLNode node : children )
			node.render( gl );
	}
	
	public void dispose( GL3 gl )
	{
		for( GLNode node : children )
			node.dispose( gl );
		
		isLoaded = false;
		isModified = false;
	}
	
	public int size()
	{
		int size = 1;
		for( GLNode node : children )
			size += node.size();
		return size;
	}
	
	public int height()
	{
		int size = 1;
		GLNode current = this;
		while( current.hasChildren() )
			size++;
		return size;
			
	}
	
	public void saveTo( File file, String format ) throws IOException 
	{ 
		
	}
	
	public void iterate( Consumer<GLNode> consumer )
	{
		for( GLNode node : children )
			node.iterate( consumer );
		consumer.accept( this );
	}
}
