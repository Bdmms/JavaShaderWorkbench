package swb.utils;

public class PrintUtil 
{
	public static String[] convertToStringArr( boolean[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( byte[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( short[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( int[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( long[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( float[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( double[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = String.valueOf( arr[idx++] );
		return parts;
	}
	
	public static String[] convertToStringArr( Object[] arr, int idx, int end )
	{
		String[] parts = new String[end - idx];
		for( int i = 0; idx < end; i++ ) parts[i] = arr[idx++].toString();
		return parts;
	}
	
	public static String[] convertToStringArr( boolean[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( byte[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( short[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( int[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( long[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( float[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( double[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String[] convertToStringArr( Object[] arr )
	{
		return convertToStringArr( arr, 0, arr.length );
	}
	
	public static String toString( String delimiter, boolean[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, byte[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, short[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, int[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, long[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, float[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, double[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, Object[] arr )
	{
		return String.join( delimiter, convertToStringArr( arr ) );
	}
	
	public static String toString( String delimiter, boolean[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, byte[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, short[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, int[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, long[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, float[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, double[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( String delimiter, Object[] arr, int idx, int end )
	{
		return String.join( delimiter, convertToStringArr( arr, idx, end ) );
	}
	
	public static String toString( boolean[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( byte[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( short[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( int[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( long[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( float[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( double[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
	
	public static String toString( Object[] arr )
	{
		return String.join( ", ", convertToStringArr( arr ) );
	}
}
