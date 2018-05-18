package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
//import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.Map;

public class Chat {

	private String name;
	private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	//private AtomicInteger cont_users;
	private ChatManager chatManager;

	public Chat(ChatManager chatManager, String name) {
		this.chatManager = chatManager;
		this.name = name;
		//this.cont_users = new AtomicInteger(0);
	}

	public String getName() {
		return name;
	}

	public void addUser(User user) {
		/**users.put(user.getName(), user);
		for(User u : users.values()){
			if (u != user) {
				u.newUserInChat(this, user);
			}
		}*/
		/** Mejora 2 **/
		if(users.putIfAbsent(user.getName(), user) == null){
			for (User u: users.values()){
				if(u != user){
					//u.newUserInChat(this, nuevo);
					/** Mejora 3 */
					chatManager.getExecutor(u).execute(()->u.newUserInChat(this, user));
				}
			}
			//cont_users.getAndIncrement();
		}
	}

	public void removeUser(User user) {
		if(users.remove(user.getName()) != null){
			for(User u : users.values()){
				//u.userExitedFromChat(this, user);
				/** Mejora 3 */
				chatManager.getExecutor(u).execute(()->u.userExitedFromChat(this, user));
			}
			//cont_users.getAndDecrement();
		}
	}

	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String name) {
		return users.get(name);
	}

	public void sendMessage(User user, String message) {
		for(User u : users.values()){
			//u.newMessage(this, user, message);
			// Mejora 3
			chatManager.getExecutor(u).execute(()->u.newMessage(this, user, message));
		}
	}

	/**
	public int getNumberUsersInChat(){
		return this.cont_users.get();
	}**/
	
	public void close() {
		this.chatManager.closeChat(this);
	}
}
