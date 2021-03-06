package swb.ai;

import java.util.List;

public class NetworkTester 
{
	public static void main( String[] args )
	{
		int[] structure = { 1, 1 };
		NetworkTrainer trainer = new NetworkTrainer( structure, 1.0, 0.1 )
		{
			@Override
			public double rate( double currRating, double[] output, double[] expected )
			{
				return currRating - Math.abs( output[0] - expected[0] );
			}
		};
		
		SampleGenerator generator = new SampleGenerator( 500 )
		{
			private SimpleSample sample = new SimpleSample( new double[1], new double[1] );
			
			@Override
			public DataSample getNext() 
			{
				double x = Math.random();
				sample.input[0] = x;
				sample.output[0] = 0.5 * x;
				return sample;
			}
		};
		
		long t0 = System.currentTimeMillis();
		List<NeuralNetwork[]> generations = trainer.train( 100, 100, 0.95, generator );
		NeuralNetwork network = NetworkTrainer.getBestPerformer( generations.get( generations.size() - 1 ) );
		long t1 = System.currentTimeMillis();
		
		System.out.println( String.format( "%d ms", t1 - t0 ) );
		
		for( int i = 0; i < 10; i++ )
		{
			double x = Math.random();
			double y = network.accept( new double[] { x } )[0];
			double z = 0.5 * x;
			
			System.out.println( String.format( "2 * %f = %f (expected: %f)", x, y, z ) );
		}
	}
	
	public static class SimpleSample implements DataSample
	{
		private double[] input;
		private double[] output;
		
		public SimpleSample( double[] in, double[] out )
		{
			input = in;
			output = out;
		}
		
		@Override
		public double[] getInput() 
		{
			return input;
		}

		@Override
		public double[] getOutput() 
		{
			return output;
		}
	}
}
