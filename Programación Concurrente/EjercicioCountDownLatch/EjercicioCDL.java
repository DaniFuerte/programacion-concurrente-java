package examenes;

import java.util.concurrent.CountDownLatch;

public class EjercicioCDL {

	private CountDownLatch latchD = new CountDownLatch(2);
	private CountDownLatch latchB = new CountDownLatch(1);
	private CountDownLatch latchG = new CountDownLatch(1);
	private CountDownLatch latchH = new CountDownLatch(1);
	private CountDownLatch latchE = new CountDownLatch(1);
	private CountDownLatch latchC = new CountDownLatch(1);

	public void p1() throws InterruptedException {
		System.out.println("A");
		latchD.countDown();
		latchB.await();
		System.out.println("B");
		latchH.countDown();
		latchE.countDown();
		latchC.await();
		System.out.println("C");

	}

	public void p2() throws InterruptedException {
		latchD.await();
		System.out.println("D");
		latchB.countDown();
		latchG.countDown();
		latchE.await();
		System.out.println("E");
		latchC.countDown();
	}

	public void p3() throws InterruptedException {
		System.out.println("F");
		latchD.countDown();
		latchG.await();
		System.out.println("G");
		latchH.await();
		System.out.println("H");
	}

	public void exec() {

		new Thread(() -> {
			try {
				p1();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				p2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			try {
				p3();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

	}

	public static void main(String[] args) {
		new EjercicioCDL().exec();
	}

}
