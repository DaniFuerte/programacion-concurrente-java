package fork_and_join;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MaxArray extends RecursiveTask<Integer> {

	int vector[];
	int ini;
	int fin;
	
	public MaxArray(int[] vector,int ini,int fin){
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
			MaxArray parte_izquierda = new MaxArray(vector,ini,medio);
			MaxArray parte_derecha	= new MaxArray(vector,medio+1,fin);
			parte_izquierda.fork();
			parte_derecha.fork();
			return(int)(Math.max(parte_izquierda.join(), parte_derecha.join()));
		}
		
	}
	
	public static void main(String[]args){
		int [] vector = new int[10];
		
		for(int i = 0; i < vector.length;i++){
			vector[i] = (int)(Math.random()*101);
		}
		
		System.out.println("Vector -> "+Arrays.toString(vector));
		ForkJoinPool pool = new ForkJoinPool(4);
		MaxArray tarea = new MaxArray(vector,0,vector.length-1);
		System.out.println("Máximo -> "+pool.invoke(tarea));
		
	}
	

}
