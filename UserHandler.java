import java.util.*;

public class UserHandler {
	
	private List<User> users;
	
	public void createUser(String username, Role role, String password) {
		
		User newUser = new User(username, role, password);
		
		users.add(newUser);		
	}
	
	public List<User> listUsers(User user) {
		
		if(user.getRole() == Role.ADMIN)
		return users;

		else
			throw new SecurityException("Only an administrator can see a comprehensive User list!");

	}	

}
