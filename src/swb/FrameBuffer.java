package swb;

import javax.swing.ImageIcon;

import com.jogamp.opengl.GL3;

/**
 * TODO: In theory, this could be implemented, but it conflicts
 * with to many other components at the current moment
 */
public class FrameBuffer extends ITexture
{
	private int[] attachments;
	private int[] frameID = new int[1];
	private int[] renderID = new int[1];
	private int width;
	private int height;
	
	public FrameBuffer( int[] attachments ) 
	{
		super( "Frame0", GL3.GL_TEXTURE_2D, attachments.length );
		
		this.attachments = attachments;
		this.width = 0;
		this.height = 0;
	}
	
	public void load( GL3 gl )
	{
		delete( gl );
		
		// Frame buffer
		gl.glGenFramebuffers( 1, frameID, 0 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, frameID[0] );
		loaded = true;
		
		// Textures
		gl.glGenTextures( attachments.length, textureID, 0 );
		
		for ( int i = 0; i < attachments.length; i++ )
		{
			gl.glBindTexture( GL3.GL_TEXTURE_2D, textureID[i] );
			gl.glTexImage2D( GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA16F, width, height, 0, GL3.GL_RGBA, GL3.GL_FLOAT, null );
			gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
			gl.glTexParameteri( GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
			gl.glFramebufferTexture2D( GL3.GL_FRAMEBUFFER, attachments[i], GL3.GL_TEXTURE_2D, textureID[i], 0);
		}
		
		gl.glDrawBuffers( attachments.length, attachments, 0 );
		
		// Render buffer
		gl.glGenRenderbuffers( 1, renderID, 0 );
		gl.glBindRenderbuffer( GL3.GL_RENDERBUFFER, renderID[0] );
		gl.glRenderbufferStorage( GL3.GL_RENDERBUFFER, GL3.GL_DEPTH24_STENCIL8, width, height );
		gl.glFramebufferRenderbuffer( GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_STENCIL_ATTACHMENT, GL3.GL_RENDERBUFFER, renderID[0] );

		if ( gl.glCheckFramebufferStatus( GL3.GL_FRAMEBUFFER) != GL3.GL_FRAMEBUFFER_COMPLETE )
		{
			System.err.println( "Error - Incomplete frame buffer!" );
			delete( gl );
		}

		gl.glBindRenderbuffer( GL3.GL_RENDERBUFFER, 0 );
		gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, 0 );
	}
	
	/*
	@Override
	public void bind( GL3 gl )
	{
		for ( int i = 0; i < attachments.length; i++ )
		{
			gl.glActiveTexture( GL3.GL_TEXTURE0 | i );
			gl.glBindTexture( GL3.GL_TEXTURE_2D, textureID[i] );
		}
	}*/

	public void delete( GL3 gl )
	{
		if( loaded ) return;
		
		gl.glDeleteFramebuffers( 1, frameID, 0 );
		loaded = false;
	}
	
	@Override
	public ImageIcon getIcon() 
	{
		return null;
	}
	
	public FrameSwap createFrameSwap( String name )
	{
		return new FrameSwap( name );
	}
	
	public class FrameSwap extends GLNode
	{
		public FrameSwap(String name) 
		{
			super( name, true );
		}
		
		@Override
		public void render( GL3 gl )
		{
			gl.glBindFramebuffer( GL3.GL_FRAMEBUFFER, frameID[0] );
			gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
			gl.glViewport( 0, 0, width, height );
		}
	}

	private static float[] VIEW_PLANE = 
	{
		-1.0f,  1.0f,  0.0f, 1.0f,
		-1.0f, -1.0f,  0.0f, 0.0f,
		 1.0f, -1.0f,  1.0f, 0.0f,
		-1.0f,  1.0f,  0.0f, 1.0f,
		1.0f, -1.0f,  1.0f, 0.0f,
		1.0f,  1.0f,  1.0f, 1.0f
	};
	private static final VertexBuffer VIEW = new VertexBuffer( VIEW_PLANE, 2 );
	
	public static void initialize( GL3 gl )
	{
		VIEW.update( gl );
	}
	
	public static void cleanup( GL3 gl )
	{
		VIEW.dispose( gl );
	}
}
