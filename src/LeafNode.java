
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL3;

public class LeafNode 
{
	protected String name;
	protected LeafNode parent = null;
	protected boolean isCompiled = false;
	protected boolean isLoaded = false;
	protected boolean isModified = false;
	
	public LeafNode( String name )
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
	
	public LeafNode parent() 
	{
		return parent;
	}
	
	public void setParent( LeafNode node ) 
	{
		parent = node;
	}
	
	public boolean isCompiled()
	{
		return isCompiled;
	}
	
	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	public boolean isModified()
	{
		return isModified;
	}
	
	public boolean build( GL3 gl )
	{
		return true;
	}
	
	public boolean compile( GL3 gl ) 
	{
		isCompiled = true;
		return true;
	}
	
	public void bind( GL3 gl )
	{
		
	}
	
	public void upload( GL3 gl ) 
	{
		isLoaded = true;
		isModified = false;
	}
	
	public void render( GL3 gl ) 	
	{ 
		
	}
	
	public void dispose( GL3 gl ) 	
	{ 
		isLoaded = false;
		isModified = false;
	}
	
	public void saveTo( File file, String format ) throws IOException { }
}
