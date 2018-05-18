package hoja1;

import java.util.concurrent.Semaphore;

public class Ej2WaterMol {

	static volatile int cont_o2 = 0;
	static volatile int cont_h 	= 0;
	
	static Semaphore mutex = new Semaphore(1);
	
	static Semaphore flag_o2  = new Semaphore(0);
	static Semaphore flag_h	  = new Semaphore(0);
	
	
	public static void o2() throws InterruptedException{
		
		//while(true){
			mutex.acquire();
			cont_o2++;
			flag_o2.release();
			if(cont_h < 2){
				mutex.release();
				flag_h.acquire();
			}else{
				System.out.println("[OXIGENO] MOLECULA DE AGUA FORMADA");
				cont_o2--;
				cont_h -= 2;
				mutex.release();
			}
			//break;
		//}
		
	}
	
	public static void h() throws InterruptedException{
		
		//while(true){
		
			mutex.acquire();
			cont_h++;
			if(cont_h >= 2)
				mutex.release();
				flag_h.release();
			if(cont_o2 == 0){
				mutex.release();
				flag_o2.acquire();
			}else{
				System.out.println("[HIDROGENO] MOLECULA DE AGUA FORMADA");
				cont_h -= 2;
				cont_o2 --;
				mutex.release();
			}
			
			//break;
			
		//}
		
		
	}
	
	
	public static void exec(){
		
		for(int i = 0; i < 5;i++){
			new Thread(()->{
				try {
					o2();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}
		
		for(int i = 0; i < 10 ; i++){
			new Thread (()->{
				try {
					h();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}
		
		
	}
	
	public static void main(String[]args){
		new Ej2WaterMol();
		Ej2WaterMol.exec();
	}
	
}
