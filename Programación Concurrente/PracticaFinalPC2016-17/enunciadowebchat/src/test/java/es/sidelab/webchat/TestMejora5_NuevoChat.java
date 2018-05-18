package es.sidelab.webchat;

import java.util.Objects;
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

public class TestMejora5_NuevoChat {

	ChatManager manager = new ChatManager(5);
	Chat chat;
	CompletionService<String> crear_chat = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	
	public void nuevo_chat(String chatName) throws InterruptedException, TimeoutException{
		manager.newUser(new TestUser("Admin"));
		chat = manager.newChat(chatName, 5, TimeUnit.SECONDS);
		assertTrue("Creado nuevo Chat ",Objects.equals(chatName, chat.getName()));
	}
	
	@Test
	public void test_mejora_5_nuevo_chat() throws Throwable{
		
		Callable<String> tarea = ()->{
			nuevo_chat("Chat");
			return "Ejecución Test Nuevo Chat";
		};
		
		crear_chat.submit(tarea);
		
		try{
		Future<String> f_chat = crear_chat.take();
		System.out.println("Terminado "+f_chat.get());
		}catch(ExecutionException e){
			throw e.getCause();
		}
	}
	
	
}
