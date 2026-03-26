package project;
import java.util.*;

/**
 * Manages user accounts and authentication.
 * Provides operations for creating, finding, and managing users.
 */
public class UserHandler {
	
	// List of all users in the system
	private List<User> users;
	
	/**
	 * Creates a new UserHandler with an empty user list.
	 */
	public UserHandler() {
		this.users = new ArrayList<>();
	}
	
	/**
	 * Creates a new user and adds them to the system.
	 * 
	 * @param username The unique username
	 * @param role The user's role
	 * @param password The user's password
	 * @param email The user's email address
	 */
	public void createUser(String username, Role role, String password, String email) {
		User newUser = new User(username, role, password, email);
		users.add(newUser);		
	}
	
	/**
	 * Returns the list of all users. Only administrators can access this.
	 * 
	 * @param user The user requesting the list (must be ADMIN)
	 * @return List of all users
	 * @throws SecurityException if user is not an administrator
	 */
	public List<User> listUsers(User user) {
		if(user.getRole() == Role.ADMIN)
			return users;
		else
			throw new SecurityException("Only an administrator can see a comprehensive User list!");
	}
	
	/**
	 * Finds a user by their username.
	 * 
	 * @param username The username to search for
	 * @return The User object if found, null otherwise
	 */
	public User findUser(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}
	
	/**
	 * Authenticates a user with their username and password.
	 * 
	 * @param username The username to authenticate
	 * @param password The password to verify
	 * @return true if credentials are valid, false otherwise
	 */
	public boolean authenticateUser(String username, String password) {
		User user = findUser(username);
		return user != null && user.getPassword().equals(password);
	}
	
	/**
	 * Changes a user's password. Only administrators can perform this action.
	 * 
	 * @param adminUser The administrator requesting the change
	 * @param targetUsername The username whose password should be changed
	 * @param newPassword The new password
	 * @return true if password was changed successfully, false if user not found
	 * @throws SecurityException if adminUser is not an administrator
	 */
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
	
	/**
	 * Returns a list of all usernames in the system.
	 * Used for populating user selection dropdowns.
	 * 
	 * @return List of all usernames
	 */
	public List<String> getAllUsernames() {
		List<String> usernames = new ArrayList<>();
		for (User user : users) {
			usernames.add(user.getUsername());
		}
		return usernames;
	}
}
