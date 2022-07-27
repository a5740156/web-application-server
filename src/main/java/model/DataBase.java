package model;

import java.util.HashMap;
import java.util.Map;

public class DataBase {
	private static Map<String, User> users = new HashMap<String, User>();
	
	public static void addUser(User user) {
		users.put(user.getUserId() , user);
	}
	
	public static User getUser(String userId) {
		return users.get(userId);
	}
	
	public static User findUserById(String userId) {
        return users.get(userId);
    }
	
}
