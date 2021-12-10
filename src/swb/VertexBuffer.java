package swb;

import java.util.Arrays;

import javax.swing.JPopupMenu;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import swb.dialog.TransformDialog;
import swb.editors.EditorView;
import swb.editors.VertexTable;
import swb.math.mat4x4;
import swb.math.vec2f;
import swb.math.vec3f;
import swb.math.vec4f;
import swb.math.vecf;

/**
 * Stores the vertices in a buffer.
 */
public class VertexBuffer extends GLNode
{
	public final static String TAG = "vertices";
	
	VertexAttribute _linkedAttribute = null;
	
	protected float[] _buffer;
	protected int _size = 0;
	protected int _stride = 0;
	
	public int[] vbo = { -1 }; // Vertex buffer
	public int[] vao = { -1 }; // Vertex array
	
	public VertexBuffer( int capacity, int stride ) 
	{ 
		super( TAG, ID_ARRAY | ID_BUFFER, true );
		_buffer = new float[ capacity * stride ];
		_stride = stride;
		_size = 0;
	}
	
	public VertexBuffer( float[] buffer, int stride )
	{
		super( TAG, ID_ARRAY | ID_BUFFER, true );
		_buffer = buffer;
		_stride = stride;
		_size = buffer.length / stride;
	}
	
	public VertexBuffer( String name, float[] buffer, int stride )
	{
		super( name, ID_ARRAY | ID_BUFFER, true );
		_buffer = buffer;
		_stride = stride;
		_size = buffer.length / stride;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new VertexTable( getPath(), this );
	}
	
	public void setVertexAttribute( VertexAttribute atr )
	{
		_linkedAttribute = atr;
	}
	
	public void resize( int size )
	{
		float[] data = new float[size];
		for( int i = 0; i < _buffer.length && i < size; i++ )
			data[i] = _buffer[i];
		_buffer = data;
		_size = _buffer.length / _stride;
		modifyFlag = true;
		deleteFlag = true;
	}
	
	public void add( float ... vertex )
	{
		if( vertex.length % _stride != 0 )
		{
			System.err.println( "Error - Invalid vertex" );
			return;
		}
		
		int index = _buffer.length;
		resize( index + vertex.length );
		
		for( int i = 0; i < vertex.length; i++ )
			_buffer[ index + i ] = vertex[i];
	}
	
	public float get( int vertIdx, int element )
	{
		return _buffer[ vertIdx * _stride + element ];
	}
	
	public void set( int vertIdx, int element, float value )
	{
		_buffer[ vertIdx * _stride + element ] = value;
		modifyFlag = true;
	}
	
	public void flipNormals( int atr )
	{
		for( int i = 0; i < _buffer.length; i += _stride )
		{
			int j = i + atr;
			_buffer[j++] *= -1.0f;
			_buffer[j++] *= -1.0f;
			_buffer[j++] *= -1.0f;
		}
		modifyFlag = true;
	}
	
	public void calculateNormals( int pAtr, int nAtr )
	{
		//TODO:
		modifyFlag = true;
	}
	
	public void transformBy( mat4x4 transform, int atr )
	{
		for( int i = 0; i < _buffer.length; i += _stride )
		{
			transform.transform3f( _buffer, i + atr );
		}
		modifyFlag = true;
	}
	
	@Override
	public boolean build( Renderer renderer )
	{
		compileFlag = true;
		ShaderProgram program = (ShaderProgram)renderer.instances[LAST_PROGRAM];
		
		if( program == null )
		{
			System.err.println( "Error - Shader Program not defined" );
			return false;
		}
		
		if( program.compileFlag ) return true;
		
		ShaderCode vShader = program.getShaderComponent( GL3.GL_VERTEX_SHADER );
		if( vShader == null )
		{
			System.err.println( "Error - Vertex Shader not defined" );
			return false;
		}
		
		System.out.println( "Building: " + getPath() );
		
		_linkedAttribute = VertexAttribute.parse( vShader.getCode() );
		if( _linkedAttribute == null || !_linkedAttribute.isCompatibleWith( _stride ) )
		{
			System.err.println( "Error - Incompatible attributes!" );
			return false;
		}
		
		_linkedAttribute.print();
		return true;
	}
	
	@Override
	public void bind( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
		gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
	}
	
	@Override
	public void update( GL3 gl )
	{
		if( deleteFlag ) delete( gl );
		
		if( !initFlag )
		{
			System.out.println( "Initializing: " + getPath() + " (" + length() + " vertices)" );
			gl.glGenVertexArrays( 1, vao, 0 );
			gl.glGenBuffers( 1, vbo, 0 );
		
			gl.glBindVertexArray( vao[0] );
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
			gl.glBufferData( GL3.GL_ARRAY_BUFFER, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ), GL3.GL_STATIC_DRAW );
			
			_linkedAttribute.bind( gl );
		}
		else if( modifyFlag )
		{
			System.out.println( "Uploading: " + getPath() + " (" + length() + " vertices)" );
			gl.glBindVertexArray( vao[0] );
			gl.glBindBuffer( GL3.GL_ARRAY_BUFFER, vbo[0] );
			gl.glBufferSubData( GL3.GL_ARRAY_BUFFER, 0, _buffer.length * Float.BYTES, Buffers.newDirectFloatBuffer( _buffer ) );
		}
		
		super.update( gl );
	}
	
	@Override
	public void render( GL3 gl )
	{
		gl.glBindVertexArray( vao[0] );
	}
	
	@Override
	public void dispose( GL3 gl )
	{
		bind( gl );
		super.dispose( gl );
		delete( gl );
	}
	
	private void delete( GL3 gl )
	{
		System.out.println( "Deleting: " + getPath() );
		gl.glDeleteBuffers( 1, Buffers.newDirectIntBuffer( vbo ) );
		gl.glDeleteVertexArrays( 1, Buffers.newDirectIntBuffer( vao ) );
		initFlag = false;
		deleteFlag = false;
	}
	
	public int stride() 
	{
		return _stride;
	}
	
	public int length()
	{
		return _size;
	}
	
	public int capacity()
	{
		return _buffer.length;
	}
	
	public void print() 
	{
		// Number of vertices
		int size = length();
		
		System.out.println( size + " vertices (" + stride() + ")" );
		for( int i = 0; i < size; i++ )
		{
			int offset = i * _stride;
			String[] parts = new String[ _stride ];
			
			for( int e = 0; e < _stride; e++ )
				parts[e] = String.valueOf( _buffer[offset + e] );
			
			System.out.println( "v" + i + ": " + String.join( ", ", parts ) );
		}
	}
	
	@Override
	public void populate( JPopupMenu menu ) 
	{
		menu.add( Workbench.createMenuItem( "Flip Normals", e -> flipNormals( 3 ) ) );
		menu.add( Workbench.createMenuItem( "Transform", e -> 
		{
			mat4x4 mat = TransformDialog.openTransformDialog();
			if( mat != null ) 
			{
				transformBy( mat, 0 );
				parent.childEventNotify( this );
			}
			
		} ) );
	}
	
	/**
	 * Wraps a set of vectors around a single buffer. The size of
	 * each vector may not be uniform.
	 * @param buffer - source buffer
	 * @param atr - attribute list that specifies the size of each vector
	 * @return an array of vectors wrapped around the buffer
	 */
	public static vecf[][] wrap( float[] buffer, int[] atr )
	{
		int stride = Arrays.stream( atr ).sum();
		vecf[][] vectors = new vecf[buffer.length / stride][atr.length];
		
		for( int i = 0; i < vectors.length; i++ )
		{
			vecf[] vertex = vectors[i];
			int idx = i * stride;
			
			for( int j = 0; j < atr.length; j++ )
			{
				switch( atr[j] )
				{
				case 2:  vertex[j] = new vec2f( buffer, idx ); break;
				case 3:	 vertex[j] = new vec3f( buffer, idx ); break;
				case 4:	 vertex[j] = new vec4f( buffer, idx ); break;
				default: vertex[j] = new vecf( buffer, idx, atr[j] ); break;
				}
				idx += atr[j];
			}
		}
		
		return vectors;
	}
}
 