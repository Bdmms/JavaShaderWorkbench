package swb;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.math.FloatUtil;

import swb.editors.EditorView;
import swb.editors.VectorDrawingEditor;
import swb.math.vec2f;
import swb.math.vec2i;
import swb.math.vec3i;
import swb.math.vec4f;

public class VectorDrawing extends GLNode
{
	private DynamicVertexBuffer<TextureVertex> vertices;
	private List<vec2i> lines = new ArrayList<>();
	
	public VectorDrawing(String name) 
	{
		super( name );
		vertices = new DynamicVertexBuffer<>( 1000, 6 );
	}
	
	public int addVertex( float x, float y )
	{
		vertices.addVertex( new TextureVertex( vertices.getData(), vertices.getOffset(), x, -y ) );
		return vertices.size() - 1;
	}
	
	public int addLine( vec2i line )
	{
		lines.add( line );
		return lines.size() - 1;
	}
	
	public int getSelectedVertex( float x, float y, float radius )
	{
		int size = vertices.size();
		y = -y;
		radius *= radius;
		
		for( int i = 0; i < size; i++ )
		{
			TextureVertex vertex = vertices.getVertex( i );
			
			if( vertex.position.dot( x, y ) <= radius )
				return i;
		}
		
		return -1;
	}
	
	public void setPosition( int vIdx, float x, float y )
	{
		TextureVertex vertex = vertices.getVertex( vIdx );
		vertex.position.set( x, -y );
		vertex.modified = true;
	}
	
	public void setColor( int vIdx, float x, float y )
	{
		TextureVertex vertex = vertices.getVertex( vIdx );
		float dx = x - vertex.position.getX();
		float dy = y - vertex.position.getY();
		
		float angle = FloatUtil.atan2(dy, dx);
		int col = (int)(angle * 0xFFFFFF);
		float r = ((col >> 16) & 0xFF) / 255.0f;
		float g = ((col >> 8) & 0xFF) / 255.0f;
		float b = (col & 0xFF) / 255.0f;
		
		vertex.color.set( r, g, b );
		vertex.modified = true;
	}
	
	public void setSelected( int vIdx, boolean state )
	{
		TextureVertex vertex = vertices.getVertex( vIdx );
		if( state != vertex.selected ) 
		{
			vertex.modified = true;
			vertex.selected = state;
		}
	}
	
	public DynamicVertexBuffer<TextureVertex> getVertexBuffer()
	{
		return vertices;
	}
	
	public List<vec2i> getLines()
	{
		return lines;
	}
	
	public List<vec3i> compileTriangles()
	{
		int[] unique = new int[6];
		
		List<vec3i> faces = new ArrayList<>();
		for( int i = 0; i < lines.size() - 2; i++ )
		{
			vec2i l0 = lines.get( i );
			unique[0] = l0.getX();
			unique[1] = l0.getY();
			
			for( int j = i + 1; j < lines.size() - 1; j++ )
			{
				int c2x = 2;
				vec2i l1 = lines.get( j );
				if( !isContained( unique, l1.getX(), c2x ) ) unique[c2x++] = l1.getX();
				if( !isContained( unique, l1.getY(), c2x ) ) unique[c2x++] = l1.getY();
				if( c2x > 3 ) continue;
				
				for( int k = j + 1; k < lines.size(); k++ )
				{
					int c3x = c2x;
					vec2i l2 = lines.get( k );
					if( !isContained( unique, l2.getX(), c2x ) ) c3x++;
					if( !isContained( unique, l2.getY(), c2x ) ) c3x++;
					
					if( c3x == 3 )
						faces.add( new vec3i( unique[0], unique[1], unique[2] ) );
				}
			}
		}
		
		return faces;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new VectorDrawingEditor( getPath(), this, Workbench.capabilities );
	}
	
	@Override
	public void saveTo( File file, String format ) throws IOException
	{
		FileWriter writer = new FileWriter( file );
		
		writer.write( vertices.size() );
		writer.write( '\n' );
		writer.write( vertices.toString() );
		writer.write( '\n' );
		
		writer.write( lines.size() );
		writer.write( '\n' );
		
		for( vec2i line : lines )
		{
			writer.write( line.toString() );
			writer.write( '\n' );
		}
		
		writer.close();
	}
	
	private static boolean isContained( int[] arr, int value, int sz )
	{
		for( int i = 0; i < sz; i++ )
			if( arr[i] == value )
				return true;
		return false;
	}
	
	private static class TextureVertex extends Vertex
	{
		public vec2f position;
		public vec4f color;
		public boolean selected = false;
		public boolean modified = true;
		
		public TextureVertex( float[] buffer, int offset, float x, float y  )
		{
			super( buffer, offset, 6 );
			position = new vec2f( data, offset );
			color = new vec4f( data, offset + 2 );
			data[offset++] = x;
			data[offset++] = y;
			data[offset++] = 1.0f;
			data[offset++] = 1.0f;
			data[offset++] = 1.0f;
			data[offset++] = 1.0f;
		}

		@Override
		public void copyTo( float[] buffer, int idx )
		{
			super.copyTo( buffer, idx );
			modified = false;
		}

		@Override
		public boolean isModified() 
		{
			return modified;
		}
	}
}
