package es.sidelab.webchat;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class TestMejora4_Notificaciones {
	
	static final int THREADS = 4;

    Semaphore flag_chat = new Semaphore(0);
	CountDownLatch flag_user = new CountDownLatch(THREADS-1);
	
	static ChatManager manager = new ChatManager(5);
	static Chat chat;
	
	ExecutorService threads = Executors.newScheduledThreadPool(THREADS);
	
	CompletionService<String> serv1 = new ExecutorCompletionService<String>(threads);
	CompletionService<String> serv2 = new ExecutorCompletionService<String>(threads);
	CompletionService<String> serv3 = new ExecutorCompletionService<String>(threads);
	CompletionService<String> serv4 = new ExecutorCompletionService<String>(threads);
	
	public void crear_chat(String name_thread, int tickets) throws InterruptedException, TimeoutException{
		TestUser user = new TestUser(name_thread);
		manager.newUser(user);
		chat = manager.newChat("Chat", 5, TimeUnit.SECONDS);
		chat.addUser(user);
		flag_chat.release(tickets);
		flag_user.await();
		chat.sendMessage(user, "Hola!!!");
	}
	
	public void registrar_usuario(String name_thread) throws InterruptedException{
		flag_chat.acquire();
		TestUser user = new TestUser(name_thread);
		manager.newUser(user);
		chat.addUser(user);
		flag_user.countDown();
	}	
	
	@Test
	public void testMejora4Notificaciones()throws Throwable{
		Callable<String> usuario1 = ()->{
			crear_chat("usuario1",(THREADS-1));
			Thread.sleep(1000);
			return "Thread #1";
		};
		serv1.submit(usuario1);
		
		Callable<String> usuario2 = ()->{
			registrar_usuario("usuario2");
			Thread.sleep(1000);
			return "Thread #2";
		};
		serv2.submit(usuario2);
		
		Callable<String> usuario3 = ()->{
			registrar_usuario("usuario3");
			Thread.sleep(1000);
			return "Thread #3";
		};
		serv3.submit(usuario3);
		
		Callable<String> usuario4 = ()->{
			registrar_usuario("usuario4");
			Thread.sleep(1000);
			return "Thread #4";
		};
		serv4.submit(usuario4);
		
		
		try{
			Future<String> f1 = serv1.take();
			System.out.println("Terminado "+f1.get());
			Future<String> f2 = serv2.take();
			System.out.println("Terminado "+f2.get());
			Future<String> f3 = serv3.take();
			System.out.println("Terminado "+f3.get());
			Future<String> f4 = serv4.take();
			System.out.println("Terminado "+f4.get());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
}
