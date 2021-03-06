/*
 * Copyright (c) 2006 Rick Mugridge, www.RimuResearch.com
 * Released under the terms of the GNU General Public License version 2 or later.
*/
package fitlibrary.eg.chat;

import java.util.List;

import fitlibrary.traverse.DomainAdapter;

public class ChatSystem implements DomainAdapter {
	private Chat chat = new Chat();
	
	public Object getSystemUnderTest() {
		return chat;
	}
	public int getNumberOfRooms() {
		return chat.getRooms().size();
	}
	public User name(String name) {
		return new User(name);
	}
	public Room nameOwnerUsers(String name, User owner, List users) {
		Room room = new Room(name,owner);
		room.setUsers(users);
		return room;
	}
	public User findUser(String userName) {
		User user = chat.getUser(userName);
		if (user == null)
			throw new RuntimeException("Unknown user: "+userName);
		return user;
	}
	public Room findRoom(String roomName) {
		Room room = chat.getRoom(roomName);
		if (room == null)
			throw new RuntimeException("Unknown room: "+roomName);
		return room;
	}
}
