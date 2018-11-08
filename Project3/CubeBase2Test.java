package CubeBase2;

import java.util.LinkedList;

import Helper.Answer;

class FactorialHelper {
    // attention overflow
    public final static int Fsize = 18;
    public static int[] F = new int[Fsize];
    public static int[][] C = new int[Fsize][Fsize];
    static {
        F[1] = 1;
        
        for (int i = 2; i < Fsize; i++) {
            F[i] = F[i - 1] * i;
            C[i][0] = 1;
            C[i][i] = 1;
        }
        
        for (int i = 0; i < Fsize; i++) {
            for (int j = 1; j < i; j++) {
                C[i][j] = F[i] / F[j] / F[i - j];
            }
        }
    }
}

class CubeType {
	public static final int UNKNOW = 0;
	public static final int INSIDE = 1;
	public static final int OUTSIDE = 2;
	public static final int EDGE = 3;
}

class CubeBaseArr {
	public static int generateNum;
	public static int dim;
	double[] arr;

	/**
	 * 
	 * @param len
	 */
	CubeBaseArr(int dimension) {
		dim = dimension;
		arr = new double[dimension];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 0.5;
		}
		generateNum = (int)Math.pow(2, dimension);
	}

	protected CubeBaseArr(double arr[]) {
		this.arr = new double[arr.length];
		for (int i = 0; i < arr.length; i++) {
			this.arr[i] = arr[i];
		}
	}

	/**
	 * give a grain to decide if arr inside the hypersphere
	 * 
	 * @param grain
	 * @return
	 */
	public int isInside(double grain) {
		double highsum = 0.0;
		double lowsum = 0.0;
		for (int i = 0; i < arr.length; i++) {
			double temp1 = (arr[i] + grain);
			highsum += temp1 * temp1;
			double temp2 = (arr[i] - grain);
			lowsum += temp2 * temp2;
		}
		if (highsum < 1.0) {
			return CubeType.INSIDE;
		} else if (lowsum > 1.0) {
			return CubeType.OUTSIDE;
		} else {
			return CubeType.EDGE;
		}
	}
	
	public LinkedList<CubeBaseArr> generate(double grain) {
		LinkedList<CubeBaseArr> cbas = new LinkedList<CubeBaseArr>();
		for (int i = 0; i < generateNum; i++) {
			CubeBaseArr cur = new CubeBaseArr(arr);
			for (int d = 0, shift = dim - 1; d < dim; d++, shift--) {// dimension
				if ((i & (1 << shift)) == 0) {
					cur.arr[d] -= grain;
				} else {
					cur.arr[d] += grain;
				}
			}
			cbas.add(cur);
		}
		return cbas;
	}
}

class CubeBaseProxy {
	public double sampleCount;
	private int dimension;
	private double curGrain;
	private double lastGrain;
	private double curVolume;
	private double curWeight;
	private LinkedList<CubeBaseArr> cbas;

	CubeBaseProxy(int dimension) {
		this.dimension = dimension;
		lastGrain = 1;
		curGrain = 0.5;
		cbas = new LinkedList<CubeBaseArr>();
		CubeBaseArr cba = new CubeBaseArr(dimension);
		cbas.add(cba);
		curWeight = 1.0;
		sampleCount = 0.0;
	}

	/**
	 * for old one
	 * 
	 * @return
	 */
	public double pushFoward() {
		int size = cbas.size();
		lastGrain /= 2;
		curGrain /= 2;
		long count = 0;
		for (int i = 0; i < size; i++) {
			CubeBaseArr cur = cbas.removeFirst();
			sampleCount++;
			int temp = cur.isInside(lastGrain);
			if (temp == CubeType.EDGE) {
				cbas.addAll(cur.generate(curGrain));
			} else if (temp == CubeType.INSIDE) {
				count++;
			}
		}
		curVolume += curWeight * count;
		curWeight /= Math.pow(2, dimension);
		return curVolume;
	}
}

/**
 * 
 * @author wangbicheng
 * Cube-Base Test
 */
public class CubeBase2Test {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		int dimension = 3;
		int grainLevel = 12; // the sample number is about (2 ^ dimension) ^ grainLevel
		double estimateVolume = Math.pow(2, dimension);
		CubeBaseProxy cbp = new CubeBaseProxy(dimension);
		
		for(int i = 0; i < grainLevel - 1; i++) {
			cbp.pushFoward();
		}
		estimateVolume *= cbp.pushFoward();
		
		long end = System.currentTimeMillis();
		System.out.println("estimate sample: " + Math.pow(Math.pow(2, dimension), grainLevel));
		System.out.println("actually sample:" + cbp.sampleCount);
		System.out.println("standard volume:" + Answer.answer(dimension));
		System.out.println("estimate volume:" + estimateVolume);
		System.out.println("cost time:" + (end - start) / 1000 + "s");
		
		return;
	}
}
