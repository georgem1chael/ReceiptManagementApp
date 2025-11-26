package project;
public class User {
	
	private String username;
	private Role role;
	private String password;
	private String email;
	
	public User(String username, Role role, String password, String email) {
		
		this.username = username;
		this.role = role;
		this.password = password;
		this.email = email;
	}
	
	public String getUsername() {
		
		return username;
	}
	
	public Role getRole() {
		
		return role;
	}

	public String getPassword(){

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public String getEmail(){

		return this.email;
	}

}
