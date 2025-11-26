package project;
import java.util.*;

public class UserHandler {
	
	private List<User> users;
	
	public UserHandler() {
		this.users = new ArrayList<>();
	}
	
	public void createUser(String username, Role role, String password, String email) {
		User newUser = new User(username, role, password, email);
		users.add(newUser);		
	}
	
	public List<User> listUsers(User user) {
		if(user.getRole() == Role.ADMIN)
			return users;
		else
			throw new SecurityException("Only an administrator can see a comprehensive User list!");
	}
	
	public User findUser(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}
	
	public boolean authenticateUser(String username, String password) {
		User user = findUser(username);
		return user != null && user.getPassword().equals(password);
	}
	
	public boolean changePassword(User adminUser, String targetUsername, String newPassword) {
		if (adminUser.getRole() != Role.ADMIN) {
			throw new SecurityException("Only administrators can change passwords!");
		}
		
		User targetUser = findUser(targetUsername);
		if (targetUser == null) {
			return false;
		}
		
		targetUser.setPassword(newPassword);
		return true;
	}
	
	public List<String> getAllUsernames() {
		List<String> usernames = new ArrayList<>();
		for (User user : users) {
			usernames.add(user.getUsername());
		}
		return usernames;
	}
}
