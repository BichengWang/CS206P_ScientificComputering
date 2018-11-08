package MonteCarloNCubeBase1;

import java.util.Arrays;
import java.util.Random;

import Helper.Answer;
import Helper.MonteCarloMultiHelper;

public class MonteCarloNCubeBase1TestPart1 {
	static Random r = new Random();

	public static void main(String[] args) {
//		System.out.println("Monte Carlo Integration:");
//		for (int i = 1; i < 10; i++) {
//			System.out.println("d=" + i + " answer:" + Answer.answer(i));
//			int[] res = new int[10];
//			for (int j = 0; j < 10; j++) {
//				System.out.println("no." + j + " test:");
//				res[j] = MonteCarloIntegration(i);
//			}
//			Arrays.sort(res);
//			System.out.println("Conclusion sample count: 4*10^" + res[0]);
//		}

		System.out.println("Cube based Integration:");
		for (int i = 1; i < 4; i++) {
			System.out.println("d=" + i + " answer:" + Answer.answer(i));
			CubebasedIntegration(i);
		}
	}

	private static int MonteCarloIntegration(int d) {
		double ans = Answer.answer(d);
		long sampleCount, start = 0, end = 0;
		double res = 0.0;
		for (sampleCount = 4000; Math.abs(res - ans) > 0.001; sampleCount *= 10) {
			int threadnum = 16;
			long[] temp = new long[threadnum];
			double[] halftemp = new double[threadnum];
			MonteCarloMultiHelper[] ths = new MonteCarloMultiHelper[threadnum];
			start = System.currentTimeMillis();
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
			end = System.currentTimeMillis();
			long count = 0;
			double half = 0;
			for (long i : temp) {
				count += i;
			}
			for (double i : halftemp) {
				half += i;
			}
			res = ((double) count + half / 2) / (double) sampleCount * Math.pow(2, d);
		}

		int zero = 0;
		while (sampleCount > 4) {
			sampleCount /= 10;
			zero++;
		}

		System.out.println("Time: " + (end - start) + "ms sampleCount: 4*10^" + (zero - 1));
		return zero - 1;
	}

	private static void CubebasedIntegration(int d) {
		double ans = Answer.answer(d);
		long sampleCount, start = 0, end = 0;
		double res = 0.0;
		for (sampleCount = 64; Math.abs(res - ans) > 0.001; sampleCount *= 2) {
			start = System.currentTimeMillis();
			long[] count = new long[1];
			double[] half = new double[1];
			helper(0, d, count, sampleCount, half);
			end = System.currentTimeMillis();
			res = ((double) count[0] + half[0] / 2) / (double) Math.pow(sampleCount, d) * Math.pow(2, d);
			System.out.println(sampleCount);
		}
		System.out.println(
				"CubebasedIntegration" + "d: " + d + " Time: " + (end - start) + "ms sampleCount: " + sampleCount / 2);
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
