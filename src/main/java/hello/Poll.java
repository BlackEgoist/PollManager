package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Poll {
    private final long id;
    private final String title;
    private final String description;
    private boolean isActive = true;
    private final List<PollOption> options;

    public Poll(long id, String title, String description, List<String> options) {
        this.id = id;
        this.title = title;
        this.options = new ArrayList<>(options.size());
        for (String option : options) {
            this.options.add(new PollOption(option));
        }
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public String getDescription() {
        return description;
    }


    public boolean isActive() {
        return this.isActive;
    }


    public boolean vote(String optionTitle) {
        if (!isActive) {
            return false;
        }
        for (PollOption p : this.options) {
            if (p.title.equals(optionTitle)) {
                p.counter.incrementAndGet();
                return true;
            }
        }
        return false;
    }

    public void close() {
        this.isActive = false;
    }

    class PollOption {

        private final String title;
        private AtomicLong counter;


        public PollOption(String title) {
            this.title = title;
            this.counter = new AtomicLong();
        }

        public String getTitle() {
            return title;
        }

        public AtomicLong getCounter() {
            return counter;
        }
    }
}
