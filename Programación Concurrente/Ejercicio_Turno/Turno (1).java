package examenes;

import java.util.Map.Entry;
import java.util.concurrent.*;

public class Turno {

	private int turno;
	private static int jugadores;
	private boolean fin = false;
	Semaphore mutex;
	private CopyOnWriteArrayList<Semaphore> turnos;

	public Turno(int jugadores) {
		this.jugadores = jugadores;
		this.jugadores = 0;
		this.mutex = new Semaphore(1);
		for (int i = 1; i<=jugadores;i++){
			turnos.add(new Semaphore(0));
		}
	}

	// El método se bloquea hasta que sea el turno del jugador o se acabe la
	// partida
	public boolean esperaTurnoOFin(int jugador) throws InterruptedException {
		Boolean salida = false;
		mutex.acquire();
		turnos.get(jugador).acquire();
		if (!fin){
			int rand = (int)(Math.random()*jugadores+1);
    		fin = rand == jugador;
			salida = fin;
		}
		this.mutex.release();
		return salida;
		
	}
	

	// Este método será invocado por un jugador para indicar que se acaba la
	// partida
	public void finPartida(int jugador) {
		for (Semaphore barrier: turnos){
			if (barrier.hasQueuedThreads())
				barrier.release();
		}

	}

	// Este método será invocado por un jugador para pasar el turno al siguiente
	// jugador
	public void pasaTurnoAlSiguiente() throws InterruptedException {
		int turno_aux = 0;
    	this.mutex.acquire();
    	turno = (turno+1)%jugadores;
    	turno_aux = turno;
    	this.mutex.release();
    	turnos.get(turno_aux).release();
	}
	
	public void hilo() throws InterruptedException{
		Turno turno = new Turno(5);
				while(true){
				    boolean fin = turno.esperaTurnoOFin(0);
				    if(fin){
				        break;
				    } else {
				        if(fin){
				           turno.finPartida(0);
				           break;
				        } else {
				           turno.pasaTurnoAlSiguiente();
				        }
				    }
				}
	}
	
	public void exec() {
			new Thread(() -> {
				try {
					hilo();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
		}
	
	public static void main (String[]args){
		new Turno(5).exec();
	}
}
