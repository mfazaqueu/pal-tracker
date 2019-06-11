package io.pivotal.pal.tracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    private List<TimeEntry> repo;
    private long index;

    public InMemoryTimeEntryRepository(){
        this.repo = new ArrayList<TimeEntry>();
        this.index = 1L;
    }

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(this.index);
        this.repo.add(timeEntry);
        this.index++;
        return timeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {

        for (TimeEntry timeEntry:this.repo) {
            if( timeEntry.getId() == timeEntryId ){
                return timeEntry;
            }
        }

        return null;
    }

    public TimeEntry find(Long id) {

        return new TimeEntry();
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {

        for (int i = 0; i < this.repo.size(); i++){

            TimeEntry timeEntryRepo = this.repo.get(i);
            if( timeEntryRepo.getId() == id ){
                TimeEntry timeEntryX = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
                this.repo.set(i, timeEntryX);
                return timeEntryX;
            }

        }

        return null;
    }

    @Override
    public void delete(long timeEntryId) {
        for (int i = 0; i < this.repo.size(); i++){

            TimeEntry timeEntryRepo = this.repo.get(i);
            if( timeEntryRepo.getId() == timeEntryId ){
                this.repo.remove(i);
            }

        }
    }

    public void delete(Object id) {

    }

    public List<TimeEntry> list() {
        return this.repo;
    }
}
