package swb;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

public class VertexAttribute 
{
	private List<GLDataType> _attributes = new ArrayList<GLDataType>();
	private int _vertexSize = 0;
	
	public void add( int i, GLDataType type )
	{
		_attributes.add( i, type );
	}
	
	public void recalculateSize()
	{
		_vertexSize = _attributes.stream().filter( atr -> atr != null ).mapToInt( atr -> atr.size * sizeof( atr.type ) ).sum();
	}
	
	public void bind( GL3 gl )
	{
		int offset = 0;
		
		for( int i = 0; i < _attributes.size(); i++)
		{
			GLDataType dataType = _attributes.get( i );
			
			gl.glEnableVertexAttribArray( i );
			gl.glVertexAttribPointer( i, dataType.size, dataType.type, false, _vertexSize, offset );
			offset += dataType.size * sizeof( dataType.type );
		}
	}
	
	public int getVertexSize()
	{
		return _vertexSize;
	}
	
	public boolean isCompatibleWith( int stride )
	{
		return _vertexSize == stride * Float.BYTES;
	}
	
	public void print()
	{
		for( int i = 0; i < _attributes.size(); i++)
		{
			GLDataType dataType = _attributes.get( i );
			
			if( dataType == null )
				System.out.println( "\tlayout (location = " + i + ") null" );
			else
				System.out.println( "\tlayout (location = " + i + ") in " + dataType.keyword + " (" + dataType.size + "x" + dataType.type + ");"  );
		}
	}
	
	public static int sizeof( int GL_TYPE )
	{
		switch( GL_TYPE )
		{
		default: return 4;
		case GL3.GL_BOOL: 	return 1;
		case GL3.GL_BYTE: 	return 1;
		case GL3.GL_SHORT: 	return 2;
		case GL3.GL_INT: 	return 4;
		case GL3.GL_FLOAT: 	return 4;
		case GL3.GL_DOUBLE: return 8;
		case GL3.GL_UNSIGNED_BYTE: 	return 1;
		case GL3.GL_UNSIGNED_SHORT: return 2;
		case GL3.GL_UNSIGNED_INT: 	return 4;
		}
	}
	
	public static VertexAttribute parse( String content )
	{
		VertexAttribute attribute = new VertexAttribute();
		
		String[] lines = content.split( "\n" );
		
		for( String line : lines )
		{
			if( !line.contains("layout") ) continue;
			
			int index = line.indexOf( ')' );
			int loc = Integer.parseInt( line.substring( line.indexOf('=') + 1, index ).replaceAll( " ", "" ) );
			String[] parts = line.substring( index + 1 ).split( " " );
			
			GLDataType type = null;
			for( int i = 0; i < parts.length; i++ )
			{
				if( parts[i].equals("in") )
					type = GLDataType.getType( parts[i + 1] );
			}
			
			attribute.add( loc, type );
		}
		
		attribute.recalculateSize();
		return attribute;
	}
}
