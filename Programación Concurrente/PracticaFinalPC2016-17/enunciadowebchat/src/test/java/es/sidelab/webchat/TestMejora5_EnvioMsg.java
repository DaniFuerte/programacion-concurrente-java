package es.sidelab.webchat;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestMejora5_EnvioMsg {
	
	static final int THREADS = 2;
	static final String SALUDO = "Hola que tal?";
	
	Semaphore flag_chat = new Semaphore(0);
	Semaphore flag_user = new Semaphore(0);
	
	ChatManager manager = new ChatManager(1);
	Chat chat;
	
    BlockingQueue<String> buzon = new ArrayBlockingQueue<String>(1);
    Exchanger<Boolean> ex = new Exchanger<>();
	
    CompletionService<String> threads = new ExecutorCompletionService<String>(Executors.newFixedThreadPool(THREADS));
	
	public void crear_chat(String userName) throws InterruptedException, TimeoutException{

		
		TestUser user = new TestUser(userName);
		manager.newUser(user);
		chat = manager.newChat("Chat", 5, TimeUnit.SECONDS);
		chat.addUser(user);
		
		flag_chat.release();
		flag_user.acquire();
		
		chat.sendMessage(user, SALUDO);
		
		boolean envio = ex.exchange(null);
		
		assertTrue("Resultado "+user.getName()+" -> ",envio);
		
	}
	
	public void registrar_usuario(String userName) throws InterruptedException, BrokenBarrierException{
		
		
		flag_chat.acquire();
		TestUser user = new TestUser(userName){
			
			@Override
			public void newMessage(Chat chat, User user, String message){
				//buzon_mensajes.add(message);
				try {
					buzon.put(message);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		manager.newUser(user);
		chat.addUser(user);
		flag_user.release();
		
		boolean recepcion_msg = buzon.take().equals(SALUDO);
		ex.exchange(recepcion_msg);
		
	}
	
	@Test
	public void mejora_test_5_envio_msg()throws Throwable{
		
		Callable<String> envia_saludo = ()->{
			crear_chat("Marta");
			return "Ejecucion Test Envia Mensaje";
		};
		
		threads.submit(envia_saludo);
		
		Callable<String> recibe_saludo = ()->{
			registrar_usuario("Dani");
			return "Ejecucion Test Recibe Mensaje";
		};
		
		threads.submit(recibe_saludo);
		
		
		try{
			Future<String> fut_envia_saludo = threads.take();
			System.out.println("Terminado "+fut_envia_saludo.get());
			Future<String> fut_responde_saludo = threads.take();
			System.out.println("Terminado "+fut_responde_saludo.get());
		}catch(ExecutionException e){
			throw e.getCause();
		}
		
		
	}
	
}
