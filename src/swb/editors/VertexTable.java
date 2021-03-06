package swb.editors;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

import swb.GLNode;
import swb.VertexBuffer;

@SuppressWarnings("serial")
public class VertexTable extends EditorTable
{
	private static final TableColumn[] COLUMNS = {
			new TableColumn( "i", int.class, false ),
			new TableColumn( "x", float.class, true ),
			new TableColumn( "y", float.class, true ),
			new TableColumn( "z", float.class, true ),
			new TableColumn( "ox", float.class, true ),
			new TableColumn( "oy", float.class, true ),
			new TableColumn( "oz", float.class, true ),
			new TableColumn( "tx", float.class, true ),
			new TableColumn( "ty", float.class, true )
	};
	
	
	private JTextField _textField = new JTextField();
	
	public VertexTable( String title, VertexBuffer buffer ) 
	{
		super( title, new VertexTableModel( buffer ) );
	}
	
	public VertexBuffer getBuffer()
	{
		return ((VertexTableModel)_model).buffer;
	}

	@Override
	protected void add() 
	{
		((VertexTableModel)_model).addRow( new float[ _model.getColumnCount() - 1 ] );
	}

	@Override
	protected void remove() 
	{
		_model.removeRow();
	}
	
	@Override
	public TableCellEditor getCellEditor( int row, int column )
	{
		return new DefaultCellEditor( _textField );
	}
	
	private static class VertexTableModel extends ObserverTableModel
	{
		private VertexBuffer buffer;
		
		public VertexTableModel( VertexBuffer buffer )
		{
			super();
			this.buffer = buffer;
			
			for( int i = 0; i < COLUMNS.length && i <= buffer.stride(); i++ )
				addColumn( COLUMNS[i] );
			
			for( int i = COLUMNS.length - 1; i < buffer.stride(); i++ )
				addColumn( new TableColumn( "e" + i, float.class, true ) );
		}
		
		public void addRow( float ... row )
		{
			buffer.add( row );
			updateTable( new TableModelEvent( this ) );
		}
		
		@Override
		public void removeRow() 
		{
			buffer.resize( buffer.capacity() - buffer.stride() );
			updateTable( new TableModelEvent( this ) );
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			try
			{
				buffer.set( row, col - 1, Float.parseFloat( value.toString() ) );
			}
			catch( NumberFormatException e )
			{
				System.err.println( "Error - Cannot parse value!" );
			}
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return row;
			default: return buffer.get( row, col - 1 );
			}
		}

		@Override
		public int getRowCount()
		{
			return buffer.length();
		}
		
		@Override 
		public GLNode getSource()
		{
			return buffer;
		}
	}
}
