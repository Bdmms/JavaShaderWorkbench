package swb;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import swb.editors.EditorView;
import swb.editors.UniformTable;

public class UniformList extends GLNode
{
	private List<Uniform> uniforms = new ArrayList<>();
	private ShaderProgram _linkedProgram = null;
	
	public UniformList( String name )
	{
		super( name );	
	}
	
	@Override
	public EditorView createEditor() 
	{
		return new UniformTable( getPath(), this );
	}
	
	@Override
	public boolean build( Renderer renderer )
	{
		_linkedProgram = (ShaderProgram)renderer.instances[LAST_PROGRAM];
		compileFlag &= _linkedProgram != null && _linkedProgram.compileFlag;
		return _linkedProgram != null;
	}
	
	@Override
	public boolean compile( GL3 gl )
	{
		if( compileFlag ) return true;
		
		System.out.println( "Compiling: " + getPath() );
		if( _linkedProgram == null ) return false;
		
		for( Uniform uniform : uniforms )
		{
			uniform.loc = gl.glGetUniformLocation( _linkedProgram.getID(), uniform.name );
			System.out.println( "\t" + uniform.type.toString() + ' ' + uniform.name + " = " + uniform.getValue() );
		}
		
		modifyFlag = true;
		return super.compile( gl );
	}
	
	@Override
	public void update( GL3 gl )
	{
		if( !modifyFlag ) return;
		
		System.out.println( "Uploading: " + getPath() );
		for( Uniform uniform : uniforms )
		{
			switch( uniform.type )
			{
			case IVEC1:
			case SAMP2D: gl.glUniform1i( uniform.loc, ((UniformInt)uniform).value ); break;
			case VEC1: gl.glUniform1f( uniform.loc, ((UniformFloat)uniform).value ); break;
			default: System.err.println( "Error - Unknown data type" ); break;
			}
		}
		
		super.update( gl );
	}
	
	public Uniform get( int i )
	{
		return uniforms.get( i );
	}
	
	public void setName( int i, String name )
	{
		Uniform uniform = uniforms.get( i );
		if( uniform.name.equals( name ) ) return;
		uniform.name = name;
		compileFlag = false;
		modifyFlag = true;
	}
	
	public void setValue( int i, String value )
	{
		try
		{
			uniforms.get( i ).setValue( value );
			modifyFlag = true;
		}
		catch( NumberFormatException e )
		{
			System.err.println( "Error - Cannot parse value!" );
		}
	}
	
	public void swapType( int i, GLDataType type )
	{
		Uniform uniform = uniforms.get( i );
		if( uniform.type == type ) return;
		uniforms.set( i, createUniform( uniform.name, type ) );
		compileFlag = false;
		modifyFlag = true;
	}
	
	public void add( String name, GLDataType type )
	{
		uniforms.add( createUniform( name, type ) );
		compileFlag = false;
		modifyFlag = true;
	}
	
	public void add( String name, GLDataType type, String value )
	{
		Uniform uniform = createUniform( name, type );
		uniforms.add( uniform );
		uniform.setValue( value );
		compileFlag = false;
		modifyFlag = true;
	}
	
	public Uniform removeLastUniform()
	{
		// TODO: Handle deleting uniform
		Uniform uniform = uniforms.remove( uniforms.size() - 1 );
		compileFlag = false;
		modifyFlag = true;
		return uniform;
	}
	
	public int length()
	{
		return uniforms.size();
	}
	
	public static Uniform createUniform( String name, GLDataType type )
	{
		Uniform uniform;
		
		switch( type )
		{
		case IVEC1: 	uniform = new UniformInt(); break;
		case VEC1: 		uniform = new UniformFloat(); break;
		case DVEC1: 	uniform = new UniformDouble(); break;	
		case SAMP2D: 	uniform = new UniformInt(); break;
		default: return null;
		}
		
		uniform.name = name;
		uniform.type = type;
		return uniform;
	}
	
	public static abstract class Uniform
	{
		public String name = "";
		public GLDataType type = GLDataType.UNKNOWN;
		public int loc = -1;
		
		public abstract void setValue( String val ) throws NumberFormatException;
		public abstract String getValue();
	}
	
	public static class UniformInt extends Uniform
	{
		public int value = 0;
		public void setValue( String val ) throws NumberFormatException { value = Integer.parseInt( val ); }
		public String getValue() { return Integer.toString( value ); }
	}
	
	public static class UniformFloat extends Uniform
	{
		public float value = 0.0f;
		public void setValue( String val ) throws NumberFormatException { value = Float.parseFloat( val ); }
		public String getValue() { return Float.toString( value ); }
	}
	
	public static class UniformDouble extends Uniform
	{
		public double value = 0.0;
		public void setValue( String val ) throws NumberFormatException { value = Double.parseDouble( val ); }
		public String getValue() { return Double.toString( value ); }
	}
}
