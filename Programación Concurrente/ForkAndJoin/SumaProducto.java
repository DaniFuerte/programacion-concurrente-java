package fork_and_join;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SumaProducto extends RecursiveTask<Integer>{

	int vector1[];
	int vector2[];
	int ini;
	int fin;
	
	public SumaProducto(int [] vector1,int [] vector2,int ini,int fin){
		this.vector1 = vector1;
		this.vector2 = vector2;
		this.ini = ini;
		this.fin = fin;
	}
	
	
	@Override
	protected Integer compute() {
		// TODO Auto-generated method stub
		if(ini == fin){
			return vector1[ini]*vector2[ini];
		}else{
			int medio = (ini+fin)/2;
			SumaProducto izq = new SumaProducto(vector1,vector2,ini,medio);
			SumaProducto der = new SumaProducto(vector1,vector2,medio+1,fin);
			izq.fork();
			der.fork();
			return izq.join()+der.join();
		}
	}
	
	
	public static void main(String[]args){
		int vector1 [] = new int[20];
		int vector2 [] = new int[20];
		for(int i = 0; i < 20;i++){
			int n1 = (int)(Math.random()*21);
			int n2 = (int)(Math.random()*21);
			vector1[i] = n1;
			vector2[i] = n2;
		}
		
		System.out.println("Vetor1 -> "+Arrays.toString(vector1));
		System.out.println("Vector2 -> "+Arrays.toString(vector2));
		
		ForkJoinPool pool = new ForkJoinPool(10);
		SumaProducto tareas = new SumaProducto(vector1,vector2,0,19);
		System.out.println("Suma Producto -> "+pool.invoke(tareas));
		
	}

	
	
}
