package MonteCarloNCubeBase1;

import java.util.Random;

import Helper.Answer;
import Helper.MonteCarloMultiHelper;

public class MonteCarloNCubeBase1TestPart3 {
	static Random r = new Random();

	public static void main(String[] args) {
		System.out.println("For example, N=1,000,000");
		double answer=3.1415926535897932;
		for (int i = 2; i < 20; i++) {
			System.out.println();
			double res=pi(i,MonteCarloIntegration(i));
			double error=Math.abs(answer-res);
			System.out.println("d=" + i+" res="+res+" error="+error);	
		}
	}

	private static double MonteCarloIntegration(int d) {
		long sampleCount = 1000000;

		int threadnum = 16;
		long[] temp = new long[threadnum];
		double[] halftemp=new double[threadnum];
		MonteCarloMultiHelper[] ths = new MonteCarloMultiHelper[threadnum];
		for (int i = 0; i < threadnum; i++) {
			ths[i] = new MonteCarloMultiHelper(i, temp, d, sampleCount / threadnum,halftemp);
			ths[i].start();
		}
		for (MonteCarloMultiHelper th : ths) {
			try {
				th.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long count = 0;
		for (long i : temp) {
			count += i;
		}
		double half=0;
		for (double i : halftemp) {
			half += i;
		}
		System.out.println("count of points in the hypersphere: "+count);

		double res = ((double)count+half/2) / (double)sampleCount * Math.pow(2, d);
		return res;
	}
	
	private static double pi(int d, double res) {
		  int divisor = 1;
		  if (d % 2 == 1) {
		   res /= Math.pow(2, d / 2 + 1);
		   for (int i = 1; i <= d; i += 2) {
		    divisor *= i;
		   }
		  } else {
		   for (int i = 2; i <= d / 2; i ++) {
		    divisor *= i;
		   }
		  }
		  res *= (double)divisor;

		  return Math.pow(res, (double)1.0 / (double)(d/2));
		 }
}