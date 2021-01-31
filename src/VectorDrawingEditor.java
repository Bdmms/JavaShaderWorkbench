import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;

import math.vec2i;

public class VectorDrawingEditor extends GLJPanel implements GLEventListener, 
	EditorView, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private VectorDrawing drawing;
	
	// Overlay
	private Texture textureQueue = null;
	private Texture overlay;
	private ShaderProgram overlayShader;
	private VertexBuffer overlayVertices;
	private ElementBuffer overlayElements;
	private UniformLocation overlayLoc = new UniformLocation();
	
	// Shader
	private ShaderProgram shader;
	private VertexBuffer vertexBuffer;
	private LineBuffer lines;
	private ElementBuffer tris;
	private UniformLocation shaderLoc = new UniformLocation();
	
	// Window Parameters
	private int width = 1;
	private int height = 1;
	
	// Transformations
	private float zoom = 1.0f;
	private float aspectRatio = 1.0f;
	private float imageRatio = 1.0f;
	private float[] position = new float[2];
	private float[] scale = new float[2];
	
	// Mouse Variables
	private float[] anchor = new float[2];
	private int[] mouseLocation = new int[2];
	private int heldButton = -1;
	
	// Flags
	private boolean recompileVerts = false;
	private boolean recompileLines = false;
	private boolean recompileTris = false;
	private boolean showVertices = true;
	private boolean showLines = true;
	private boolean showTris = true;
	private boolean showOverlay = true;
	
	private int lastVertexID = -1;
	private int selectedVertex = -1;
	
	public VectorDrawingEditor( String title, VectorDrawing drawing, GLCapabilities capabilities )
	{
		super( capabilities );
		setName( title );
		this.drawing = drawing;
		
		addKeyListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
		addMouseWheelListener( this );
		addGLEventListener( this );
		
		position[0] = 0.0f;
		position[1] = 0.0f;
		scale[0] = 1.0f;
		scale[1] = 1.0f;
	}
	
	@Override
	public void display( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		scale[0] = imageRatio * zoom;
		scale[1] = -aspectRatio * zoom;
		
		if( textureQueue != null )
		{
			if( overlay != null ) overlay.delete( gl );
			overlay = textureQueue;
			overlay.load( gl );
			imageRatio = (float) overlay.getImage().getWidth() / overlay.getImage().getHeight();
			scale[0] = imageRatio;
			textureQueue = null;
		}
		
		gl.glClear( GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT );
		
		if( overlay != null && showOverlay )
		{
			gl.glUseProgram( overlayShader.getID() );
			overlayLoc.upload( gl );
			gl.glActiveTexture( GL3.GL_TEXTURE0 );
			gl.glBindTexture( GL3.GL_TEXTURE_2D, overlay.textureID[0] );
			gl.glBindVertexArray( overlayVertices.vao[0] );
			overlayElements.render( gl );
		}
		
		gl.glUseProgram( shader.getID() );
		shaderLoc.upload( gl );
		
		if( recompileVerts ) { vertexBuffer.update( gl, drawing.getVertices() ); recompileVerts = false; }
		if( recompileLines ) { lines.update( gl, drawing.getLines() ); recompileLines = false; }
		if( recompileTris ) { tris.upload( gl ); recompileTris = false; }
		
		gl.glBindVertexArray( vertexBuffer.vao[0] );
		if( showVertices ) 	gl.glDrawArrays( GL.GL_POINTS, 0, vertexBuffer.size() );
		if( showLines )		lines.render( gl );
		if( showTris )		tris.render( gl );
		gl.glBindVertexArray( 0 );
		
		int error = gl.glGetError();
		if( error != 0 )
			System.err.println( "Error on render: " + error );
	}

	@Override
	public void dispose( GLAutoDrawable g ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		overlayShader.dispose( gl );
		overlayVertices.dispose( gl );
		overlayElements.dispose( gl );
		
		shader.dispose( gl );
		vertexBuffer.dispose( gl );
		lines.dispose( gl );
		tris.dispose( gl );
		
		if( overlay != null ) overlay.delete( gl );
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
		//gl.glEnable( GL3.GL_BLEND );
		gl.glDisable( GL3.GL_DEPTH_TEST );
		
		overlayShader = ShaderProgram.createFrom( "overlay", new File( "src\\svg\\overlay.vs" ), new File( "src\\svg\\overlay.fs" ) );
		overlayVertices = new VertexBuffer( new float[] {  0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f }, 2 );
		overlayElements = new ElementBuffer( new int[] { 0, 1, 2, 1, 2, 3 } );
		
		overlayShader.compile( gl );
		overlayVertices.compile( gl );
		overlayElements.compile( gl );
		
		overlayShader.upload( gl );
		overlayLoc.compile( gl, overlayShader );
		
		shader = ShaderProgram.createFrom( "shader", new File( "src\\svg\\editorShader.vs" ), new File( "src\\svg\\editorShader.fs" ) );
		vertexBuffer = new VertexBuffer( 1000, 6 );
		lines = new LineBuffer( 1000 );
		tris = new ElementBuffer( new int[0] );
		
		shader.compile( gl );
		vertexBuffer.compile( gl );
		shaderLoc.compile( gl, shader );
		vertexBuffer.upload( gl );
		
		lines.upload( gl );
		tris.upload( gl );
	}

	@Override
	public void reshape( GLAutoDrawable g, int x, int y, int width, int height ) 
	{
		final GL3 gl = g.getGL().getGL3();
		
		this.width = width;
		this.height = height;
		aspectRatio = (float) width / height;
		
		gl.glViewport( 0, 0, width, height );
	}

	@Override
	public JComponent createView() 
	{
		return this;
	}
	
	@Override 
	public LeafNode getModelSource()
	{
		return drawing;
	}
	
	public void loadOverlay()
	{
		JFileChooser fc = new JFileChooser();
		int resp = fc.showOpenDialog( this );
		
		if( resp == JFileChooser.APPROVE_OPTION )
		{
			try {
				BufferedImage image = ImageIO.read( fc.getSelectedFile() );
				textureQueue = new Texture( fc.getSelectedFile().getName(), image );
			} catch (IOException e1) { System.err.println( "Error - Cannot load file!" );}
		}
	}
	
	public void compileTriangles()
	{
		tris.replaceWith( drawing.compileTriangles() );
		recompileTris = true;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		float value = (float)e.getPreciseWheelRotation() * 0.25f;
		
		if( value > 0.0f && zoom < 0.5f ) return;
		
		anchor[0] = position[0] += value * ((2.0f * e.getX() / width - 1.0f) - position[0]) / zoom;
		anchor[1] = position[1] -= value * ((2.0f * e.getY() / height - 1.0f) + position[1]) / zoom;
		zoom -= value;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		mouseLocation[0] = e.getX();
		mouseLocation[1] = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
	
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if( heldButton == MouseEvent.BUTTON1 )
		{
			if( selectedVertex < 0 )
			{
				int pivotX = e.getX() - mouseLocation[0];
				int pivotY = e.getY() - mouseLocation[1];
				position[0] = anchor[0] + 2.0f * pivotX / width;
				position[1] = anchor[1] - 2.0f * pivotY / height;
			}
			else
			{
				float x = ((2.0f * e.getX() / width - 1.0f) - position[0]) / scale[0];
				float y = ((2.0f * e.getY() / height - 1.0f) + position[1]) / scale[1];
				drawing.setPosition( selectedVertex, x, y );
				recompileVerts = true;
			}
			repaint();
		}
		else if( heldButton == MouseEvent.BUTTON2 && selectedVertex > -1 )
		{
			float x = ((2.0f * e.getX() / width - 1.0f) - position[0]) / scale[0];
			float y = ((2.0f * e.getY() / height - 1.0f) + position[1]) / scale[1];
			drawing.setColor( selectedVertex, x, y );
			recompileVerts = true;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if( selectedVertex > -1 )
		{
			drawing.setSelected( selectedVertex, false );
			recompileVerts = true;
		}
		
		position[0] = anchor[0];
		position[1] = anchor[1];
		mouseLocation[0] = e.getX();
		mouseLocation[1] = e.getY();
		
		float x = ((2.0f * e.getX() / width - 1.0f) - position[0]) / scale[0];
		float y = ((2.0f * e.getY() / height - 1.0f) + position[1]) / scale[1];
		selectedVertex = drawing.getSelectedVertex( x, y, 0.025f / zoom );
		heldButton = e.getButton();
		
		if( heldButton == MouseEvent.BUTTON1 )
		{
			if( selectedVertex > -1 ) drawing.setSelected( selectedVertex, true );
		}
		if( heldButton == MouseEvent.BUTTON3 )
		{
			if( selectedVertex > -1 )
			{
				drawing.setSelected( selectedVertex, true );
				
				if( lastVertexID != -1 && lastVertexID != selectedVertex )
				{
					drawing.addLine( new vec2i( lastVertexID, selectedVertex ) );
					recompileLines = true;
				}
				
				lastVertexID = selectedVertex;
			}
			else
			{
				int nextIndex = drawing.addVertex( x, y );
				
				if( lastVertexID != -1 ) 
				{
					drawing.addLine( new vec2i( lastVertexID, nextIndex ) );
					recompileLines = true;
				}
				
				lastVertexID = nextIndex;
			}
			
			recompileVerts = true;
		}
		
		requestFocusInWindow();
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		anchor[0] = position[0];
		anchor[1] = position[1];
		heldButton = -1;
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		switch( e.getKeyCode() )
		{
		case KeyEvent.VK_L:	loadOverlay();					repaint();	break;
		case KeyEvent.VK_K:	compileTriangles();				repaint();	break;
		case KeyEvent.VK_1: showVertices = !showVertices; 	repaint();	break;
		case KeyEvent.VK_2: showLines = !showLines; 		repaint();	break;
		case KeyEvent.VK_3: showTris = !showTris; 			repaint();	break;
		case KeyEvent.VK_4: showOverlay = !showOverlay; 	repaint();	break;
		
		case KeyEvent.VK_ESCAPE: 	lastVertexID = -1;		break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }
	
	private class UniformLocation
	{
		private int positionLoc = 0;
		private int scaleLoc = 0;
		private int textureLoc = 0;
		
		public void compile( GL3 gl, ShaderProgram shader )
		{
			gl.glUseProgram( shader.getID() );
			positionLoc = gl.glGetUniformLocation( shader.getID(), "location" );
			scaleLoc = gl.glGetUniformLocation( shader.getID(), "scale" );
			textureLoc = gl.glGetUniformLocation( shader.getID(), "diffuse" );
			
			gl.glUniform1i( textureLoc, 0 );
			gl.glUniform2fv( positionLoc, 1, position, 0 );
			gl.glUniform2fv( scaleLoc, 1, scale, 0 );
		}
		
		public void upload( GL3 gl )
		{
			gl.glUniform2fv( positionLoc, 1, position, 0 );
			gl.glUniform2fv( scaleLoc, 1, scale, 0 );
		}
	}
	
	private class LineBuffer
	{
		public int[] ebo = new int[1];
		public int[] buffer;
		public int size;
		
		public LineBuffer( int capacity )
		{
			buffer = new int[ capacity * 2 ];
			size = 0;
		}
		
		public void update( GL3 gl, List<vec2i> lines )
		{
			// No changes
			if( size == lines.size() ) return;
			int currentCapacity = lines.size() * 2;
			
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			
			if( buffer.length < currentCapacity )
			{
				buffer = new int[ buffer.length * 2 ];
				int idx = 0;
				for( vec2i line : lines )
				{
					buffer[idx++] = line.x;
					buffer[idx++] = line.y;
				}
				
				size = lines.size();
				gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( buffer ), GL3.GL_STATIC_DRAW );
			}
			else
			{
				// Add new lines
				int difference = (lines.size() - size) * 2;
				int index = size * 2;
				int idx = index;
				for( int i = size; i < lines.size(); i++ )
				{
					buffer[idx++] = lines.get( i ).x;
					buffer[idx++] = lines.get( i ).y;
				}
				
				size = lines.size();
				gl.glBufferSubData( GL3.GL_ELEMENT_ARRAY_BUFFER, index * Integer.BYTES, difference * Integer.BYTES, Buffers.newDirectIntBuffer( buffer, index ) );
			}
		}
		
		public void upload( GL3 gl )
		{
			gl.glGenBuffers( 1, ebo, 0 );
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glBufferData( GL3.GL_ELEMENT_ARRAY_BUFFER, buffer.length * Integer.BYTES, Buffers.newDirectIntBuffer( buffer ), GL3.GL_STATIC_DRAW );
		}
		
		public void render( GL3 gl )
		{
			gl.glBindBuffer( GL3.GL_ELEMENT_ARRAY_BUFFER, ebo[0] );
			gl.glDrawElements( GL3.GL_LINES, size * 2, GL3.GL_UNSIGNED_INT, 0 );
		}
		
		public void dispose( GL3 gl )
		{
			gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( ebo ) );
		}
	}
}
