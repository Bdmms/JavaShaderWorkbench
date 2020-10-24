import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

public class UniformList extends AbstractNode
{
	private List<Uniform> uniforms = new ArrayList<>();
	
	public UniformList( String name )
	{
		super( name );	
	}
	
	@Override
	public EditorView createEditor() 
	{
		return new UniformTable( getPath(), this );
	}
	
	public void initialize( GL3 gl, ShaderProgram program ) 
	{
		for( Uniform uniform : uniforms )
		{
			if( uniform.type == GLDataType.SAMP2D )
			{
				try
				{
					int value = Integer.parseInt( uniform.value.toString() );
					int loc = gl.glGetUniformLocation( program.getID(), uniform.name );
					gl.glUniform1i( loc, value );
				}
				catch( NumberFormatException e ) {}
			}
			
			System.out.println( uniform.name + ": " + uniform.value.toString() );
		}
	}
	
	public Uniform get( int index )
	{
		return uniforms.get( index );
	}
	
	public void add( String name, GLDataType type, String value )
	{
		uniforms.add( new Uniform( name, value, type ) );
	}
	
	public void removeLast()
	{
		uniforms.remove( uniforms.size() - 1 );
	}
	
	public int size()
	{
		return uniforms.size();
	}
	
	public static class Uniform
	{
		public String name;
		public GLDataType type;
		public Object value;
		
		public Uniform( String name, String value, GLDataType type )
		{
			this.name = name;
			this.value = value;
			this.type = type;
		}
		
		public void setValue( String val )
		{
			// TODO
			switch( type )
			{
			default: value = val; break;
			}
		}
	}
}
