public class User {
	
	private String username;
	private Role role;
	private String password;
	
	public User(String username, Role role, String password) {
		
		this.username = username;
		this.role = role;
		this.password = password;
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

}
