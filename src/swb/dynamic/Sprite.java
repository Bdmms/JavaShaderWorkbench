package swb.dynamic;

import com.jogamp.opengl.GL3;

import swb.ActiveCamera;
import swb.GLNode;
import swb.Renderer;
import swb.ShaderProgram;
import swb.VertexBufferDirect;
import swb.math.mat4x4;

public class Sprite extends VertexBufferDirect
{
	private static final float[] VERTS =  
	{
			1.0f, 1.0f, 0.0f, 	-1.0f, 1.0f, 0.0f, 	1.0f, -1.0f, 0.0f, 	
			-1.0f, 1.0f, 0.0f, 	1.0f, -1.0f, 0.0f, 	-1.0f, -1.0f, 0.0f
	};
	
	private ActiveCamera camera;
	private mat4x4 model = new mat4x4();
	private int modelLoc;
	
	public Sprite( String name ) 
	{
		super( name, VERTS );
	}
	
	@Override
	public boolean build( Renderer renderer )
	{
		camera = renderer.camera;
		return super.build(renderer);
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		int[] id = new int[1];
		gl.glGetIntegerv( GL3.GL_CURRENT_PROGRAM, id, 0 );
		modelLoc = gl.glGetUniformLocation( id[0], "model" );
		return super.compile( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		model.setQuaternionRotation( camera.position );
		model.upload( gl, modelLoc );
		super.render( gl );
	}

	public static GLNode generateSprite( String name )
	{
		GLNode node = new GLNode( name );
		node.add( ShaderProgram.generateProgram( "sprite" ) );
		node.add( new Sprite( "sprite" ) );
		return node;
	}
}
