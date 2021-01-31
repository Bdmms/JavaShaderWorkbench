package tests;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TestClass
{
	private static Method[] DEFAULT = Object.class.getMethods();
	public static Class<?>[] TESTS = 
	{
			TestLine.class,
			TestCircle.class,
			TestMatrix.class
	};
	
	private static boolean isDefault( final Method m )
	{
		return Arrays.stream( DEFAULT ).anyMatch( def -> m.toString().equals( def.toString() ) );
	}
	
	private static void test( Object o, Class<?> c )
	{
		for( Method m : c.getMethods() )
		{
			if( m.getParameterCount() != 0 || isDefault( m ) ) continue;
			
			Thread thread = new Thread( new Runnable() {
				@Override
				public void run() 
				{
					String log = m.toString();
					long t0 = System.currentTimeMillis();
					
					try 
					{
						m.invoke( o );
						log += " - SUCCESS (";
						long t1 = System.currentTimeMillis();
						System.out.println( log + (t1 - t0) + " ms)" );
					}
					catch ( Exception e ) 
					{
						log += " - FAILURE (";
						long t1 = System.currentTimeMillis();
						System.out.println( log + (t1 - t0) + " ms)" );
						e.printStackTrace();
					} 
					
				}
			} );
			thread.run();
		}
	}
	
	public static void main( String[] args )
	{
		for( Class<?> test : TESTS )
		{
			try 
			{
				test( test.newInstance(), test );
			} 
			catch ( Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
}
