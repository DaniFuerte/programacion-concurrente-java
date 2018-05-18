package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class ChatManager {

	private ConcurrentHashMap<String, Chat> chats = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	/** Funcionalidad 1 **/
	private BlockingQueue<Integer> tickets_chat;
	/** Mejora 3 */
	private ConcurrentHashMap<User,ExecutorService> executors = new ConcurrentHashMap<>();
	private int maxChats;
	/** Funcionalidad 2 **/
	private ExecutorService threads = Executors.newFixedThreadPool(10);
	private CompletionService<Chat> services = new ExecutorCompletionService<>(threads);

	/** Funcionalidad 2 -> ParallelForSum **/
	/** promedio **/
	private int get_promedio(Chat [] a, int size){
		int promedio = (parallelForSum(size, i -> a[i]))/size;
		return promedio;
	}
	/** parallelforsum que calcula la suma de usuarios registrados **/ 
	private int parallelForSum(int size, Function<Integer,Chat> iteration){
		for(int i = 0; i < size; i++){
			int iter = i;
			services.submit(()->iteration.apply(iter));
		}
		int suma = 0;
		for(int i = 0; i < size ; i++){
			try {
				Chat chat = services.take().get();
				suma += chat.getUsers().size();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return suma;
	}

	/** minimo **/
	private Chat get_min(Chat [] a, int size){
		Chat min = this.parallelForSumMin(size, Integer.MAX_VALUE, i -> a[i]);
		return min;		
	}
	
	/** parallelforsum que devuelve el chat con el menor número de usuarios **/
	private Chat parallelForSumMin(int size, int minimo, Function<Integer,Chat> iteration){
		for(int i = 0; i < size ; i++){
			int iter = i;
			services.submit(() -> iteration.apply(iter));
		}
		Chat resultado = null;
		int min = minimo;
		for(int i = 0; i < size ; i++){
			try {
				Chat aux = services.take().get();
				if(aux.getUsers().size() <= min) {
					min = aux.getUsers().size();
					resultado = aux;
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultado;
	}
	
	/** maximo **/
	private Chat get_max(Chat [] a, int size){
		Chat max = this.parallelForSumMax(size, Integer.MIN_VALUE, i -> a[i]);
		return max;
	}
	
	/** parallelforsum que devuelvel el chat con el mayor número de usuarios registrados**/
	private Chat parallelForSumMax(int size, int maximo, Function<Integer,Chat> iteration){
		for(int i = 0; i < size; i++){
			int iter = i;
			services.submit(()->iteration.apply(iter));
		}
		Chat resultado = null;
		int max = maximo;
		for(int i = 0; i < size; i++){
			try {
				Chat aux = services.take().get();
				if(aux.getUsers().size() > max) {
					max = aux.getUsers().size();
					resultado = aux;
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return resultado;
	}
	
	public ChatManager(int maxChats) {
		this.maxChats = maxChats;
		/** Funcionalidad 1**/
		this.tickets_chat = new ArrayBlockingQueue<>(this.maxChats);
		
		/** Funcionalidad 2 **/
		Runnable tareas = () -> {
			/** tareas del hilo principal
			 *  imprimir por pantalla el numero de chats creados sobre el total permitido 
			 *  imprimir el numero de usuarios registrados 
			 */
			System.out.println("Número total de Chats -> "+chats.size()+" / "+maxChats);
			System.out.println("Numero total de Usuario -> "+users.size());

			/** tareas del hilo principal
			 * volcar los chats creados hasta el momento en un array para que los hilos secundarios se encarguen en 
			 * de obtener los estadisticos 
			 * - promedio de usuarios por chat
			 * - chat con menor número de usuarios
			 * - chat con mayor número de usuarios
			 */
			
			if(users.size() == 0) {
				System.out.println("No hay usuarios registrados para calcular el promedio ");
			} else {
				Chat [] users_chat = new Chat[chats.values().size()];
				int i = 0;
				for(Chat chat: chats.values()){
					users_chat[i] = chat;
					i++;
				}
				int media = this.get_promedio(users_chat, users_chat.length);
				System.out.println("Promedio de usuarios por chat creado -> "+media);
				Chat minimo = this.get_min(users_chat, users_chat.length);
				Chat maximo = this.get_max(users_chat, users_chat.length);
				if(minimo != null)
					System.out.println("El chat con menor numero de usuarios -> "+minimo.getName()+" con "+minimo.getUsers().size()+" usuarios");
				if(maximo != null)
					System.out.println("El chat con mayor numero de usuarios -> "+maximo.getName()+" con "+maximo.getUsers().size()+" usuarios");
			}
			System.out.println();
		};
		
		/** hilo principal usado para el cálculo de estadisticos **/
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(tareas, 3, 3, TimeUnit.SECONDS);
		
	}

	public void newUser(User user) {	
		
		if(users.containsKey(user.getName())){
			throw new IllegalArgumentException("There is already a user with name \'"
					+ user.getName() + "\'");
		} else {
			//users.put(user.getName(), user);
			/** Mejora 2 y 3 **/
			if(users.putIfAbsent(user.getName(), user) == null) // Mejora 2 
				executors.putIfAbsent(user, Executors.newSingleThreadExecutor()); // Mejora 3
		}
	}

	public Chat newChat(String name, long timeout, TimeUnit unit) throws InterruptedException,
			TimeoutException {
		boolean flag_timeout = false;
		if (chats.size() == maxChats) {
			/** Funcionalidad 1**/
			if(!this.tickets_chat.offer(1, timeout, unit))
				throw new TimeoutException("There is no enought capacity to create a new chat");
			else
				flag_timeout = true;
		}

		if(chats.containsKey(name)){
			return chats.get(name);
		} else {
			/** Funcionalidad 1**/
			if(!flag_timeout)
				this.tickets_chat.put(1);
			Chat newChat = new Chat(this, name);
			//chats.put(name, newChat);
			chats.putIfAbsent(newChat.getName(), newChat);
			for(User user : users.values()){
				user.newChat(newChat);
			}

			return newChat;
		}
	}

	public void closeChat(Chat chat) {
		Chat removedChat = chats.remove(chat.getName());
		if (removedChat == null) {
			throw new IllegalArgumentException("Trying to remove an unknown chat with name \'"
					+ chat.getName() + "\'");
		}

		for(User user : users.values()){
			user.chatClosed(removedChat);
		}
		/** Funcionalidad 1 **/
		try {
			this.tickets_chat.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Collection<Chat> getChats() {
		return Collections.unmodifiableCollection(chats.values());
	}

	public Chat getChat(String chatName) {
		return chats.get(chatName);
	}

	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String userName) {
		return users.get(userName);
	}
	
	public ExecutorService getExecutor(User user){
		return executors.get(user);
	}

	public void close() {}
}
