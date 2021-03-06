package swb.editors;

import javax.swing.JComponent;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;

import swb.Animation;
import swb.GLDataType;
import swb.GLNode;
import swb.ShaderCode;
import swb.ShaderProgram;
import swb.VertexAttribute;
import swb.VertexBuffer;
import swb.math.Transform;

public class AnimationViewer extends GLJPanel implements GLEventListener, EditorView
{
	private static final long serialVersionUID = 1L;

	private Animation animation;
	
	private ShaderProgram shader;
	private VertexBuffer boneMesh;
	
	private long time = System.currentTimeMillis();
	private float deltaTime;
	private float frame;
	
	private int modelLoc;
	private float[] model = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
	
	public AnimationViewer( Animation animation, GLCapabilities capabilities )
	{
		super( capabilities );
		setName( animation.getPath() );
		
		this.animation = animation;
		
		shader = ShaderProgram.generateProgram( "bones" );
		boneMesh = new VertexBuffer( BONE_MESH, 3 );
		
		VertexAttribute atr = new VertexAttribute();
		atr.add( 0, GLDataType.VEC3 );
		boneMesh.setVertexAttribute( atr );
		
		frame = animation.startFrame;
		
		addGLEventListener( this );
	}
	
	@Override
	public GLNode getModelSource() 
	{
		return animation;
	}

	@Override
	public JComponent createView() 
	{
		return this;
	}

	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		shader.render( gl );
		
		// Update bones
		gl.glBindVertexArray( boneMesh.vao[0] );
		animation.interpolate( frame );
		
		for( int i = 0; i < animation.skeleton.size; i++ )
		{
			//int parent = animation.skeleton.parent[i];
			//vec3f rotation = parent != -1 ? animation.skeleton.rotation[parent] : animation.skeleton.rotation[i];
			
			Transform.setMatrix( model, 0, animation.skeleton.position[i], animation.skeleton.rotation[i] );
			gl.glUniformMatrix4fv( modelLoc, 1, true, model, 0 );
			gl.glDrawArrays( GL3.GL_TRIANGLES, 0, boneMesh.length() );
		}
		
		gl.glBindVertexArray( 0 );
		
		// Update time
		long next = System.currentTimeMillis();
		deltaTime = 0.5f * (next - time) / 1000.0f;
		frame = (frame + deltaTime * 60.0f) % animation.endFrame;
		time = next;
	}

	@Override
	public void dispose( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		shader.dispose( gl );
		boneMesh.dispose( gl );
	}

	@Override
	public void init( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		if( !gl.isExtensionAvailable("GL_ARB_explicit_attrib_location") )
			System.err.println( "MISSING EXTENSION" );
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		
		gl.glEnable( GL3.GL_TEXTURE_2D );
		gl.glEnable( GL3.GL_DEPTH_TEST );
		
		gl.glDisable( GL3.GL_BLEND );
		gl.glDepthFunc( GL3.GL_LEQUAL );
		
		ShaderCode.compileShaders( gl );
		
		shader.compile( gl );
		boneMesh.update( gl );
		modelLoc = gl.glGetUniformLocation( shader.getID(), "model" );
		
		// Set framerate
		setAnimator( new FPSAnimator( g, 60, true ) );
		getAnimator().start();
	}

	@Override
	public void reshape(GLAutoDrawable g, int x, int y, int width, int height) 
	{
		final GL3 gl = g.getGL().getGL3();
		gl.glViewport( 0, 0, width, height );
	}
	
	float[] BONE_MESH = 
	{
		0.5f, -0.05f, -0.05f, 	0.5f,  0.05f, -0.05f, 	0.0f,  0.00f,  0.00f,
		0.5f,  0.05f, -0.05f, 	0.5f,  0.05f,  0.05f, 	0.0f,  0.00f,  0.00f,
		0.5f,  0.05f,  0.05f, 	0.5f, -0.05f,  0.05f, 	0.0f,  0.00f,  0.00f,
		0.5f, -0.05f,  0.05f, 	0.5f, -0.05f, -0.05f, 	0.0f,  0.00f,  0.00f,
		
		1.0f,  0.00f,  0.00f, 	0.5f,  0.05f, -0.05f, 	0.5f, -0.05f, -0.05f,
		1.0f,  0.00f,  0.00f, 	0.5f,  0.05f,  0.05f, 	0.5f,  0.05f, -0.05f,
		1.0f,  0.00f,  0.00f, 	0.5f, -0.05f,  0.05f, 	0.5f,  0.05f,  0.05f,
		1.0f,  0.00f,  0.00f, 	0.5f, -0.05f, -0.05f, 	0.5f, -0.05f,  0.05f,
	};
}
