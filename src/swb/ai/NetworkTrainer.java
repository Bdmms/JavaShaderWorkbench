package swb.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkTrainer 
{
	private int[] netStructure;
	private double mutationChance;
	private double mutationLimit;
	
	public NetworkTrainer( int[] structure, double change, double limit )
	{
		netStructure = structure;
	}
	
	public List<NeuralNetwork[]> train( int generationSize, int numGenerations, double percentile, Iterable<DataSample> trainingData )
	{
		List<NeuralNetwork[]> generations = new ArrayList<>();
		NeuralNetwork[] current = createGeneration( generationSize );
		generations.add( current );
		
		int percentileIdx = (int)(generationSize * percentile);
		
		for( int i = 1; i < numGenerations; i++ )
		{
			System.out.println( String.format( "Generation %d:", i ) );
			
			// Process and rate generation
			evaluate( current, trainingData );
			
			// Find best performers
			Arrays.sort( current );
			NeuralNetwork[] samples = partition( current, percentileIdx );
			
			// Create next generation
			current = createGeneration( generationSize, samples );
			generations.add( current );
		}
		
		return generations;
	}
	
	public void evaluate( NeuralNetwork[] generation, Iterable<DataSample> trainingData )
	{
		for( NeuralNetwork network : generation )
		{
			for( DataSample sample : trainingData )
			{
				double[] output = network.accept( sample.getInput() );
				network.rating = rate( network.rating, output, sample.getOutput() );
			}
		}
	}
	
	public NeuralNetwork[] createGeneration( int size )
	{
		NeuralNetwork[] gen = new NeuralNetwork[size];
		
		for( int i = 0; i < size; i++ )
			gen[i] = new NeuralNetwork( netStructure );
		
		return gen;
	}
	
	public NeuralNetwork[] createGeneration( int size, NeuralNetwork[] samples )
	{
		NeuralNetwork[] gen = new NeuralNetwork[size];
		
		for( int i = 0; i < size; i++ )
			gen[i] = samples[i % samples.length].mutate( mutationChance, mutationLimit );
		
		return gen;
	}
	
	public abstract double rate( double currRating, double[] output, double[] expected );
	
	public static NeuralNetwork getBestPerformer( NeuralNetwork[] generation )
	{
		Arrays.sort( generation );
		return generation[ generation.length - 1 ];
	}
	
	public static NeuralNetwork[] partition( NeuralNetwork[] arr, int index )
	{
		NeuralNetwork[] sample = new NeuralNetwork[arr.length - index];
		
		for( int i = 0; i < sample.length; i++ )
			sample[i] = arr[index++];
		
		return sample;
	}
}
