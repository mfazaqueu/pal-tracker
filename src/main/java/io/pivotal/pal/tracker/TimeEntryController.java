package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(TimeEntryRepository repo, MeterRegistry meterRegistry){

        this.timeEntryRepository = repo;
        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry response = this.timeEntryRepository.create(timeEntryToCreate);
        if(response != null){
            actionCounter.increment();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else{
            return null;
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry response = this.timeEntryRepository.find(id);
        if(response != null){
            actionCounter.increment();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry expected) {
        TimeEntry response = this.timeEntryRepository.update(id, expected);
        if(response != null){
            actionCounter.increment();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable long id) {
        /*TimeEntry response = this.timeEntryRepository.find(timeEntryId);
        if(response != null){*/
            this.timeEntryRepository.delete(id);
            actionCounter.increment();
            timeEntrySummary.record(timeEntryRepository.list().size());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        /*}else{
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        }*/
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> response = this.timeEntryRepository.list();
        if(response != null){
            actionCounter.increment();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
