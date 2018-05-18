package es.sidelab.webchat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestMejora1 {

	static final int NCHATS = 50;
	static final int ITER	= 5;
	static final int THREADS = 4;
	/** Paso 1 -> Crear un ChatManager con un tope de 50 chats.*/
	ChatManager manager = new ChatManager(NCHATS);
	
	/** Paso 2 -> Ejecutar 4 hilos en paralelo simulando 4 usuarios concurrentes.*/
	/**ExecutorService hilo1 = Executors.newSingleThreadExecutor();
	ExecutorService hilo2 = Executors.newSingleThreadExecutor();
	ExecutorService hilo3 = Executors.newSingleThreadExecutor();
	ExecutorService hilo4 = Executors.newSingleThreadExecutor();**/
	
	ExecutorService threads = Executors.newScheduledThreadPool(THREADS);
	
	
	/** Paso 3 -> Cada hilo creará un objeto "TestUser", y lo registrará en el "ChatManager". 
	 	Para ello me creo un método llamado "procesar_tarea", que se encargará de realizar 
	 	esta función para cada hilo.
	 * @throws TimeoutException 
	 * @throws InterruptedException */
	public void procesar_tarea(String nombreUsuario) throws InterruptedException, TimeoutException {
		TestUser user = new TestUser(nombreUsuario);
		manager.newUser(user);
		/** Paso 4 */
		for(int veces = 0; veces < ITER; veces++){
			int pid = veces;
			/** 4.1 -> Creará un chat llamado "chat"+número de iteración...*/
			Chat chat = manager.newChat("Chat "+pid, ITER, TimeUnit.SECONDS);
			/** 4.2 **/
			chat.addUser(user);
			/** 4.3 **/
			for(User u: chat.getUsers()){
				System.out.println(chat.getName()+" iteracion "+pid+" -> "+u);
			}
					
		}
	}
	
	/**
	public Runnable tarea(String nombreUsuario){
		Runnable tarea = () -> {
			try {
				procesar_tarea(nombreUsuario);
			} catch (InterruptedException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		return tarea;
	}
	
	
	/** Paso 2 -> Ejercutar 4 hilos en paralelo simulando 4 usuarios concurrentes **/
	/**
	@Test
	public void newUserInChat() throws Throwable{
		Runnable tarea1 = tarea("usuario 1");
		hilo1.execute(tarea1);
		Runnable tarea2 = tarea("usuario 2");
		hilo2.execute(tarea2);
		Runnable tarea3 = tarea("usuario 3");
		hilo3.execute(tarea3);
		Runnable tarea4 = tarea("usuario 4");
		hilo4.execute(tarea4);
	}*/
	
	@Test
	public void newUserInChat() throws Throwable{
		
		Runnable tarea1 = () -> {
			try {
				procesar_tarea("usuario 1");
			} catch (InterruptedException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		threads.execute(tarea1);
		Runnable tarea2 = () -> {
			try {
				procesar_tarea("usuario 2");
			} catch (InterruptedException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		threads.execute(tarea2);
		Runnable tarea3 = () -> {
			try {
				procesar_tarea("usuario 3");
			} catch (InterruptedException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		threads.execute(tarea3);
		Runnable tarea4 = () -> {
			try {
				procesar_tarea("usuario 4");
			} catch (InterruptedException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		threads.execute(tarea4);
		
	}
	
	
}
