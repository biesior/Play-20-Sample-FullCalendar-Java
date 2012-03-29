package models;


import com.avaje.ebean.Expr;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class Event extends Model {

    @Id
    public Long id;


    @Constraints.Required
    public String title;
    public Boolean allDay;

    @Constraints.Required
    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date start = new Date();

    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date end = new Date();

    public Boolean endsSameDay;


    public static Finder<Long,Event> find = new Finder<Long,Event>(Long.class, Event.class);

    public Event(String title, Date start, Date end, Boolean allDay) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;

    }


    public static List<Event> findInDateRange(Date start, Date end) {


        return find.where().or(
                Expr.and(
                        Expr.lt("start", start),
                        Expr.gt("end", end)
                ),
                Expr.or(
                        Expr.and(
                                Expr.gt("start", start),
                                Expr.lt("start", end)
                        ),
                        Expr.and(
                                Expr.gt("end", start),
                                Expr.lt("end", end)
                        )
                )
        ).findList();
    }




}
