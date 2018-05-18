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

public class TestMejora5_AltaBajaUsuario {

	static final int THREADS = 2;
	
	ChatManager manager = new ChatManager(5);
	Chat chat;
	
	CompletionService<String> s_elimina = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	CompletionService<String> s_user  = new ExecutorCompletionService<String>(Executors.newSingleThreadExecutor());
	
	public void nuevo_usuario(String userName) throws InterruptedException, TimeoutException{
		chat = manager.newChat("Chat", 5, TimeUnit.SECONDS);
		TestUser user = new TestUser(userName);
		manager.newUser(user);
		chat.addUser(user);
		TestUser aux = (TestUser) chat.getUser(userName);
		boolean existe = Objects.equals(user, aux);
		assertTrue(existe);
		
	}
	
	public void baja_usuario(String userName){
		TestUser user = new TestUser(userName);
		chat.removeUser(user);
		boolean eliminado = chat.getUser(userName) == null;
		assertTrue(eliminado);
	}
	
	@Test
	public void test_mejora_5_nuevo_usuario()throws Throwable{
		/**
		Callable<String> crear_chat = ()->{
			manager.newUser(new TestUser("admin"));
			chat = manager.newChat("Chat", 1, TimeUnit.SECONDS);
			return "Thread #admin";
		};
		s_admin.submit(crear_chat);**/
		
		Callable<String> tarea = ()->{
			//Thread.sleep(3000);
			nuevo_usuario("Usuario 1");
			return "Ejecución Test Nuevo Usuario";
		};
		
		s_user.submit(tarea);
		
		try{
			//Future<String> f_admin = s_admin.take();
			//System.out.println("Terminado "+f_admin.get());
			Future<String> f_user  = s_user.take();
			System.out.println("Terminado "+f_user.get());
		}catch(ExecutionException e){
			throw e.getCause();
		}
		
		
	}
	
	@Test
	public void test_mejora_5_baja_usuario()throws Throwable{
		
		Callable<String> alta_usuario = ()->{
			nuevo_usuario("Usuario 1");
			return "";
		};
		
		s_user.submit(alta_usuario);
		
		Callable<String> tarea = ()->{
			//nuevo_usuario("Usuario1");
			//Thread.sleep(3000);
			baja_usuario("Usuario 1");
			return "Ejecucion Test Baja Usuario";
		};
		
		s_elimina.submit(tarea);
		
		try{
			Future<String> f_elimina  = s_elimina.take();
			System.out.println("Terminado "+f_elimina.get());
		}catch(ExecutionException e){
			throw e.getCause();
		}
		
	}
	
}
