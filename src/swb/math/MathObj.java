package swb.math;

public interface MathObj<T> extends Cloneable
{
	public void add( T b );
	public void sub( T b );
	public void mul( float b );
	public void div( float b );
	
	public int toColor();
	public T clone();
	
	public static <T extends MathObj<T>> T add( MathObj<T> a, T b )
	{
		T c = a.clone();
		c.add( b );
		return c;
	}
	
	public static <T extends MathObj<T>> T sub( MathObj<T> a, T b )
	{
		T c = a.clone();
		c.sub( b );
		return c;
	}
	
	public static <T extends MathObj<T>> T mul( MathObj<T> a, float b )
	{
		T c = a.clone();
		c.mul( b );
		return c;
	}
	
	@SafeVarargs
	public static <T extends MathObj<T>> T average( MathObj<T> start, T ... values )
	{
		T avg = start.clone();
		for( T val : values )
			avg.add( val );
		avg.div( values.length + 1 );
		return avg;
	}
	
	public static <T extends MathObj<T>> T average( T v0, T v1, float w)
	{
		T avg = MathObj.mul( v0, 1.0f - w );
		avg.add( MathObj.mul( v1, w ) );
		return avg;
	}
	
	public static <T extends MathObj<T>> T average( T v0, T v1, T v2, T v3, float w0, float w1)
	{
		float w2 = w1 - w0 * w1;
		float w3 = w0 * w1;
		float rw1 = 1.0f - w1;
		w1 = w0 * rw1;
		w0 = rw1 - w0 * rw1;

		T avg = MathObj.mul( v0, w0 );
		avg.add( MathObj.mul( v1, w1 ) );
		avg.add( MathObj.mul( v2, w2 ) );
		avg.add( MathObj.mul( v3, w3 ) );
		return avg;
	}
}
