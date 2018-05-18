package count_down;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class EjercicioExamen {

	ConcurrentHashMap<String,CountDownLatch> flag = new ConcurrentHashMap<>();
	
	public void inicializar(){
		flag.putIfAbsent("D", new CountDownLatch(2));
		flag.putIfAbsent("B", new CountDownLatch(1));
		flag.putIfAbsent("G", new CountDownLatch(1));
		flag.putIfAbsent("H", new CountDownLatch(1));
		flag.putIfAbsent("E", new CountDownLatch(1));
		flag.putIfAbsent("C", new CountDownLatch(1));
	}
	
	public void p1() throws InterruptedException{
		System.out.print("A");
		flag.get("D").countDown();
		flag.get("B").await();
		System.out.print("B");
		flag.get("H").countDown();
		flag.get("E").countDown();
		flag.get("C").await();
		System.out.print("C");
	}
	
	public void p2() throws InterruptedException{
		flag.get("D").await();
		System.out.print("D");
		flag.get("B").countDown();
		flag.get("G").countDown();
		flag.get("E").await();
		System.out.print("E");
		flag.get("C").countDown();
	}
	
	public void p3() throws InterruptedException{
		System.out.print("F");
		flag.get("D").countDown();
		flag.get("G").await();
		System.out.print("G");
		flag.get("H").await();
		System.out.print("H");
	}
	
	public void exec(){
		inicializar();
		Thread th1 = new Thread(()->{
			try {
				p1();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		Thread th2 = new Thread(()->{
			try {
				p2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		Thread th3 = new Thread(()->{
			try {
				p3();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		th1.start();
		th2.start();
		th3.start();
		
	}
	
	public static void main(String[]args){
		new EjercicioExamen().exec();
	}
	
}
