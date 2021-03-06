package swb.math;

public class MFloat implements MathObj<MFloat>
{
	public float value;
	
	public MFloat() 
	{
		value = 0.0f;
	}
	
	public MFloat( float v )
	{
		value = v;
	}

	@Override
	public void add( MFloat b) 
	{
		value += b.value;
	}
	
	@Override
	public void sub( MFloat b) 
	{
		value -= b.value;
	}
	
	@Override
	public void mul( float b ) 
	{
		value *= b ;
	}

	@Override
	public void div( float b ) 
	{
		value /= b;
	}
	
	@Override
	public MFloat clone()
	{
		return new MFloat( value );
	}
	
	@Override
	public int toColor()
	{
		int ch = value < 0.0f ? 0x00 : (value > 1.0f ? 0xFF : ((int)(value * 255.0f) & 0xFF));
		return 0xFF000000 | (ch << 16) | (ch << 8) | ch;
	}
}
