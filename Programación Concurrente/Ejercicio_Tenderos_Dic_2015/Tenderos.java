package tenderosv3;

import java.util.concurrent.*;

public class Tenderos {

	static final int TENDEROS = 3;
	static final int CLIENTES = 5;
	
	BlockingQueue<Exchanger<Integer>> tenderos = new ArrayBlockingQueue<>(TENDEROS);
	
	
	
	public Exchanger<Integer> esperaTenderoLibre() throws InterruptedException{
		
		return tenderos.take();
		
	}
	
	public int pideProductoATendero(Exchanger<Integer> canal) throws InterruptedException{
		return canal.exchange(0);
	}
	
	public void devolverProductoATendero(Exchanger<Integer> canal) throws InterruptedException{
		
		canal.exchange(0);
		
	}
	
	public void pagaATendero(int precio, Exchanger<Integer>canal) throws InterruptedException{
		
		canal.exchange(precio);
		
	}
	
	public int esperaCliente(int pid, Exchanger<Integer> canal) throws InterruptedException{
		
		tenderos.put(canal);
		return canal.exchange(pid);

		
	}
	
	public void indicaPrecioProducto(int precio,Exchanger<Integer> canal) throws InterruptedException{
		
		canal.exchange(precio);
		
	}
	
	public int cobraORecogeProducto(Exchanger<Integer> canal) throws InterruptedException{
		
		return canal.exchange(0);
		
	}
	
	
	public void cliente(int pid) throws InterruptedException{
		  int dinero = 1000;
		  while(true){
		    Exchanger<Integer> canal = esperaTenderoLibre();
		    int tendero = canal.exchange(pid);
		    System.out.println("Cliente "+pid+" Atendido por tendero "+tendero);
		    int precio = pideProductoATendero(canal);
		    System.out.println("Cliente "+pid+" Precio Tendero "+tendero+" -> "+precio+"[Saldo = "+dinero+"€]");
		    if(dinero <= precio){
		       devolverProductoATendero(canal);
		       System.out.println("Cliente "+pid+" producto devuelto a Tendero "+tendero+" -> "+precio+"[Saldo = "+dinero+"€]");
		       break;
		    }
		    dinero -= precio;
		    pagaATendero(precio,canal);
		    System.out.println("Cliente "+pid+" producto pagado a Tendero "+tendero+" -> "+precio+"[Saldo = "+dinero+"€]");
		    Thread.sleep(5000);
		  }
	}
	
	public void tendero(int pid) throws InterruptedException{
		
		int caja = 0;
		
		while(true){
			Exchanger<Integer> canal = new Exchanger<>();
		    int cliente = esperaCliente(pid,canal);
			System.out.println("Tendero "+pid+" atendiendo a cliente "+cliente);
		    int precio = (int) (Math.random() * 1000);
		    System.out.println("Tendero "+pid+" precio producto para cliente "+cliente+" -> "+precio);
		    indicaPrecioProducto(precio,canal);
		    int aux = cobraORecogeProducto(canal);
		    if(aux != 0){
		    	caja += aux;
		    	System.out.println("Tendero "+pid+" cobrado producto a cliene "+cliente+"[Caja "+caja+"€]");
		    }else{
		    	System.out.println("Tendero "+pid+" producto devuelto de cliene "+cliente+"[Caja "+caja+"€]");
		    }
		}
	}
	
	public void exec(){
		
		for(int i = 0; i < TENDEROS; i++){
			int pid = i;
			Thread tendero = new Thread(()->{
				try {
					tendero(pid);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			tendero.start();
		}
		
		for(int i = 0; i < CLIENTES;i++){
			int pid = i;
			Thread cliente = new Thread(()->{
				try {
					cliente(pid);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			cliente.start();
		}
		
	}
	
	public static void main(String[]args){
		new Tenderos().exec();
	}
	
	
}
