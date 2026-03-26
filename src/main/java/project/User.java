package project;

/**
 * Represents a user in the receipt management system.
 * Each user has a role that determines their permissions and capabilities.
 */
public class User {
	
	// User credentials and profile information
	private String username;
	private Role role;
	private String password;
	private String email;
	
	/**
	 * Creates a new user with the specified credentials and role.
	 * 
	 * @param username The unique username for this user
	 * @param role The role determining user permissions
	 * @param password The user's password for authentication
	 * @param email The user's email address
	 */
	public User(String username, Role role, String password, String email) {
		
		this.username = username;
		this.role = role;
		this.password = password;
		this.email = email;
	}
	
	/**
	 * Returns the username of this user.
	 * 
	 * @return The username
	 */
	public String getUsername() {
		
		return username;
	}
	
	/**
	 * Returns the role of this user.
	 * 
	 * @return The user's role (SALESPERSON, ACCOUNTANT, MANAGER, or ADMIN)
	 */
	public Role getRole() {
		
		return role;
	}

	/**
	 * Returns the password of this user.
	 * 
	 * @return The user's password
	 */
	public String getPassword(){

		return password;
	}

	/**
	 * Updates the password for this user.
	 * 
	 * @param password The new password
	 */
	public void setPassword(String password) {

		this.password = password;
	}

	/**
	 * Returns the email address of this user.
	 * 
	 * @return The user's email address
	 */
	public String getEmail(){

		return this.email;
	}

}
