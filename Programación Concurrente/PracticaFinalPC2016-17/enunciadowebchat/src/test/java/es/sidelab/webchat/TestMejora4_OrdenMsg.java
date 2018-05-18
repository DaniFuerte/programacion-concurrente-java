package es.sidelab.webchat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
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

import static org.junit.Assert.assertTrue;

public class TestMejora4_OrdenMsg {

	Semaphore flag_chat = new Semaphore(0);
	CountDownLatch flag_user = new CountDownLatch(1);
	
	ChatManager manager = new ChatManager(1);
	Chat chat;
	
    BlockingQueue<String> buzon = new ArrayBlockingQueue<String>(10);
    Exchanger<Boolean> ex = new Exchanger<>();
	
	CompletionService<String> serv1 = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	
	CompletionService<String> serv2 = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	
	public void crear_chat(String userName) throws InterruptedException, TimeoutException{

		TestUser user = new TestUser(userName);
		manager.newUser(user);
		chat = manager.newChat("Chat", 5, TimeUnit.SECONDS);
		chat.addUser(user);
		flag_chat.release();
		flag_user.await();
		
		for(int i = 0; i < 10; i++){
			chat.sendMessage(user, ""+i);
		}
		
		Thread.sleep(500);
		
		assertTrue("Resultado ",ex.exchange(null));
		
	}
	
	public void registrar_usuario(String userName) throws InterruptedException, BrokenBarrierException{
		
		boolean sorted = false;
		
		flag_chat.acquire();
		TestUser user = new TestUser(userName){
			
			@Override
			public void newMessage(Chat chat, User user, String message){
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
		flag_user.countDown();
		
		Thread.sleep(500);
		
		for(int i = 0; i < 10; i++){
			int n = Integer.parseInt(buzon.take());
			sorted = n == i;
			if(!sorted)
				break;
		}
		
		ex.exchange(sorted);
		
	}
	
	
	@Test
	public void test_mejora4_orden_msg() throws Throwable{
		
		
		Callable<String> task1 = () -> {
			crear_chat("Usuario 1");
			return "Thread #1";
		};
		serv1.submit(task1);
		
		Callable<String> task2 = () -> {
			registrar_usuario("Usuario 2");
			return "Thread #2";
		};
		serv2.submit(task2);
		
		try{
			Future<String> f1 = serv1.take();
			System.out.println("Terminado "+f1.get());
			Future<String> f2 = serv2.take();
			System.out.println("Terminado "+f2.get());
		}catch(ExecutionException e){
			e.getCause();
		}
	
	}
	
}