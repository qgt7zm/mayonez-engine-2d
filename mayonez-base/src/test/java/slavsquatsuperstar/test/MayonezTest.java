package slavsquatsuperstar.test;

import slavsquatsuperstar.mayonez.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Driver code for new features in the engine.
 */
public class MayonezTest {

    public static void main(String[] args) throws Exception {
        EventListener myListener1 = event -> Logger.log("Listener 1 reports: %s", event);
        EventListener myListener2 = event -> Logger.log("Listener 2 reports: %s", event);
        EventGenerator myGenerator = new EventGenerator();
        myGenerator.addListener(myListener1);
        myGenerator.addListener(myListener2);
        myGenerator.createEvent(new Event("Clankers inbound!"));
    }

    interface EventListener {
        void onReceiveEvent(Event event);
    }

    static class Event {
        private final String msg;

        public Event(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return String.format("Event (%s)", msg);
        }
    }

    static class EventGenerator {
        private final List<EventListener> listeners = new ArrayList<>();

        public void addListener(EventListener e) {
            listeners.add(e);
        }

        public void removeListener(EventListener e) {
            listeners.remove(e);
        }

        public void createEvent(Event event) {
            listeners.forEach(l -> l.onReceiveEvent(event));
        }
    }

}
