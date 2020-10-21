
import com.jogamp.opengl.GL3;

@SuppressWarnings("serial")
public class VertexTable extends EditorTable
{
	public VertexTable() 
	{
		super( new VertexTableModel() );
	}

	@Override
	public void bindAll(GL3 gl, ShaderProgram program) 
	{
		
	}

	@Override
	protected void add() 
	{
		
	}

	@Override
	protected void remove() 
	{
		
	}

	private static class VertexTableModel extends EditorTableModel<Vertex>
	{
		public VertexTableModel()
		{
			super( new TableColumn[] {
					new TableColumn( "Index", Integer.class, false )
			} );
		}
		
		public void addRow( Vertex vertex )
		{
			addRow( vertex );
		}
		
		@Override
		public void bindAll( GL3 gl, ShaderProgram program ) 
		{
			
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) 
		{
			//Vertex vertex = _rows.get( row );
			
			switch( col )
			{
			case 0: break;
			}
		}

		@Override
		public Object getValueAt( int row, int col ) 
		{
			switch( col )
			{
			case 0: return row;
			default: return null;
			}
		}
	}
}
