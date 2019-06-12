package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry any) {

        String sql = "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row = this.jdbcTemplate.update(new PreparedStatementCreator() {
                                     @Override
                                     public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                                         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                         ps.setLong(1, any.getId());
                                         ps.setLong(2, any.getProjectId());
                                         ps.setLong(3, any.getUserId());
                                         ps.setString(4, any.getDate().toString());
                                         ps.setInt(5, any.getHours());
                                         return ps;
                                     }
                                 }

                , keyHolder);

        Long newId = keyHolder.getKey().longValue();
        any.setId(newId);
        return any;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        TimeEntry timeEntry;
        String sql = "SELECT * FROM time_entries WHERE id = ?";
        try {
            timeEntry = (TimeEntry) this.jdbcTemplate.queryForObject(
                    sql, new Object[]{timeEntryId},
                    new BeanPropertyRowMapper(TimeEntry.class)
            );
        }catch (Exception e) {
                return null;
        }

        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> timeEntryList = new ArrayList<TimeEntry>();
        String sql = "SELECT * FROM time_entries";
        try {
            List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(sql);
            for (Map row : rows) {
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setId((Long)(row.get("id")));
                timeEntry.setProjectId((Long)row.get("project_id"));
                timeEntry.setUserId((Long)row.get("user_id"));
                timeEntry.setDate(((Date) row.get("date")).toLocalDate());
                timeEntry.setHours((Integer)row.get("hours"));
                timeEntryList.add(timeEntry);
            }
        }catch (Exception e) {
            return null;
        }

        return timeEntryList;
    }

    @Override
    public TimeEntry update(long eq, TimeEntry any) {

        String sql = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";

        this.jdbcTemplate.update(
                "UPDATE time_entries SET id = ?, project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?",
                eq, any.getProjectId(), any.getUserId(), any.getDate(), any.getHours(), eq);

        return find(eq);
    }

    @Override
    public void delete(long timeEntryId) {
        String sql = "DELETE FROM time_entries WHERE id ="+timeEntryId;
        this.jdbcTemplate.execute(sql);
    }
}
