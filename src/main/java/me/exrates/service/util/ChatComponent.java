package me.exrates.service.util;

import java.util.NavigableSet;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ChatComponent {

    private final ReadWriteLock lock;
    private NavigableSet<ChatMessage> cache;
    private ChatMessage tail;

}

