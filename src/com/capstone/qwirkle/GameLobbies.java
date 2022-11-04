package com.capstone.qwirkle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class GameLobbies {
    private static final ReentrantLock lock = new ReentrantLock();
    public static final Map<Integer, Set<PlayerClient>> lobbies = new HashMap<>();
    public static void addLobby(int i) {
        lock.lock();
        lobbies.put(i, new HashSet<>());
        lock.unlock();
    }

}
