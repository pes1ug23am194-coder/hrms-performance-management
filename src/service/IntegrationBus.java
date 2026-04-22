package service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class IntegrationBus {
    private static IntegrationBus instance;
    private Map<String, Map<String, Function<Object[], Object>>> services = new HashMap<>();
    private Map<String, List<Consumer<Object>>> subscribers = new HashMap<>();
    private List<Map<String, Object>> log = new ArrayList<>();

    private IntegrationBus() {}

    public static synchronized IntegrationBus getInstance() {
        if (instance == null) {
            instance = new IntegrationBus();
        }
        return instance;
    }

    /**
     * Register a subsystem with its exposed methods.
     * This is where a real integration happens: other subsystems call this to share their data.
     */
    public void register(String name, Map<String, Function<Object[], Object>> methods) {
        services.put(name, methods);
        log("register", name, "Subsystem \"" + name + "\" registered and ready for cross-database data sharing");
        publish("system:registered", name);
    }

    /**
     * Call a method on another subsystem to retrieve data from its database.
     */
    public Object call(String subsystem, String method, Object... args) {
        if (!services.containsKey(subsystem)) {
            throw new RuntimeException("Subsystem \"" + subsystem + "\" not registered");
        }
        if (!services.get(subsystem).containsKey(method)) {
            throw new RuntimeException("Method \"" + method + "\" not found on \"" + subsystem + "\"");
        }
        Object result = services.get(subsystem).get(method).apply(args);
        log("call", "bus", "Fetched data from " + subsystem + "." + method + "(" + Arrays.toString(args) + ")");
        return result;
    }

    public void subscribe(String event, Consumer<Object> cb) {
        subscribers.computeIfAbsent(event, k -> new ArrayList<>()).add(cb);
    }

    public void publish(String event, Object data) {
        if (subscribers.containsKey(event)) {
            for (Consumer<Object> cb : subscribers.get(event)) {
                cb.accept(data);
            }
        }
        log("event", "bus", event + ": " + data);
    }

    private void log(String type, String source, String msg) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("ts", System.currentTimeMillis());
        entry.put("type", type);
        entry.put("source", source);
        entry.put("msg", msg);
        log.add(0, entry);
        if (log.size() > 30) log.remove(log.size() - 1);
    }

    public List<Map<String, Object>> getLog() {
        return new ArrayList<>(log);
    }

    public boolean isRegistered(String name) {
        return services.containsKey(name);
    }

    public Set<String> getRegisteredSubsystems() {
        return services.keySet();
    }
}
