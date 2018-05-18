package blanca_nieves;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
//import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Snow_White_v2 {

	
	static final int ENANOS = 7;
	static final int SILLAS = 4;
	
	/**
	Semaphore sillas = new Semaphore(SILLAS);
	Semaphore dwarfs = new Semaphore(0);
	Semaphore snow_white = new Semaphore(0);
	**/
	
	/** Podría funcionar igual con sillas = new Semaphore(4);**/
	
	BlockingQueue<Integer> sillas = new ArrayBlockingQueue<>(SILLAS);
	BlockingQueue<Exchanger<Integer>> mesa = new ArrayBlockingQueue<>(SILLAS);
	
	public void dwarf(int pid) throws InterruptedException{
		Exchanger<Integer> canal = new Exchanger<>();
		while(true){
			System.out.println("[Enano "+pid+"] Esperando a sentarme a la mesa");
			sillas.put(1);
			mesa.put(canal);
			System.out.println("[Enano "+pid+"] Sentado a la mesa");
			canal.exchange(pid);
			System.out.println("[Enano "+pid+"] Esperando comida");
			canal.exchange(0);
			System.out.println("[Enano "+pid+"] Comiendo");
			Thread.sleep(1000);
			System.out.println("[Enano "+pid+"] He terminado de comer");
			sillas.take();
			System.out.println("[Enano "+pid+"] Me voy a trabajar");
			Thread.sleep(10000);
		}
	}
	
	public void snoWhite() throws InterruptedException{
		while(true){
			//System.out.println("[Blancanieves]: Esperando a enanitos");
			Exchanger<Integer> canal = mesa.poll(1, TimeUnit.SECONDS);
			if(canal != null){
				int enano = canal.exchange(0);
				System.out.println("[Blancanieves]: Preparando comida para enanito "+enano);
				Thread.sleep(1000);
				System.out.println("[Blancanieves]: Sirviendo comida");
				canal.exchange(1);
			}else{
				System.out.println("[Blancanieves]: Me voy a pasear");
				Thread.sleep(5000);
			}
			
		}
	}
	
	public void exec(){
		
		new Thread(()->{
			try {
				snoWhite();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		for(int i = 1; i <= ENANOS;i++){
			int pid = i;
			new Thread(()->{
				try {
					dwarf(pid);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}
		
	}
	
	public static void main(String[]args){
		new Snow_White_v2().exec();
	}
	
}
