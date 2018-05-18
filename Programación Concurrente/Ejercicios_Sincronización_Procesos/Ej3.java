package simulacro3;

import java.util.concurrent.*;

public class Ej3 {

	ConcurrentHashMap<String,CountDownLatch> barreras = new ConcurrentHashMap<String,CountDownLatch>();
	
	public void inicializa(){
		barreras.putIfAbsent("A", new CountDownLatch(1));
		barreras.putIfAbsent("E", new CountDownLatch(1));
		barreras.putIfAbsent("D", new CountDownLatch(2));
		barreras.putIfAbsent("B", new CountDownLatch(2));
	}
	
	public void p1() throws InterruptedException{
		barreras.get("A").await();
		System.out.print("A");
		barreras.get("D").countDown();
		barreras.get("B").await();
		System.out.print("B");
	}
	
	public void p2() throws InterruptedException{
		System.out.print("C");
		barreras.get("A").countDown();
		barreras.get("E").countDown();
		barreras.get("D").await();
		System.out.print("D");
		barreras.get("B").countDown();
	}
	
	public void p3() throws InterruptedException{
		barreras.get("E").await();
		System.out.print("E");
		barreras.get("D").countDown();
		System.out.print("F");
		barreras.get("B").countDown();
	}
	
	public void exec(){
		inicializa();
		new Thread(()->{
			try {
				p1();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				p2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(()->{
			try {
				p3();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
	}
	
	public static void main(String[]args){
		new Ej3().exec();
	}
	
}
