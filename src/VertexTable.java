
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class VertexTable extends EditorTable
{
	private JTextField _textField = new JTextField();
	
	public VertexTable( String title, VertexBuffer buffer ) 
	{
		super( title, new VertexTableModel( buffer ) );
	}
	
	public void addVertex( float ... vertex )
	{
		((VertexTableModel)_model).addRow( vertex );
	}
	
	public void replaceWith( float[] arr, int stride )
	{
		((VertexTableModel)_model).replaceWith( arr, stride );
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
			super( new TableColumn[] {
					new TableColumn( "i", int.class, false ),
					new TableColumn( "x", float.class, true ),
					new TableColumn( "y", float.class, true ),
					new TableColumn( "z", float.class, true ),
					new TableColumn( "ox", float.class, true ),
					new TableColumn( "oy", float.class, true ),
					new TableColumn( "oz", float.class, true ),
					new TableColumn( "tx", float.class, true ),
					new TableColumn( "ty", float.class, true )
			} );
			
			this.buffer = buffer;
		}
		
		public void addRow( float ... row )
		{
			buffer.add( row );
			updateTable( new TableModelEvent( this ) );
		}
		
		public void replaceWith( float[] arr, int stride )
		{
			buffer.replace( arr, stride );
			updateTable( new TableModelEvent( this ) );
		}
		
		@Override
		public void removeRow() 
		{
			buffer.resize( buffer.length() - buffer.stride() );
			updateTable( new TableModelEvent( this ) );
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			buffer.set( row, col - 1, Float.parseFloat( value.toString() ) );
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
			return buffer.size();
		}
	}
}
