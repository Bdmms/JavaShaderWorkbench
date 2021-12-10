package swb.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public abstract class AbstractTree<T extends AbstractTree<T>> implements Collection<T>
{
	/** List of children */
	protected LinkedList<T> children = new LinkedList<>();
	/** Parent node */
	protected T parent = null;
	
	/**
	 * Returns the parent of this node
	 * @return The parent of this node
	 */
	public T parent() 
	{
		return parent;
	}
	
	/**
	 * Returns the direct descendants of this node.
	 * @return The children of this node
	 */
	public LinkedList<T> children() 
	{
		return children;
	}
	
	/**
	 * Sets the parent of this node
	 * @param node - New parent node
	 */
	public void setParent( T node ) 
	{
		if( parent != null ) parent.children().remove( this );
		parent = node;
	}
	
	/**
	 * Propagates an event to all above parents
	 * @param source - tree node of origin
	 */
	public void childEventNotify( T source )
	{
		parent.childEventNotify( source );
	}

	@Override
	public boolean addAll(Collection<? extends T> c) 
	{
		boolean changed = false;
		for( T node : c )
		{
			changed |= add( node );
		}
		return changed;
	}

	@Override
	public void clear() 
	{
		children.clear();
	}

	@Override
	public boolean contains(Object o) 
	{
		if( this == o ) return true;
		for( T node : children )
			if( node.contains( o ) )
				return true;
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) 
	{
		for( Object o : c )
			if( !contains( o ) )
				return false;
		return true;
	}

	@Override
	public boolean isEmpty() 
	{
		return children.isEmpty();
	}

	@Override
	public boolean remove(Object o) 
	{
		boolean changed = false;
		for( T node : children )
		{
			if( node == o )
			{
				node.setParent( null );
				changed |= children.remove( o );
			}
			
			changed |= node.remove( o );
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) 
	{
		boolean changed = false;
		for( T node : children )
		{
			if( c.contains( node ) )
			{
				node.setParent( null );
				changed |= children.remove( node );
			}
			else
				changed |= node.removeAll( c );
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) 
	{
		boolean changed = false;
		for( T node : children )
		{
			if( !c.contains( node ) )
			{
				node.setParent( null );
				changed |= children.remove( node );
			}
			else
				changed |= node.retainAll( c );
		}
		return changed;
	}

	@Override
	public int size() 
	{
		int size = 0;
		for( T node : children )
			size += node.size();
		return size;
	}

	@Override
	public Object[] toArray() 
	{
		Object[] arr = new Object[ size() ];
		return toArray( arr );
	}

	@Override
	public <E> E[] toArray( E[] arr ) 
	{
		toArray( arr, 0 );
		return arr;
	}
	
	public <E> int toArray( E[] arr, int offset )
	{
		for( T node : children )
			offset = node.toArray( arr, offset );
		return offset;
	}
	
	@Override
	public Iterator<T> iterator() 
	{
		return new TreeIterator();
	}
	
	/** 
	 * Iterator that can navigate all elements in the Tree
	 * */
	private class TreeIterator implements Iterator<T>
	{
		/** Stack of remaining nodes to traverse */
		private Stack<T> stack = new Stack<>();

		/**
		 * Creates an iterator initialized with the root node's children
		 */
		public TreeIterator()
		{
			Iterator<T> iterator = children.descendingIterator();
			while( iterator.hasNext() )
				stack.push( iterator.next() );
		}
		
		@Override
		public boolean hasNext() {
			
			return !stack.isEmpty();
		}

		@Override
		public T next() 
		{
			// Navigates in level order
			T current = stack.pop();
			Iterator<T> iterator = current.children().descendingIterator();
			while( iterator.hasNext() )
				stack.push( iterator.next() );
			return current;
		}
	}
}
