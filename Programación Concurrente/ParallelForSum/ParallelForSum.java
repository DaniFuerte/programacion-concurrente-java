package parallel_for_sum;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ParallelForSum {

	private static final int CORES = 4;
	
	public double productoPunto(double[]a, double[]b, int size){
		double c = parallelForSum(size, i-> a[i]*b[i]);
		return c;
	}
	
	public double parallelForSum(int size, Function<Integer, Double> iteration){
		ExecutorService exec = Executors.newFixedThreadPool(CORES);
		CompletionService<Double> c = new ExecutorCompletionService<>(exec);
		
		for(int i = 0; i < size; i++){
			int iter = i;
			c.submit(()->iteration.apply(iter));
		}
		double sum = 0;
		
		for(int i = 0; i < size; i++){
			try {
				sum += c.take().get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return sum;
	}
	
	private void exec(){
		double []  a = {4,5,2,1,2,3,4};
		double []  b = {3,2,3,4,2,1,2};
		
		double suma = productoPunto(a,b,a.length);
		
		System.out.println("Suma : "+suma);
		
	}
	
	
	public static void main(String[]args){
		new ParallelForSum().exec();
	}
	
	
	
}
