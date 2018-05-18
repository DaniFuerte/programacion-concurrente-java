package es.sidelab.webchat;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class TestMejora5_CerrarChat {

	ChatManager manager = new ChatManager(5);
	Chat chat;
	CompletionService<String> cerrar_chat = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	
	public void cierra_chat(String chatName) throws InterruptedException, TimeoutException{
		chat = manager.newChat(chatName, 5, TimeUnit.SECONDS);
		Thread.sleep(5000);
		manager.closeChat(chat);
		boolean cerrado = manager.getChat(chatName) == null;
		assertTrue("Chat cerrado ",cerrado);
	}
	
	@Test
	public void test_mejora_5_cerrar_chat()throws Throwable{
		Callable<String> tarea = ()->{
			cierra_chat("Chat");
			return "Ejecucion Test Cerrar Chat";
		};
		cerrar_chat.submit(tarea);
		try{
			Future<String> f_cerrar_chat = cerrar_chat.take();
			System.out.println("Terminado "+f_cerrar_chat.get());
		}catch(ExecutionException e){
			throw e.getCause();
		}
	}
	
}
