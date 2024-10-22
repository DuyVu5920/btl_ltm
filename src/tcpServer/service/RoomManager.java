/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpServer.service;

import tcpServer.helper.RandomString;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class RoomManager {
    ArrayList<Room> rooms;
    RandomString idGenerator;

    public RoomManager() {
        rooms = new ArrayList<>();
        idGenerator = new RandomString(5);
    }

    public Room createRoom() {
        Room room = new Room(idGenerator.nextString());
        rooms.add(room);

        return room;
    }

    public boolean add(Room r) {
        if (!rooms.contains(r)) {
            rooms.add(r);
            return true;
        }
        return true;
    }

    public boolean remove(Room r) {
        if (rooms.contains(r)) {
            rooms.remove(r);
            return true;
        }
        return false;
    }

    public Room find(String id) {
        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public int getSize() {
        return rooms.size();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
