package simulacro3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class MyExecutor implements Executor {

	
	//private ExecutorService threads;
	private BlockingQueue<Runnable> tasks;
	
	public MyExecutor(int pool_tareas, int pool_hilos){
		//this.threads = Executors.newFixedThreadPool(pool_hilos);
		this.tasks = new ArrayBlockingQueue<>(pool_tareas);
		for(int i = 0; i < pool_hilos;i++){
			int pid = i;
			Thread thread = new Thread(()->procesar_tarea(pid));
			thread.start();
		}
		
	}
	
	public void procesar_tarea(int pid){
		try {
			Runnable task = tasks.take();
			System.out.println("[Proceso "+pid+"] Realizando tarea");
			task.run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void execute(Runnable r) {
		// TODO Auto-generated method stub
		try {
			this.tasks.put(r);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
