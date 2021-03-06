package swb.dynamic;

import com.jogamp.opengl.GL3;

import swb.ElementBuffer;
import swb.GLDataType;
import swb.GLNode;
import swb.ITexture;
import swb.Material;
import swb.ModelUtils;
import swb.ShaderProgram;
import swb.UniformList;
import swb.VertexBuffer;

public class DynamicPlane extends GLNode
{
	private float[] vertexArray;
	private VertexBuffer vertices;
	private ElementBuffer elements;
	
	public DynamicPlane( String name, int width, int height ) 
	{
		super( name );
		
		int size = width * height;
		vertexArray = new float[ size * 8 ];
		for( int i = 0, vIdx = 0; i < size; i++ )
		{
			float x = (float)(i % width) / (width - 1) - 0.5f;
			float z = (float)(i / width) / (height - 1) - 0.5f;
			vertexArray[vIdx++] = x;
			vertexArray[vIdx++] = 0.0f;
			vertexArray[vIdx++] = z;
			vertexArray[vIdx++] = 0.0f;
			vertexArray[vIdx++] = 1.0f;
			vertexArray[vIdx++] = 0.0f;
			vertexArray[vIdx++] = 0.0f;
			vertexArray[vIdx++] = 0.0f;
		}
		
		ShaderProgram program = ShaderProgram.generateProgram( "dynamicPlane" );
		UniformList uniforms = new UniformList( "uniforms" );
		uniforms.add( "dif_texture", GLDataType.SAMP2D, "0" );
		uniforms.add( "timeScale", GLDataType.VEC1, String.valueOf( Math.PI * 4.0 ) );
		uniforms.add( "frequency", GLDataType.VEC1, "100.0" );
		uniforms.add( "amplitude", GLDataType.VEC1, "0.01" );
		
		add( program );
		add( uniforms );
		
		Material material = new Material( "default" );
		material.addTexture( ITexture.loadTexture( Material.DEFAULT_DIF ), GL3.GL_TEXTURE0 );
		
		vertices = new VertexBuffer( vertexArray, 8 );
		elements = new ElementBuffer( ModelUtils.generateSurfaceElements( width, height, ModelUtils.NO_WRAP ) );
		add( vertices );
		vertices.add( material );
		vertices.add( elements );
		
		//vertices.transformBy( new mat4x4( new vec3f(), new vec3f( 100.0f, 100.0f, 100.0f ) ), 0 );
	}
	
	public void render( GL3 gl )
	{
		//vertices.bind( gl );
		//gl.glBufferSubData( GL3.GL_ARRAY_BUFFER, 0, vertexArray.length * Float.BYTES, vBuffer );
	}
}
