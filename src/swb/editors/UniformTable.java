package swb.editors;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import swb.GLDataType;
import swb.GLNode;
import swb.UniformList;

@SuppressWarnings("serial")
public class UniformTable extends EditorTable
{
	private JComboBox<GLDataType> _types = new JComboBox<GLDataType>( GLDataType.values() );
	private JTextField _textField = new JTextField();
	
	public UniformTable( String title, UniformList uniforms )
	{
		super( title, new UniformTableModel( uniforms ) );
		
		_textField.setFont( EditorTabs.FONT );
		_textField.setBorder( null );
		_types.setFont( EditorTabs.FONT );
	}
	
	@Override
	protected void add() 
	{
		((UniformTableModel)_model).addRow();
	}

	@Override
	protected void remove() 
	{
		_model.removeRow();
	}
	
	@Override
	public TableCellEditor getCellEditor( int row, int column )
	{
		switch( column )
		{
		case 1: 
			return new DefaultCellEditor( _types );
		
		case 0:
		case 2:
			_textField.setText( _model.getValueAt( row, column ).toString() );
			return new DefaultCellEditor( _textField );

		default: 
			return super.getCellEditor( row, column );
		}
	}
	
	private static class UniformTableModel extends ObserverTableModel
	{
		private UniformList uniforms;
		
		public UniformTableModel( UniformList uniforms )
		{
			super( new TableColumn[] {
					new TableColumn( "Uniform", String.class, true ),
					new TableColumn( "Type", GLDataType.class, true ),
					new TableColumn( "Value", String.class, true )	
			} );
			
			this.uniforms = uniforms;
		}
		
		public void addRow()
		{
			uniforms.add( "", GLDataType.VEC1 );
		}
		
		@Override
		public void removeRow() 
		{
			uniforms.removeLast();
		}
		
		@Override
		public void setValueAt( Object value, int row, int col ) 
		{
			switch( col )
			{
			case 0: uniforms.setName( row, value.toString() ); break;
			case 1: uniforms.swapType( row, (GLDataType)value );  break;
			case 2: uniforms.setValue( row, value.toString() ); break;
			}
			updateTable( new TableModelEvent( this, row ) );
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return uniforms.get( row ).name;
			case 1: return uniforms.get( row ).type;
			case 2: return uniforms.get( row ).getValue();
			default: return null;
			}
		}

		@Override
		public int getRowCount() 
		{
			return uniforms.size();
		}
		
		@Override 
		public GLNode getSource()
		{
			return uniforms;
		}
	}
}
