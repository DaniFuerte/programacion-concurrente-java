package blanca_nieves;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Snow_White {

	
	static final int ENANOS = 7;
	static final int SILLAS = 4;
	
	Semaphore sillas = new Semaphore(SILLAS);
	Semaphore dwarfs = new Semaphore(0);
	Semaphore snow_white = new Semaphore(0);
	
	public void dwarf(int pid) throws InterruptedException{
		while(true){
			System.out.println("[Enano "+pid+"] Esperando a sentarme a la mesa");
			sillas.acquire();
			System.out.println("[Enano "+pid+"] Sentado a la mesa");
			snow_white.release();
			System.out.println("[Enano "+pid+"] Esperando comida");
			dwarfs.acquire();
			System.out.println("[Enano "+pid+"] Comiendo");
			Thread.sleep(1000);
			System.out.println("[Enano "+pid+"] He terminado de comer");
			sillas.release();
			System.out.println("[Enano "+pid+"] Me voy a trabajar");
			Thread.sleep(10000);
		}
	}
	
	public void snoWhite() throws InterruptedException{
		while(true){
			System.out.println("[Blancanieves]: Esperando a enanitos");
			if(!snow_white.tryAcquire(1, TimeUnit.SECONDS)){
				System.out.println("[Blancanieves]: Me voy a pasear");
				Thread.sleep(5000);
			}else{
				System.out.println("[Blancanieves]: Cocinando");
				Thread.sleep(1000);
				System.out.println("[Blancanieves]: Sirviendo comida");
				dwarfs.release();
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
		new Snow_White().exec();
	}
	
	
}
