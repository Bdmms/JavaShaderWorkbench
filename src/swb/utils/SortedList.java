package swb.utils;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("serial")
/**
 * Implements an ordered list. It maintains the order of the list as elements
 * are added to the list. This improves the efficiency of searching.
 */
public class SortedList<T extends Comparable<T>> extends ArrayList<T>
{
	/**
	 * Creates an empty sorted list
	 */
	public SortedList()
	{
		super();
	}
	
	/**
	 * Creates an empty sorted list with a specified capacity
	 * @param capacity - initial capacity of the list
	 */
	public SortedList( int capacity )
	{
		super( capacity );
	}
	
	/**
	 * Extended indexOf method that applies binary search
	 * @param value - value to search for
	 * @return the index of the value in the list
	 */
	public int indexOf( T value )
	{
		return Collections.binarySearch( this, value );
	}
	
	@Override
	public boolean add( T value )
	{
		int index = indexOf( value );
		
		if( index > 0 ) return false;
		super.add( ~index, value );
		return true;
	}
}
