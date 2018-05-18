package es.sidelab.webchat;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class TestFuncionalidad1 {

	static final int TIMEOUT = 5;
	static final int THREADS = 3;
	static final int CHATS = 2;
	
	ChatManager manager = new ChatManager(CHATS);
	CountDownLatch flag_timeout = new CountDownLatch(1);
	
	CompletionService<String> threads = new ExecutorCompletionService<String>(Executors.newFixedThreadPool(THREADS));
	
	@Test
	public void test_funcionalidad1()throws Throwable{
		
		Callable<String> tarea1 = ()->{
			Chat chat = manager.newChat("Chat1", TIMEOUT, TimeUnit.SECONDS);
			if(flag_timeout.getCount() > 0)
				flag_timeout.countDown();
			Thread.sleep(7000);
			manager.closeChat(chat);
			return "Thread #1";
		};
		
		threads.submit(tarea1);
		
		
		Callable<String> tarea2 = ()->{
			Chat chat = manager.newChat("Chat2", TIMEOUT, TimeUnit.SECONDS);
			if(flag_timeout.getCount() > 0)
				flag_timeout.countDown();
			Thread.sleep(5000);
			manager.closeChat(chat);
			return "Thread #2";
		};
		
		threads.submit(tarea2);
		
		Callable<String> tarea3 = ()->{
			flag_timeout.await();
			Chat chat = manager.newChat("Chat3", TIMEOUT, TimeUnit.SECONDS);
			boolean resultado = manager.getChat("Chat3").getName().equals(chat.getName());
			manager.closeChat(chat);
			assertTrue(resultado);
			return "Thread #3";
		};
		
		threads.submit(tarea3);
		
		try{
			Future<String> f1 = threads.take();
			System.out.println("Terminado "+f1.get());
			Future<String> f2 = threads.take();
			System.out.println("Terminado "+f2.get());
			Future<String> f3 = threads.take();
			System.out.println("Terminado "+f3.get());
		}catch(InterruptedException | ExecutionException e){
			throw e.getCause();
		}
		
		
	}
	
	
	
}
