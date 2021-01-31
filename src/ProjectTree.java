import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.jogamp.opengl.GL3;

public class ProjectTree extends JTree
{
	private static final long serialVersionUID = 1L;
	
	private EditorTabs editor;
	private Node root;
	
	public ProjectTree( EditorTabs editor )
	{
		super();
		
		root = new Node( "#0xB7DD09" );
		this.editor = editor;
		this.setModel( new ModelTreeModel() );
		this.addMouseListener( new ModelTreeListener() );
	}
	
	public EditorTabs getEditor()
	{
		return editor;
	}
	
	public void add( LeafNode node )
	{
		((ModelTreeModel)treeModel).add( node );
		
		// Re-create tree
		this.setModel( new ModelTreeModel() );
		for(int i = 0; i < getRowCount(); i++)
	    	expandRow(i);
	}
	
	public LeafNode get( int index )
	{
		return root.children().get( index );
	}
	
	public boolean build( GL3 gl )
	{
		return root.build( gl );
	}
	
	public void upload( GL3 gl )
	{
		root.upload( gl );
	}
	
	public void render( GL3 gl )
	{
		root.render( gl );
	}
	
	public void dispose( GL3 gl )
	{
		root.dispose( gl );
	}
	
	private void open( TreePath path )
	{
		Object comp = path.getLastPathComponent();
		
		if( comp instanceof LeafNode )
		{
			EditorView view = ((LeafNode)comp).createEditor();
			
			if( view != null ) editor.open( view );
		}
	}
	
	private class ModelTreeListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e) 
		{
			int selRow = getRowForLocation( e.getX(), e.getY() );
			TreePath selPath = getPathForLocation( e.getX(), e.getY() );
			
			if( selRow != -1 && e.getClickCount() == 2 )
			{
				open( selPath );
			}
		}
	}
	
	private class ModelTreeModel implements TreeModel
	{
		private List<TreeModelListener> _listeners = new ArrayList<>();
		
		@Override
		public Node getRoot() 
		{
			return root;
		}
		
		public void add( LeafNode node )
		{
			root.add( node );
			for( TreeModelListener listener : _listeners )
			{
				listener.treeNodesInserted( new TreeModelEvent( this, new LeafNode[]{ root, node } ) );
				listener.treeNodesChanged( new TreeModelEvent( this, new LeafNode[]{ root } ) );
			}
		}
		
		@Override
		public void addTreeModelListener( TreeModelListener l ) 
		{
			_listeners.add( l );
		}
		
		@Override
		public void removeTreeModelListener( TreeModelListener l ) 
		{
			_listeners.remove( l );
		}

		@Override
		public LeafNode getChild( Object parent, int index ) 
		{
			return parent instanceof Node ? ((Node)parent).children().get( index ) : null;
		}

		@Override
		public int getChildCount( Object parent ) 
		{
			return parent instanceof Node ? ((Node)parent).children().size() : 0;
		}
 
		@Override
		public int getIndexOfChild( Object parent, Object child ) 
		{
			return parent instanceof Node ? ((Node)parent).children().indexOf( child ) : -1;
		}

		@Override
		public boolean isLeaf( Object node ) 
		{
			return (node instanceof LeafNode) && !(node instanceof Node);
		}

		@Override
		public void valueForPathChanged( TreePath path, Object newValue ) 
		{
			System.out.println( path );
			System.out.println( newValue );
			System.out.println( "???" );
		}
	}
}
