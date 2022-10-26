package com.capstone.qwirkle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubBroker {
    // Collection of subscribers. Maps topic onto set of subscribers.
    private final Map<String, Set<Subscriber>> subscribers;

    // Singleton instance of the broker.
    static private PubSubBroker instance = null;

    // Made private so only can be called by self.
    private PubSubBroker() {
        subscribers = new ConcurrentHashMap<>(); //Use thread-safe Map
    }

    // Provides access to a single instance of a broker.
    static public PubSubBroker getInstance() {
        if(instance == null)
            instance = new PubSubBroker();

        return instance;
    }

    public void subscribe(String topic, Subscriber subscriber) {
        // Get set of subscribers listening to topic. If none, create a new set.
        Set<Subscriber> subscriberSet;
        subscriberSet = subscribers.computeIfAbsent(topic, key -> new HashSet<>());

        // Add subscriber to the set.
        subscriberSet.add(subscriber);
    }

    public void unsubscribe(String topic, Subscriber subscriber) {
        // Get set of subscribers listening to the topic.
        Set<Subscriber> subscriberSet;
        subscriberSet = subscribers.get(topic);

        // If no-one listening, stop.
        if(subscriberSet == null)
            return;

        // Remove from set.
        subscriberSet.remove(subscriber);

        // Empty set? If so, remove the set.
        if(subscriberSet.size() == 0)
            subscribers.remove(topic);
    }

    public void unsubscribe(Subscriber subscriber) {
        // Getting topics, but copying to another structure since the
        // process of unsubscribing could remove a subscriber set, hence
        // modify the keySet while iterating through it - i.e. a problem.
        List<String> topics = new ArrayList<>();
        topics.addAll(subscribers.keySet());

        for (String topic : topics) {
            unsubscribe(topic, subscriber);
        }
    }

    public void publish(Object publisher, String topic, Map<String, Object> params) {
        Set<Subscriber> subscriberSet;
        subscriberSet = subscribers.get(topic);

        // If no subscribers for the topic, done!
        if(subscriberSet == null)
            return;

        // Notify all subscribers of the publishing of the message.
        subscriberSet.forEach(
                subscriber -> subscriber.onPublished(publisher, topic, params));
    }
}
