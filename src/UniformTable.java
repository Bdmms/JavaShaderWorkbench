import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import com.jogamp.opengl.GL3;

@SuppressWarnings("serial")
public class UniformTable extends EditorTable
{
	private JComboBox<GLDataType> _types = new JComboBox<GLDataType>( GLDataType.values() );
	private JTextField _textField = new JTextField();
	
	public UniformTable()
	{
		super( new UniformTableModel() );
		
		_textField.setFont( ShaderEditor.FONT );
		_textField.setBorder( null );
		_types.setFont( ShaderEditor.FONT );
	}
	
	@Override
	public void bindAll ( GL3 gl, ShaderProgram program )
	{
		_model.bindAll( gl, program );
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
	
	private static class Uniform
	{
		public String name;
		public GLDataType type;
		public Object value;
		
		public Uniform( String name, String value, GLDataType type )
		{
			this.name = name;
			this.value = value;
			this.type = type;
		}
		
		public void setValue( String val )
		{
			// TODO
			switch( type )
			{
			default: value = val; break;
			}
		}
	}
	
	private static class UniformTableModel extends EditorTableModel<Uniform>
	{
		public UniformTableModel()
		{
			super( new TableColumn[] {
					new TableColumn( "Uniform", String.class, true ),
					new TableColumn( "Type", GLDataType.class, true ),
					new TableColumn( "Value", String.class, true )	
			} );
		}
		
		public void addRow()
		{
			addRow( new Uniform( "", "", GLDataType.VEC1 ) );
		}
		
		public void bindAll( GL3 gl, ShaderProgram program ) 
		{
			for( Uniform uniform : _rows )
			{
				if( uniform.type == GLDataType.SAMP2D )
				{
					try
					{
						int value = Integer.parseInt( uniform.value.toString() );
						int loc = gl.glGetUniformLocation( program.getID(), uniform.name );
						gl.glUniform1i( loc, value );
					}
					catch( NumberFormatException e ) {}
				}
				
				System.out.println( uniform.name + ": " + uniform.value.toString() );
			}
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			switch( col )
			{
			case 0: _rows.get( row ).name = value.toString(); break;
			case 1: _rows.get( row ).type = (GLDataType) value; break;
			case 2: _rows.get( row ).setValue( value.toString() ); break;
			}
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return _rows.get( row ).name;
			case 1: return _rows.get( row ).type;
			case 2: return _rows.get( row ).value;
			default: return null;
			}
		}
	}
}
