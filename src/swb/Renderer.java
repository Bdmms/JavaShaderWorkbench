package swb;

import java.util.ArrayList;

import com.jogamp.opengl.GL3;

public class Renderer extends ArrayList<GLNode>
{
	private static final long serialVersionUID = 1L;
	
	/** Instance list */
	public GLNode[] instances = new GLNode[GLNode.INSTANCE_CAPACITY];
	public ActiveCamera camera;
	
	public Renderer( ActiveCamera camera )
	{
		this.camera = camera;
	}
	
	/**
	 * Streamlines the tree into a linear list of instructions
	 * @param gl
	 * @param tree
	 */
	public boolean compile( GL3 gl, GLNode tree )
	{
		clear();
		gl.glUseProgram( 0 );
		
		// Stage 1: Check if tree structure will build
		for( GLNode node : tree )
		{
			if( !node.build( this ) )
				return false;
			
			// Add the node to the instance list
			for( byte i = 0; i < instances.length; i++ )
			{
				if( ((node.renderID >> i) & 0b1) == 0b1 )
				{
					instances[i] = node;
				}
			}
		}
		
		// Compile all shader code
		ShaderCode.compileShaders( gl );
		
		// Stage 2: Recompile the nodes in order
		for( GLNode node : tree )
		{
			if( !node.compile( gl ) ) 
				return false;
			node.bind( gl );
		}
		
		// Stage 3: Update the nodes and add them to the render list
		for( GLNode node : tree )
		{
			node.update( gl );
			if( node.renderFlag ) add( node );
		}
		
		// Upload al textures
		ITexture.uploadTextures( gl );
		
		System.out.println( String.format( "SUCCESS: %d nodes processed\n", tree.size() ) );
		return true;
	}
	
	/**
	 * Renders the compiled components
	 * @param gl
	 */
	public void render( GL3 gl )
	{
		for( GLNode node : this )
		{
			node.render( gl );
		}
	}
	
	@Override
	public void clear()
	{
		for( int i = 0; i < instances.length; i++ )
			instances[i] = null;
		super.clear();
	}
	
	@Override
	public String toString()
	{
		String text = "--- List of Renderable Components ---";
		for( GLNode node : this )
		{
			text = String.format( "%s\n%s", text, node.getPath() );
		}
		return text;
	}
}
