package Helper;

import java.util.Random;

public class MonteCarloMultiHelper extends Thread {

	private int ip;
	private long[] temp;
	private int d;
	private long sampleCount;
	private Random r;
	private double[] halftemp;

	public MonteCarloMultiHelper(int i, long[] temp, int d, long sampleCount, double[] halftemp) {
		this.ip = i;
		this.temp = temp;
		this.d = d;
		this.sampleCount = sampleCount;
		r = new Random();
		this.halftemp = halftemp;
	}

	@Override
	public void run() {
		long count = 0;
		double half = 0;
		for (int j = 0; j < sampleCount; j++) {
			double sum = 0;
			for (int i = 0; i < d && sum <= 1; i++) {
				double randomValue = r.nextDouble();
				sum += randomValue * randomValue;
				if (sum > 1) {
					break;
				}
			}
			if (sum < 1) {
				count++;
			}
			if (sum == 1) {
				half++;
			}
		}
		temp[ip] = count;
		halftemp[ip] = half;
	}
}



