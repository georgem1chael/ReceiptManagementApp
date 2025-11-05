package project;

public class PermissionHandler {
	
	//public boolean canSubmit(User user) {				//if everyone can submit a receipt so not needed??
		
		//if( user.getRole() == Role.SALESPERSON)
			//return true;
		
		//return false;
	//}
	
	public boolean canApprove(User user, Receipt rec) {
		
		if( user.getRole() == Role.SALESPERSON || user.getRole() == Role.ACCOUNTANT)
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
	
	//public boolean canManageUsers(User user) {				// is this even useful atp
		
		//if ( user.getRole() == Role.MANAGER || user.getRole() == Role.ADMIN)
			//return true;
		
		//return false;
	//}

}
