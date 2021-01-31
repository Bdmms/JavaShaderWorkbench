import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class ElementTable extends EditorTable
{
	private JTextField _textField = new JTextField();
	
	public ElementTable( String title, ElementBuffer buffer ) 
	{
		super( title, new ElementTableModel( buffer ) );
	}
	
	public void addTriangle( int v1, int v2, int v3 )
	{
		((ElementTableModel)_model).addRow( v1, v2, v3 );
	}
	
	@Override
	protected void add() 
	{
		((ElementTableModel)_model).addRow( 0, 0, 0 );
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
	
	public ElementBuffer getBuffer()
	{
		return ((ElementTableModel)_model).buffer;
	}
	
	private static class ElementTableModel extends ObserverTableModel
	{
		private ElementBuffer buffer;
		
		public ElementTableModel( ElementBuffer buffer )
		{
			super( new TableColumn[] {
					new TableColumn( "i", int.class, false ),
					new TableColumn( "v1", int.class, true ),
					new TableColumn( "v2", int.class, true ),
					new TableColumn( "v3", int.class, true )
			} );
			
			this.buffer = buffer;
		}
		
		@Override
		public void removeRow() 
		{
			buffer.resize( buffer.length() - 3 );
			updateTable( new TableModelEvent( this ) );
		}
		
		public void addRow( int ... row )
		{
			buffer.add( row );
			updateTable( new TableModelEvent( this ) );
		}
		
		@Override
		public void setValueAt( Object value, int row, int col ) 
		{
			try
			{
				switch( col )
				{
				case 1: buffer.set( row, 0, Integer.parseInt( value.toString() ) ); break;
				case 2: buffer.set( row, 1, Integer.parseInt( value.toString() ) ); break;
				case 3: buffer.set( row, 2, Integer.parseInt( value.toString() ) ); break;
				}
			}
			catch( NumberFormatException e ) {}
		}

		@Override
		public Integer getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return row;
			case 1: return buffer.get( row, 0 );
			case 2: return buffer.get( row, 1 );
			case 3: return buffer.get( row, 2 );
			default: return null;
			}
		}

		@Override
		public int getRowCount() 
		{
			return buffer.size();
		}
		
		@Override 
		public LeafNode getSource()
		{
			return buffer;
		}
	}
}
