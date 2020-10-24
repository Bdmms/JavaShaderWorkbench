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
public abstract class EditorTable extends JTable implements EditorView
{
	protected ObserverTableModel _model;
	
	public EditorTable( String title, ObserverTableModel model )
	{
		super( model );
		_model = model;
		
		setName( title );
		setFont( EditorTabs.FONT );
		getTableHeader().setFont( EditorTabs.FONT );
	}
	
	public void bindAll( GL3 gl, ShaderProgram program )
	{
		if( _model instanceof EditorTableModel )
		{
			((EditorTableModel<?>)_model).bindAll( gl, program );
		}
	}
	
	protected abstract void add();
	protected abstract void remove();
	
	public JComponent createView()
	{
		JPanel panel = new JPanel( new BorderLayout() );
		JPanel buttons = new JPanel( new BorderLayout() );
		
		JButton createRow = new JButton( new AbstractAction( "Add" )
		{
			@Override
			public void actionPerformed( ActionEvent e ) 
			{
				add();
			}
		} );
		
		JButton deleteRow = new JButton( new AbstractAction( "Remove" )
		{
			@Override
			public void actionPerformed( ActionEvent e ) 
			{
				remove();
			}
		} );
		
		buttons.add( createRow, BorderLayout.WEST );
		buttons.add( deleteRow, BorderLayout.EAST );
		
		panel.add( new JScrollPane( this ), BorderLayout.CENTER );
		panel.add( buttons, BorderLayout.SOUTH );
		
		return panel;
	}
	
	public JComponent getComponent()
	{
		return this;
	}
	
	public ObserverTableModel getEditorModel()
	{
		return _model;
	}
	
	public static class TableColumn
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
	
	protected static abstract class ObserverTableModel implements TableModel
	{
		private List<TableModelListener> _listeners = new ArrayList<>();
		protected List<TableColumn> _columns = new ArrayList<>();
		
		public ObserverTableModel( TableColumn[] columns )
		{
			_columns.addAll( Arrays.asList( columns ) );
		}
		
		public void setColumns( TableColumn[] columns )
		{
			_columns.clear();
			_columns.addAll( Arrays.asList( columns ) );
			updateTable( new TableModelEvent( this ) );
		}
		
		protected void updateTable( TableModelEvent e )
		{
			for( TableModelListener listener : _listeners )
			{
				listener.tableChanged( e );
			}
		}
		
		public void addColumn( TableColumn column )
		{
			_columns.add( column );
		}
		
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
		
		public abstract void removeRow();
		public abstract void setValueAt(Object value, int row, int col);
		public abstract Object getValueAt( int row, int col );
	}
	
	protected static abstract class EditorTableModel<E> extends ObserverTableModel
	{
		protected List<E> _rows = new ArrayList<>();
		
		public EditorTableModel( TableColumn[] columns )
		{
			super( columns );
		}
		
		public void clear()
		{
			_rows.clear();
		}
		
		public void setColumns( TableColumn[] columns )
		{
			_rows.clear();
			super.setColumns( columns );
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
		
		public abstract void bindAll( GL3 gl, ShaderProgram program );
		
		public List<E> getRows()
		{
			return _rows;
		}
		
		public E getRow( int row )
		{
			return _rows.get( row );
		}
		
		@Override
		public int getRowCount()  
		{ 
			return _rows.size(); 
		}
	}
}
