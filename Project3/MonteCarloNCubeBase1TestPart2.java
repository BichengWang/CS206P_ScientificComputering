package MonteCarloNCubeBase1;

import java.util.Random;

import Helper.Answer;
import Helper.MonteCarloMultiHelper;

public class MonteCarloNCubeBase1TestPart2 {
	static Random r = new Random();

	public static void main(String[] args) {
		for (int i = 2; i < 40; i++) {
			System.out.println("d=" + i);
			double res1 = MonteCarloIntegration(i);
			double res2 = CubebasedIntegration(i);
			double answer = Answer.answer(i);
			double rerror1 = Math.abs(res1 - answer) / answer;
			double rerror2 = Math.abs(res2 - answer) / answer;
			double absolutediff = res1 - res2;
			System.out.println(
					"absolute diff:" + Math.abs(absolutediff) + "  relative diff：" + Math.abs(absolutediff / answer));
			System.out.println("Monte Carlo Integration relative error:" + Math.abs(rerror1));
			System.out.println("Cube based Integration relative error:" + Math.abs(rerror2));
			System.out.println("Res 1:" + res1);
			System.out.println("answer:" + answer);
			System.out.println();
		}
	}

	private static double MonteCarloIntegration(int d) {
		long sampleCount = 1000000;

		int threadnum = 16;
		long[] temp = new long[threadnum];
		double[] halftemp = new double[threadnum];
		MonteCarloMultiHelper[] ths = new MonteCarloMultiHelper[threadnum];
		for (int i = 0; i < threadnum; i++) {
			ths[i] = new MonteCarloMultiHelper(i, temp, d, sampleCount / threadnum, halftemp);
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
		double half = 0;
		for (double i : halftemp) {
			half += i;
		}
		System.out.println("count: " + count);

		double res = ((double) count + half / 2) / (double) sampleCount * Math.pow(2, d);
		return res;
	}

	private static double CubebasedIntegration(int d) {
		int sampleCount = (int) Math.round(Math.pow(1000000, (double) 1.0 / (double) d));
		int sampleCountError = (int) Math.abs(1000000 - Math.pow(sampleCount, d));
		System.out.println("Cube based Integration sample count:" + Math.pow(sampleCount, d) + " sample count error:"
				+ sampleCountError);
		long[] count = new long[1];
		double[] half = new double[1];
		helper(0, d, count, sampleCount, half);
		double res = ((double) count[0] + half[0] / 2) / (double) Math.pow(sampleCount, d) * Math.pow(2, d);
		return res;
	}

	private static void helper(double sum, int remain, long[] count, long sampleCount, double[] half) {
		if (sum > 1)
			return;
		if (remain == 0) {
			if (sum < 1) {
				count[0]++;
			}
			if (sum == 1) {
				half[0]++;
			}
			return;
		}
		double unit = (double) 1.0 / sampleCount;
		for (int j = 0; j < sampleCount; j++) {
			double temp = j * unit;
			helper(sum + temp * temp, remain - 1, count, sampleCount, half);
		}
	}

}
