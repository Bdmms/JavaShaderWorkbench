package swb.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NeuralNetwork extends ArrayList<NetworkLayer> implements Comparable<NeuralNetwork>
{
	private static final long serialVersionUID = 1L;
	
	public double rating = 0.0;
	
	public NeuralNetwork() 
	{
		super();
	}
	
	public NeuralNetwork( int[] structure )
	{
		for( int i = 1; i < structure.length; i++ )
			add( new NetworkLayer( structure[i - 1], structure[i], true ) );
	}
	
	/**
	 * Processes an input vector through the network.
	 * @param input
	 * @return
	 */
	public double[] accept( double[] input )
	{
		for( NetworkLayer layer : this )
			input = layer.accept( input );
		
		return input;
	}

	/**
	 * Mutates the network.
	 * @param probability
	 * @param limit
	 */
	public NeuralNetwork mutate( double probability, double limit )
	{
		NeuralNetwork mutated = new NeuralNetwork();
		
		for( NetworkLayer layer : this )
			mutated.add( layer.mutate( probability, limit ) );
		
		return mutated;
	}
	
	@Override
	public NeuralNetwork clone()
	{
		NeuralNetwork copy = new NeuralNetwork();
		
		for( NetworkLayer layer : this )
			copy.add( layer.clone() );
		
		return copy;
	}
	
	public static void main( String[] args )
	{
		int[] structure = { 4, 1000, 1000, 1000, 1000, 1000, 1 };
		NeuralNetwork network = new NeuralNetwork( structure );
		
		double[] input = { 1.0, 1.0, 1.0, 1.0 };
		
		long t0 = System.currentTimeMillis();
		double[] output = network.accept( input );
		long t1 = System.currentTimeMillis();
		
		String text = String.join( " ", Arrays.stream( output ).mapToObj( val -> String.valueOf( val ) ).collect( Collectors.toList() ) );
		System.out.println( text );
		System.out.println( String.format( "%d ms", t1 - t0 ) );
	}

	@Override
	public int compareTo( NeuralNetwork network ) 
	{
		return Double.compare( rating, network.rating );
	}
}
