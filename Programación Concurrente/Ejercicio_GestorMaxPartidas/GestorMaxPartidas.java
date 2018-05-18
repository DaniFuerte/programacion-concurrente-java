package simulacro2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GestorMaxPartidas {

	public static final int MAX_PARTIDAS = 10;
	
	//atributos necesarios
	private BlockingQueue<Integer> partidas;
	
	public GestorMaxPartidas(){
		this.partidas = new ArrayBlockingQueue<>(MAX_PARTIDAS);
	}
	
	public boolean iniciarNuevaPartida(long waitTimeMillis){
		try {
			if(!partidas.offer(1, waitTimeMillis, TimeUnit.SECONDS))
				return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void finalizarPartida(){
		try {
			partidas.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
