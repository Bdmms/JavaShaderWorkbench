package swb.ai;

import java.util.Arrays;

public class NetworkLayer 
{
	private double[] vector;
	private double[] bias;
	private double[][] weight;
	
	private int inputSize;
	private int layerSize;
	
	public NetworkLayer( NetworkLayer copy )
	{
		inputSize = copy.inputSize;
		layerSize = copy.layerSize;
		vector = new double[layerSize];
		weight = new double[layerSize][];
		bias = Arrays.copyOf( copy.bias, layerSize );
		
		for( int i = 0; i < weight.length; i++ )
			weight[i] = Arrays.copyOf( copy.weight[i], layerSize );
	}
	
	public NetworkLayer( double[][] weight, double[] bias )
	{
		inputSize = weight[0].length;
		layerSize = weight.length;
		vector = new double[layerSize];
		this.weight = weight;
		this.bias = bias;
	}
	
	public NetworkLayer( int inputSize, int layerSize )
	{
		this.inputSize = inputSize;
		this.layerSize = layerSize;
		weight = new double[layerSize][inputSize];
		vector = new double[layerSize];
		bias = new double[layerSize];
	}
	
	public NetworkLayer( int inputSize, int layerSize, boolean randomize )
	{
		this( inputSize, layerSize );
		if( randomize )
		{
			for( int i = 0; i < weight.length; i++ )
			{
				double[] wi = weight[i];
				for( int j = 0; j < wi.length; j++ )
					wi[j] = Math.random() * 2.0 - 1.0;
			}
			
			for( int i = 0; i < bias.length; i++ )
				bias[i] = Math.random() * 2.0 - 1.0;
		}
	}
	
	public int getInputSize()
	{
		return inputSize;
	}
	
	public int getSize()
	{
		return layerSize;
	}
	
	/**
	 * Processes an input vector through the layer.
	 * @param input
	 * @return
	 */
	public double[] accept( final double[] input )
	{
		if( input.length != inputSize ) throw new IndexOutOfBoundsException();
		
		for( int i = 0; i < vector.length; i++ )
		{
			double[] wi = weight[i];
			double value = bias[i];
			
			for( int j = 0; j < input.length; j++ )
			{
				value += input[j] * wi[j];
			}
			
			vector[i] = Math.tanh( value );
		}
		
		return vector;
	}
	
	/**
	 * Mutates the layer.
	 * @param probability
	 * @param limit
	 */
	public NetworkLayer mutate( double probability, double limit )
	{
		double[] nBias = new double[layerSize];
		double[][] nWeight = new double[layerSize][inputSize];
		
		for( int i = 0; i < weight.length; i++ )
		{
			double[] wn = nWeight[i];
			double[] wi = weight[i];
			
			for( int j = 0; j < wi.length; j++ )
				wn[j] = Math.random() < probability ? wi[j] + ( 2.0 * Math.random() - 1.0 ) * limit : wi[j];
		}
		
		for( int i = 0; i < bias.length; i++ )
			nBias[i] = Math.random() < probability ? bias[i] + ( 2.0 * Math.random() - 1.0 ) * limit : bias[i];
		
		return new NetworkLayer( nWeight, nBias );
	}
	
	@Override
	public NetworkLayer clone()
	{
		return new NetworkLayer( this );
	}
}
