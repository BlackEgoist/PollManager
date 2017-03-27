package hello;

import java.net.URL;
import java.util.*;

import javafx.util.Pair;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@EnableScheduling
@RestController
@RequestMapping("/api/polls")

public class PollController {

    private final LinkedList<Poll> active = new LinkedList<>();
    private final List<Poll> closed = new ArrayList<>();
    private final PollList pollList = new PollList();
    private final long ACTIVE_POLLS_NUMBER = 4;
    private final int DELAY_MILLS = 30000;
    private final int MIN_OPTION_NUMBER = 30000;
    private final int OPTION_RANGE = 30000;

    class PollList {
        private final LinkedList<Poll> active = PollController.this.active;
        private final List<Poll> closed = PollController.this.closed;
        private String path;

        public void setPath(String path) {
            this.path = path;
        }

        public Map<String, String> getActive() {
            Map<String, String> res = new TreeMap<String, String>();
            for(Poll p: active){
                res.put(p.getTitle(),path + "/" + p.getId());
            }
            return res;
        }

        public Map<String, String> getClosed() {
            Map<String, String> res = new TreeMap<String, String>();
            for(Poll p: closed){
                res.put(p.getTitle(),path + "/" + p.getId());
            }
            return res;
        }
    }

    private Poll createMyPoll() {
        int optionsNumber = (int) (MIN_OPTION_NUMBER + Math.random() * OPTION_RANGE);
        List<String> options = new ArrayList<>(optionsNumber);
        for (int i = 0; i < optionsNumber; ++i) {
            options.add("option" + i);
        }
        int pollsNumber = active.size() + closed.size();
        return new Poll(pollsNumber, "title" + pollsNumber, "description" + pollsNumber, options);
    }

    @Scheduled(fixedDelay = this.DELAY_MILLS)
    public void managePolls() {
        if (active.isEmpty()) {
            for (int i = 0; i < ACTIVE_POLLS_NUMBER; i++) {
                active.add(createMyPoll());
            }
        } else {
            Poll first = active.removeFirst();
            first.close();
            closed.add(first);
            active.add(createMyPoll());
        }
    }

    @RequestMapping
    public PollList getPollsList(HttpServletRequest request) {
        pollList.setPath(request.getRequestURI());
        return pollList;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Poll> getPollInfo(@PathVariable("id") int id) {

        for (Poll p : active) {
            if (p.getId() == id) {
                return new ResponseEntity<Poll>(p, HttpStatus.OK);
            }
        }
        for (Poll p : closed) {
            if (p.getId() == id) {
                return new ResponseEntity<Poll>(p, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Poll> vote(@PathVariable("id") int id, @RequestParam(value = "option") String option) {
        for (Poll p : active) {
            if (p.getId() == id) {
                for(Poll.PollOption opt : p.getOptions()){
                    if(opt.getTitle().equals(option))  {
                        opt.getCounter().incrementAndGet();
                        return new ResponseEntity<Poll>(p, HttpStatus.OK);
                    }
                }
                return new ResponseEntity<Poll>(p, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);
    }


}
