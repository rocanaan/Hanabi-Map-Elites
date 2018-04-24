package Utils;

import java.util.Collection;

public class Utils {

		public static  double getMin(Collection<Double> c) {
			double min = 9999;
			for (Double element : c) {
				if ((double)element < min) {
					min = (double)element;
				}
			}
			return min;
		}
		
		
		public static <T> double getMax(Collection<T> c) {
			double max = -9999;
			for (T element : c) {
				if ( (double)element > max) {
					max = (double)element;
				}
			}
			return max;
		}
		
		public static <T> double getMean(Collection<T> c) {
			double sum = 0;
			for (T element : c) {
				sum += (double)element;
			}
			int size = c.size();
			if  (size !=0) {
				sum = sum/(double)size;
			}
			return sum;
		}
		
		public static <T> double getStandardDeviation(Collection<T> c) {
			double mean = getMean(c);
			double sum = 0;
			for (T element : c) {
				sum += ((double)element- mean)*((double)element-mean);
			}
			int size = c.size();
			if (size != 0) {
				sum = sum/(double)size;
			}
			sum = Math.sqrt(sum);
			return sum;
		}
}
