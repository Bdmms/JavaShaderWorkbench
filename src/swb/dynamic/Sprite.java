package swb.dynamic;

import com.jogamp.opengl.GL3;

import swb.Camera;
import swb.GLNode;
import swb.Renderer;
import swb.ShaderProgram;
import swb.VertexBufferDirect;
import swb.math.mat4x4;
import swb.math.vec3f;

public class Sprite extends VertexBufferDirect
{
	private static final float[] VERTS =  
	{
			1.0f, 1.0f, 0.0f, 	-1.0f, 1.0f, 0.0f, 	1.0f, -1.0f, 0.0f, 	
			-1.0f, 1.0f, 0.0f, 	1.0f, -1.0f, 0.0f, 	-1.0f, -1.0f, 0.0f
	};
	
	private Camera camera;
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
		vec3f unit = camera.position.unit();
		model.setQuaternionRotation( unit );
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
