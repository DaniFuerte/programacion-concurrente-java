package fork_and_join;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MinArray extends RecursiveTask<Integer> {

	
	int vector [] ;
	int ini ;
	int fin;
	
	public MinArray(int [] vector, int ini, int fin){
		this.vector = vector;
		this.ini = ini;
		this.fin = fin;
	}
	
	
	@Override
	protected Integer compute() {
		// TODO Auto-generated method stub
		if(ini == fin){
			return vector[ini];
		}else{
			int medio = (ini+fin)/2;
			MinArray parte_izquierda = new MinArray(vector,ini,medio);
			MinArray parte_derecha	 = new MinArray(vector,medio+1,fin);
			parte_izquierda.fork();
			parte_derecha.fork();
			return Math.min(parte_izquierda.join(), parte_derecha.join());
		}
	}
	
	public static void main(String[]args){
		int vector [] = new int [21];
		for(int i = 0; i < vector.length;i++)
			vector[i] = (int)(Math.random()*101);
		
		System.out.println("Vector -> "+Arrays.toString(vector));
		ForkJoinPool pool = new ForkJoinPool(6);
		MinArray tarea = new MinArray(vector,0,vector.length-1);
		System.out.println("Minimo -> "+pool.invoke(tarea));
		
	}

	
	
}
