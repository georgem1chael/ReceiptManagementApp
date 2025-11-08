package src.project;

import java.util.List;

public class UserHandler {
	
	private List<User> users;
	
	public void createUser(String username, Role role) {
		
		User newUser = new User(username, role);
		
		users.add(newUser);		
	}
	
	public List<User> listUsers() {
		
		return users;
	}
	
	

}
