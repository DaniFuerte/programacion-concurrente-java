package simulacro2;

import java.util.Map.Entry;
import java.util.concurrent.*;

public class Turnov2 {
		
	private int turno = 0;
	private int jugadores = 0;
	private ConcurrentHashMap<Integer,Exchanger<Boolean>> turnos_jugadores = new ConcurrentHashMap<>();
	
	public Turnov2(int jugadores){
		this.jugadores = jugadores;
		for (int i = 0; i < jugadores; i++){
			this.turnos_jugadores.put(i, new Exchanger<>());
		}
	}
	
	// El metodo se bloquea hasta que sea el turno del jugador o se acabe la partida
	public boolean esperaTurnoOFin(int jugador){
		boolean fin_partida = false;
		Exchanger<Boolean> turno_jugador = this.turnos_jugadores.get(jugador);
		if(jugador != turno){
			try {
				turno_jugador.exchange(null);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fin_partida;
	}
	
	// este metodo será invocado por un jugador para indicar que se acaba la partida
	public void finPartida(int jugador){
		
		int ganador = (int)(Math.random()*this.jugadores);
		boolean gana_partida = ganador == jugador;
		if(gana_partida){
		
			for(Entry<Integer, Exchanger<Boolean>> ex: this.turnos_jugadores.entrySet()){
				try {
					ex.getValue().exchange(true);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
		
	}
	
	// este método será invocado por un jugador para pasar el turno al siguiente jugador
	
	public void siguienteTurno(){
		turno = (turno+1)%jugadores;
		Exchanger<Boolean> siguiente = this.turnos_jugadores.get(turno);
		try {
			siguiente.exchange(false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
