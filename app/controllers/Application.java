package controllers;

import models.Event;
import org.joda.time.DateTime;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.calendar;
import views.html.formEdit;
import views.html.formNew;
import views.html.list;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Application extends Controller {

    final static Form<Event> eventForm = form(Event.class);

    /**
     * Checks if events ends the same day which starts
     * @param start Date
     * @param end Date
     * @return Boolean: True if ends same day
     */
    private static Boolean endsSameDay(Date start, Date end){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(start).equals(dateFormat.format(end));
    }


    /**
     * Returns list of events for calendar view
     * @param start Long Timestamp of current view start
     * @param end Long Timestamp of current view end
     * @return Result
     */
    public static Result json(Long start, Long end) {

        Date startDate = new Date(start*1000);
        Date endDate = new Date(end*1000);

        List<Event> resultList = Event.findInDateRange(startDate, endDate);
        ArrayList<Map<Object, Serializable>> allEvents = new ArrayList<Map<Object, Serializable>>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Event event : resultList) {
            Map<Object, Serializable> eventRemapped = new HashMap<Object, Serializable>();
            eventRemapped.put("id", event.id);
            eventRemapped.put("title", event.title);
            eventRemapped.put("start", df.format(event.start));
            eventRemapped.put("end", df.format(event.end));
            eventRemapped.put("allDay", event.allDay);
            eventRemapped.put("url", controllers.routes.Application.edit(event.id).toString());

            allEvents.add(eventRemapped);
        }
        return ok(play.libs.Json.toJson(allEvents));
    }


    /**
     * Displays full calendar
     * @return Result
     */
    public static Result calendar() {
        return ok(calendar.render("Title of this calendar..."));
    }


    /**
     * List of events in table view
     * @return Result
     */
    public static Result list() {
        List<Event> events = Event.find.order().desc("start").findList();
        return ok(list.render(events));
    }


    /**
     * Displays blank form
     * @return Result
     */
    public static Result blank() {
        return ok(formNew.render(eventForm));
    }


    /**
     * Save new event in DB (a.k.a. submit action in other examples)
     * @return Result
     */
    public static Result add() {
        Form<Event> eventForm = form(Event.class).bindFromRequest();
        if (eventForm.hasErrors()) {
            return badRequest(formNew.render(eventForm));
        }

        Event newEvent = eventForm.get();

        newEvent.allDay = newEvent.allDay != null;
        if (newEvent.end == null) {
            newEvent.end = new DateTime(newEvent.start).plusMinutes(30).toDate();
        }
        newEvent.endsSameDay = endsSameDay(newEvent.start, newEvent.end);
        newEvent.save();
        return redirect(controllers.routes.Application.list());
    }


    /**
     * Dislays form for editing existing event
     * @param id Long
     * @return Result
     */
    public static Result edit(Long id) {
        Event event = Event.find.byId(id);
        Form<Event> eventForm = form(Event.class).fill(event);
        return ok(formEdit.render(id, eventForm, event));
    }


    /**
     * Save new event in DB (a.k.a. submit action in other examples)
     * @param id Long
     * @return Result
     */
    public static Result update(Long id) {
        Form<Event> eventForm = form(Event.class).bindFromRequest();
        if (eventForm.hasErrors()) {
            return badRequest(formEdit.render(id, eventForm, Event.find.byId(id)));
        }
        Event updatedEvent = eventForm.get();
        updatedEvent.allDay = updatedEvent.allDay != null;
        if (updatedEvent.end == null) {
            updatedEvent.end = new DateTime(updatedEvent.start).plusMinutes(30).toDate();
        }
        updatedEvent.endsSameDay = endsSameDay(updatedEvent.start, updatedEvent.end);
        updatedEvent.update(id);
        return redirect(controllers.routes.Application.list());
    }


    /**
     * Deletes event
     * @param id Long
     * @return Result
     */
    public static Result delete(Long id) {
        Event.find.ref(id).delete();
        return redirect(controllers.routes.Application.list());
    }


    /**
     * Adds event after clicking on calendar
     * @return Result
     */
    public static Result addByAjax() {
        Form<Event> eventForm = form(Event.class).bindFromRequest();
        Event newEvent = eventForm.get();
        newEvent.endsSameDay = endsSameDay(newEvent.start, newEvent.end);
        newEvent.save();

        if (eventForm.hasErrors()){
            return badRequest("There was some errors in form");
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("id", newEvent.id.toString());
        result.put("url", controllers.routes.Application.edit(newEvent.id).toString());

        return ok(play.libs.Json.toJson(result));
    }


    /**
     * Saves in DB date changed by event drag
     * @return Result
     */
    public static Result move() {

        Long id = Long.valueOf(form().bindFromRequest().get("id"));
        int dayDelta = Integer.parseInt(form().bindFromRequest().get("dayDelta"));
        int minuteDelta = Integer.parseInt(form().bindFromRequest().get("minuteDelta"));

        Event event = Event.find.byId(id);
        event.start = new DateTime(event.start).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.end = new DateTime(event.end).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.allDay = Boolean.valueOf(form().bindFromRequest().get("allDay"));
        event.endsSameDay = endsSameDay(event.start, event.end);
        event.update();

//        if (thereIsSomeError){
//            return badRequest("You can not move this event!");
//        }

        return ok("changed");
    }

    /**
     * Saves in DB end date changed by event resize
     * @return Result
     */
    public static Result resize() {

        Long id = Long.valueOf(form().bindFromRequest().get("id"));
        int dayDelta = Integer.parseInt(form().bindFromRequest().get("dayDelta"));
        int minuteDelta = Integer.parseInt(form().bindFromRequest().get("minuteDelta"));

        Event event = Event.find.byId(id);
        event.end = new DateTime(event.end).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.endsSameDay = endsSameDay(event.start, event.end);
        event.update();

//        if (thereIsSomeError){
//            return badRequest("You can not resize this event!");
//        }

        return ok("changed");
    }


}