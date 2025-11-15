public class PermissionHandler {
	
	public boolean canApprove(User user, Receipt rec) {
		
		if( user.getRole() == Role.SALESPERSON || user.getRole() == Role.ACCOUNTANT || user.getRole() == Role.ADMIN)
			return false;
		
		if( rec.getSubmitter() == user)
			return false;
		
		return true;
	}
	
	public boolean canHandle(User user, Receipt rec) {
		
		if( user.getRole() != Role.ACCOUNTANT)
			return false;
		
		if( rec.getSubmitter() == user)
			return false;
		
		return true;
	}
	
}
