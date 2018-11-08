package OurModifiedMethod;

import java.util.Random;

import Helper.Answer;

/**
 * wtf because we can only submit file by file and limit to 10...
 * @author wangbicheng
 *
 */

class CubeBaseMultiHelper extends Thread {

	public double[] ds;
	public double[] newds;
	public int len;
	public int end;
	public int startIndex;
	public int total;
	public int ip;
	
	public CubeBaseMultiHelper() {}
	public CubeBaseMultiHelper(int i, int startIndex, int len, double[] ds) {
		this.ip = i;
		this.startIndex = startIndex;
		this.len = len;
		this.end = Math.min(ds.length, startIndex + len);
		this.ds = ds;
		this.newds = new double[ds.length];
		this.total = ds.length;
	}

	@Override
	public void run() {
		for (int i = startIndex; i < end; i++) {
			double tempX = ds[i];
			if(tempX == 0) continue;
			for (int j = 0; j < this.total - i; j++) {
				newds[i + j] += tempX * ds[j];
			}
		}
	}

}
class CubeBaseModArr {
	private int zoom;
	public double[] arr;

	public CubeBaseModArr(int zoom) {
		super();
		this.zoom = zoom;
		this.arr = new double[zoom];
	}

	/**
	 * value: 0 ~ ZOOM
	 * 
	 * @param value
	 */
	public void insertValue(int value, double freq) {
		this.arr[value] += freq;
	}

	public double sumFreq() {
		double sumFreq = 0.0;
		for (int i = 0; i < zoom; i++) {
			sumFreq += arr[i];
		}
		return sumFreq;
	}
}

class ModifiedMethodProxy {
	protected int THREAD_MAX;
	protected int basicZoom;
	protected int sampleTime;
	protected int dimension;
	protected CubeBaseModArr mca;
	protected CubeBaseModArr newMca;
	protected double[] freq;

	public ModifiedMethodProxy(int dimension, int zoom, int threadNum) {
		this.basicZoom = zoom;
		this.dimension = dimension;
		this.THREAD_MAX = threadNum;
		mca = new CubeBaseModArr(zoom);
		initSample();
	}
	
	public ModifiedMethodProxy(int dimension, int zoom, int sampleTime, int threadNum) {
		this.basicZoom = zoom;
		this.sampleTime = sampleTime;
		this.dimension = dimension;
		this.THREAD_MAX = threadNum;
		mca = new CubeBaseModArr(zoom);
		randomInitSample();
	}

	public void insertSample(double value) {
		value = value * value * basicZoom;
		mca.insertValue((int) value, 1);
	}

	public void initSample() {
		for (long i = 0; i < basicZoom; i++) {
			mca.arr[(int)Math.round((double)i * i / basicZoom)] += 1.0 / basicZoom;
		}
	}
	
	public void randomInitSample() {
		Random r = new Random();
		int time = basicZoom * sampleTime;
		for (long i = 0; i < time; i++) {
			double temp = r.nextDouble();
			mca.arr[(int)Math.floor(temp * temp * basicZoom)] += 1.0 / time;
		}
	}

	private void generate() {
		newMca = new CubeBaseModArr(this.basicZoom);
		for (int i = 0; i < basicZoom; i++) {
			double tempX = mca.arr[i];
			for (int j = 0; j < basicZoom - i; j++) {
				newMca.insertValue(i + j, tempX * mca.arr[j]);
			}
		}
		mca = newMca;
	}

	public double sumFreq() {
		int level = dimension;
		while (level > 2) {
			long start = System.currentTimeMillis();
			mutliGenerate();
			level /= 2;
			long end = System.currentTimeMillis();
		}
		long start = System.currentTimeMillis();
		doubleDimension();
		long end = System.currentTimeMillis();
		double res = newMca.sumFreq();
		return res;
	}

	private void mutliGenerate() {
		newMca = new CubeBaseModArr(this.basicZoom);
		
		CubeBaseMultiHelper[] ths = new CubeBaseMultiHelper[THREAD_MAX];
		
		int len = this.basicZoom / THREAD_MAX + 1;
		for (int i = 0; i < THREAD_MAX; i ++) {
			ths[i] = new CubeBaseMultiHelper(i, i * len, len, mca.arr);
			ths[i].start();
		}
		try {
			for (int i = 0; i < THREAD_MAX; i++) {
				ths[i].join();
			}                                  	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < this.basicZoom; i++) {
			int threadEnd = i / len + 1;
			for (int j = 0; j < threadEnd; j++) {
				newMca.arr[i] += ths[j].newds[i];
			}
		}
		mca = newMca;
	}

	/**
	 * can double the dimension according to lower dimension distribution
	 */
	private void doubleDimension() {
		this.freq = new double[this.basicZoom];
		double freqSumD8 = 0;
		for (int i = 0; i < this.basicZoom; i++) {
			freqSumD8 += mca.arr[i];
			this.freq[i] = freqSumD8;
		}
		for (int i = 0; i < this.basicZoom; i++) {
			mca.arr[i] *= this.freq[this.basicZoom - i - 1];
		}
		newMca = mca;
	}

	public double volume() {
		double temp = sumFreq();
		temp = temp * Math.pow(2.0, dimension);
		return temp;
	}
}


public class ModifiedTest {

	public static void main(String[] args) {
		int dimension = 16; // 8
		/**
		 * please according the dimension change the gap
		 */
		int gap = (int) Math.pow(2, 16); // gap means divide 1 by 2^gap;
		int sampleTime = (int) Math.pow(2, 10); // sampe number = sampleTime * 2 ^ gap
		int thread = 32; // 32 best
		/**
		 * this is for monte carlo
		 */
		// monteCarloTest(dimension, gap, sampleTime, thread);
		/**
		 * this is for cube base
		 */
		cubeBasedTest(dimension, gap, thread);
	}

	private static double monteCarloTest(int d, int g, int sampleTime, int t) {
		int time = 100;
		int count = 0;
		double result = 0;
		double errAvg = 0;
		for (int i = 0; i < time; i++) {
			double err = Math.abs(testStart(d, g, sampleTime, t, true));
			errAvg += err;
			if (err < 0.0005) {
				count++;
			}
		}
		errAvg /= time;
		result = count;
		result /= time;
		System.out.println("percent: " + result + " err avg: " + errAvg);
		return result;
	}

	private static double cubeBasedTest(int d, int g, int t) {
		return testStart(d, g, 1, t, false);
	}

	/**
	 * return is in the 4 percision region
	 * 
	 * @param dimension
	 * @param gap
	 * @param thread
	 * @param openMonteCarlo
	 * @return
	 */
	private static double testStart(int dimension, int gap, int sampleTime, int thread, boolean openMonteCarlo) {
		long start = System.currentTimeMillis();

		int sampleNum = gap;
		if (openMonteCarlo) {
			sampleNum = sampleTime * gap;
		}

		double mR = 0.0;
		ModifiedMethodProxy mcp;
		if (openMonteCarlo) {
			mcp = new ModifiedMethodProxy(dimension, gap, sampleTime, thread);
		} else {
			mcp = new ModifiedMethodProxy(dimension, gap, thread);
		}
		
		mR = mcp.volume();
		double sR = Answer.answer(dimension);

		if (!openMonteCarlo) {
			System.out.println("sample number: " + sampleNum);
			System.out.println("Estimate Value: " + mR);
			System.out.println("Standard Value: " + sR);
		}

		long end = System.currentTimeMillis();
		long time = (end - start);
		if (time < 10000) {
			System.out.println("Time: " + (time) + "ms");
		} else {
			System.out.println("Time: " + (time / 1000) + "s");
		}

		return sR - mR;
	}

}
