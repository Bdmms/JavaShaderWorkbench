import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.jogamp.opengl.GL3;

@SuppressWarnings("serial")
public abstract class EditorTable extends JTable
{
	protected EditorTableModel<?> _model;
	
	public EditorTable( EditorTableModel<?> model )
	{
		super( model );
		_model = model;
		
		setFont( ShaderEditor.FONT );
		getTableHeader().setFont( ShaderEditor.FONT );
	}
	
	public abstract void bindAll( GL3 gl, ShaderProgram program );
	
	protected abstract void add();
	protected abstract void remove();
	
	public static JComponent createEditor( final EditorTable editor )
	{
		JPanel panel = new JPanel( new BorderLayout() );
		JPanel buttons = new JPanel( new BorderLayout() );
		
		JButton createRow = new JButton( new AbstractAction( "Add" )
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				editor.add();
			}
		} );
		
		JButton deleteRow = new JButton( new AbstractAction( "Remove" )
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				editor.remove();
			}
		} );
		
		buttons.add( createRow, BorderLayout.WEST );
		buttons.add( deleteRow, BorderLayout.EAST );
		
		panel.add( new JScrollPane( editor ), BorderLayout.CENTER );
		panel.add( buttons, BorderLayout.SOUTH );
		
		return panel;
	}
	
	protected static class TableColumn
	{
		public final String name;
		public final Class<?> type;
		public final boolean editable;
		
		public TableColumn( String name, Class<?> type, boolean edit )
		{
			this.name = name;
			this.type = type;
			this.editable = edit;
		}
	}
	
	protected static abstract class EditorTableModel<E> implements TableModel
	{
		private List<TableModelListener> _listeners = new ArrayList<>();
		protected List<E> _rows = new ArrayList<>();
		protected List<TableColumn> _columns;
		
		public EditorTableModel( TableColumn[] columns )
		{
			_columns = Arrays.asList( columns );
		}
		
		public void addColumn( TableColumn column )
		{
			_columns.add( column );
		}
		
		public void addRow( E row )
		{
			_rows.add( row );
			updateTable( new TableModelEvent( this ) );
		}
		
		public void removeRow()
		{
			if( !_rows.isEmpty() )
			{
				_rows.remove( _rows.size() - 1 );
				updateTable( new TableModelEvent( this ) );
			}
		}
		
		protected void updateTable( TableModelEvent e )
		{
			for( TableModelListener listener : _listeners )
			{
				listener.tableChanged( e );
			}
		}
		
		public abstract void bindAll( GL3 gl, ShaderProgram program );
		
		@Override
		public void addTableModelListener( TableModelListener listener ) 
		{ 
			_listeners.add( listener );
		}
		
		@Override
		public void removeTableModelListener( TableModelListener listener ) 
		{ 
			_listeners.remove( listener );
		}
		
		@Override
		public int getColumnCount()  
		{ 
			return _columns.size(); 
		}
		
		@Override
		public String getColumnName( int col )
		{
			return _columns.get( col ).name;
		}
		
		@Override
		public Class<?> getColumnClass(int col) 
		{
			return _columns.get( col ).type;
		}
		
		@Override
		public boolean isCellEditable( int row, int col ) 
		{
			return _columns.get( col ).editable;
		}
		
		@Override
		public int getRowCount()  
		{ 
			return _rows.size(); 
		}
	}
}
