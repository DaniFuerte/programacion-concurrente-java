package turnos;

import java.util.concurrent.*;

public class Turno {

	private int jugadores;
	private int turno;
	private boolean fin_partida = false;
	private Semaphore mutex;
	private CopyOnWriteArrayList<Semaphore> turnos;
	
	
	    public Turno(int numJugadores){
	    	this.jugadores = numJugadores;
	    	this.turno = 0;
	    	this.mutex = new Semaphore(1);
	    	this.turnos = new CopyOnWriteArrayList<>();
	    	for(int i = 0; i < jugadores;i++){
	    		if(i > 0){
	    			turnos.add(new Semaphore(0));
	    		}else{
	    			turnos.add(new Semaphore(1));
	    		}
	    	}
	    }
	    //El método se bloquea hasta que sea el turno del jugador o se acabe la partida
	    public boolean esperaTurnoOFin(int numJugador) throws InterruptedException{
	    	
	    	boolean salida = false;
	    	
	    	turnos.get(numJugador).acquire();
	    	this.mutex.acquire();
	    	if(!fin_partida){
	    		int rand = (int)(Math.random()*jugadores+1);
	    		fin_partida = rand == numJugador;
	    		salida = fin_partida;
	    	}
	    	this.mutex.release();
	    	return salida;
	    }
	    //Este método será invocado por un jugador para indicar que se acaba la partida
	    public void finPartida(){
	    	
	    	for(Semaphore barrier: turnos){
	    		if(barrier.hasQueuedThreads())
	    			barrier.release();
	    	}
	    	
	    }
	    //Este método será invocado por un jugador para pasar el turno al siguiente jugador
	    public void pasaTurnoAlSiguiente() throws InterruptedException{
	    	
	    	int turno_aux = 0;
	    	this.mutex.acquire();
	    	turno = (turno+1)%jugadores;
	    	turno_aux = turno;
	    	this.mutex.release();
	    	turnos.get(turno_aux).release();
	    	
	    }
	
	    public void hilo(int pid,Turno turno) throws InterruptedException{
					while(true){
						System.out.println("Jugador "+pid+" esperando turno");
					    boolean fin = turno.esperaTurnoOFin(pid);
					    if(fin){
					    	turno.finPartida();
					    	System.out.println("Jugador "+pid+" Partida finalizada");
					        break;
					    } else {
					        if(fin){
					           turno.finPartida();
					           System.out.println("Jugador "+pid+" Partida finalizada");
					           break;
					        } else {
					           turno.pasaTurnoAlSiguiente();
					           System.out.println("Jugador "+pid+" Siguiente turno ");
					        }
					    }
					    Thread.sleep(3000);
					}
		}
		
		public void exec() {
			Turno turno = new Turno(5);
			for(int i = 0; i < 5;i++){
				int pid = i;
				new Thread(()->{
					try {
						hilo(pid, turno);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}).start();
			}
		}
		
		public static void main (String[]args){
			new Turno(5).exec();
		}	
}
