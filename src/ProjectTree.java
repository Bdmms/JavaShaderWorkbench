import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
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
		
		root = new RootNode( "[ #0xB7DD09 ]" );
		this.editor = editor;
		this.setModel( new ModelTreeModel() );
		this.addMouseListener( new ModelTreeListener() );
	}
	
	public EditorTabs getEditor()
	{
		return editor;
	}
	
	public void add( Node node )
	{
		root.add( node );
	}
	
	public Node get( int index )
	{
		return root.children().get( index );
	}
	
	public Node root()
	{
		return root;
	}
	
	public boolean initialize( GL3 gl )
	{
		return root.initialize( gl );
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
		
		if( comp instanceof Node )
		{
			EditorView view = ((Node)comp).createEditor();
			
			if( view != null ) editor.open( view );
		}
	}
	
	private class RootNode extends AbstractNode
	{
		public RootNode(String name) 
		{
			super(name);
		}

		@Override
		public EditorView createEditor()  { return null; }
		@Override
		public void setParent( Node node ) { }
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
		public Node getChild( Object parent, int index ) 
		{
			return ((Node)parent).children().get( index );
		}

		@Override
		public int getChildCount( Object parent ) 
		{
			return ((Node)parent).children().size();
		}
 
		@Override
		public int getIndexOfChild( Object parent, Object child ) 
		{
			return ((Node)parent).children().indexOf( child );
		}

		@Override
		public boolean isLeaf( Object node ) 
		{
			return ((Node)node).children().isEmpty();
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
