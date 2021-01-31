import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import math.vec2f;
import math.vec2i;
import math.vec3i;
import math.vec4f;

public class VectorDrawing extends Node
{
	private List<TextureVertex> vertices = new ArrayList<>();
	private List<vec2i> lines = new ArrayList<>();
	
	public VectorDrawing(String name) 
	{
		super(name);
	}
	
	public int addVertex( float x, float y )
	{
		vertices.add( new TextureVertex( new vec2f( x, -y ), new vec4f( 1.0f, 1.0f, 1.0f, 1.0f ) ) );
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
			TextureVertex vertex = vertices.get( i );
			float dx = x - vertex.position.x;
			float dy = y - vertex.position.y;
			
			if( dx * dx + dy * dy <= radius )
				return i;
		}
		
		return -1;
	}
	
	public void setPosition( int vIdx, float x, float y )
	{
		TextureVertex vertex = vertices.get( vIdx );
		vertex.position.x = x;
		vertex.position.y = -y;
		vertex.modified = true;
	}
	
	public void setColor( int vIdx, float x, float y )
	{
		TextureVertex vertex = vertices.get( vIdx );
		float dx = x - vertex.position.x;
		float dy = y - vertex.position.y;
		
		float angle = (float)Math.atan2(dy, dx);
		int col = (int)(angle * 0xFFFFFF);
		float r = ((col >> 16) & 0xFF) / 255.0f;
		float g = ((col >> 8) & 0xFF) / 255.0f;
		float b = (col & 0xFF) / 255.0f;
		
		vertex.color.x = r;
		vertex.color.y = g;
		vertex.color.z = b;
		vertex.modified = true;
	}
	
	public void setSelected( int vIdx, boolean state )
	{
		TextureVertex vertex = vertices.get( vIdx );
		if( state != vertex.selected ) 
		{
			vertex.modified = true;
			vertex.selected = state;
		}
	}
	
	public List<TextureVertex> getVertices()
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
			unique[0] = l0.x;
			unique[1] = l0.y;
			
			for( int j = i + 1; j < lines.size() - 1; j++ )
			{
				int c2x = 2;
				vec2i l1 = lines.get( j );
				if( !isContained( unique, l1.x, c2x ) ) unique[c2x++] = l1.x;
				if( !isContained( unique, l1.y, c2x ) ) unique[c2x++] = l1.y;
				if( c2x > 3 ) continue;
				
				for( int k = j + 1; k < lines.size(); k++ )
				{
					int c3x = c2x;
					vec2i l2 = lines.get( k );
					if( !isContained( unique, l2.x, c2x ) ) c3x++;
					if( !isContained( unique, l2.y, c2x ) ) c3x++;
					
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
		
		writer.write( vertices.size() + "\n" );
		for( Vertex vertex : vertices )
			writer.write( vertex.toString() + '\n' );
		
		writer.write( lines.size() + "\n" );
		for( vec2i line : lines )
			writer.write( line.toString() + '\n' );
		
		writer.close();
	}
	
	private static boolean isContained( int[] arr, int value, int sz )
	{
		for( int i = 0; i < sz; i++ )
			if( arr[i] == value )
				return true;
		return false;
	}
	
	private static class TextureVertex implements Vertex
	{
		public vec2f position;
		public vec4f color;
		public boolean selected = false;
		public boolean modified = true;
		
		public TextureVertex( vec2f pos, vec4f col )
		{
			position = pos;
			color = col;
		}

		@Override
		public void writeDataBuffer( float[] buffer, int idx )
		{
			buffer[idx++] = position.x;
			buffer[idx++] = position.y;
			buffer[idx++] = color.x;
			buffer[idx++] = color.y;
			buffer[idx++] = color.z;
			buffer[idx++] = color.w;
			modified = false;
		}
		
		@Override
		public float[] getData()
		{
			return new float[] { position.x, position.y, color.x, color.y, color.z, color.w };
		}


		@Override
		public boolean isModified() 
		{
			return modified;
		}

		@Override
		public String toString() 
		{
			return String.join( ", ", Vertex.toStringArr( this ) );
		}
	}
}
