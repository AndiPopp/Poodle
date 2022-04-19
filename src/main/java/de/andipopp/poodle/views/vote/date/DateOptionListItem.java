package de.andipopp.poodle.views.vote.date;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractOption;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.util.VaadinUtils;
import de.andipopp.poodle.views.vote.OptionListItem;

/**
 * A box to display a single date option
 * @author Andi Popp
 *
 */
public class DateOptionListItem extends OptionListItem {

	ZoneId zoneId = ZoneId.systemDefault();
	
	/**
	 * @param option
	 * @param vote
	 */
	public DateOptionListItem(AbstractOption<?, ?> option, Vote<?, ?> vote, User currentUser) {
		super(option, vote, currentUser);
	}

	/**
	 * @param option
	 */
	public DateOptionListItem(AbstractOption<?, ?> option, User currentUser) {
		super(option, currentUser);
	}

	/**
	 * Getter for {@link #option}
	 * @return the {@link #option}
	 */
	@Override
	protected DateOption getOption() {
		return (DateOption) super.getOption();
	}
	
	/**
	 * Getter for {@link #zoneId}
	 * @return the {@link #zoneId}
	 */
	protected ZoneId getZoneId() {
		return zoneId;
	}

	/**
	 * Setter for {@link #zoneId}
	 * @param zoneId the {@link #zoneId} to set
	 */
	protected void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	protected String labelText() {
		String result = getOption().getZonedTimeStartEnd(zoneId);
		String connector = " (";
		if (getOption().getTitle() != null && !getOption().getTitle().isEmpty()) {
			result += connector + JSoupUtils.cleanNone(getOption().getTitle());
			connector = " / ";
		}
		if (getOption().getLocation() != null && !getOption().getLocation().isEmpty()) {
			result += connector + "<i class=\"las la-map-marker\"></i>" + JSoupUtils.cleanBasic(getOption().getLocation());
			connector = " / ";
		}
		if (connector.equals(" / ")) result += ")";
		return result;
	}

	@Override
	protected Component left() {
		VerticalLayout left = new VerticalLayout();
		left.setSpacing(false);
		left.setPadding(false);
//		left.getStyle().set("border", "2px dotted Red"); //for debug purposes
		Span monthDay = new Span(""+getOption().getZonedStartDay(zoneId));
		monthDay.getStyle().set("font-weight", "bold");
		monthDay.getStyle().set("font-size", "x-large");
		monthDay.getStyle().set("margin-bottom", "0px");
		monthDay.getStyle().set("margin-top", "0px");
//		monthDay.getStyle().set("border", "2px dotted Green"); //for debug purposes
		Span dayJumpMarker = null;
		long dayJumps = getOption().getZonedDayJumps(zoneId);
		if(dayJumps > 0) {
			dayJumpMarker = new Span("(+" + dayJumps +")");
			dayJumpMarker.getStyle().set("margin-bottom", "1ex");
		}
		Span bottom = new Span(getOption().getZonedStartWeekday(zoneId, null));
//		bottom.getStyle().set("border", "2px dotted Green"); //for debug purposes
		HorizontalLayout topWrapper = new HorizontalLayout(monthDay);
		if (dayJumpMarker != null) topWrapper.add(dayJumpMarker);
		topWrapper.setPadding(false);
		topWrapper.setSpacing(false);
		topWrapper.setDefaultVerticalComponentAlignment(Alignment.END);
		left.add(topWrapper, bottom);
		
		HorizontalLayout leftWrapper = new HorizontalLayout(left);
		leftWrapper.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		leftWrapper.setPadding(false);
		return leftWrapper;
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(VaadinUtils.getLocaleFromVaadinRequest());
	private static DateTimeFormatter weekDayFormatter = DateTimeFormatter.ofPattern("E", VaadinUtils.getLocaleFromVaadinRequest());
	private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", VaadinUtils.getLocaleFromVaadinRequest());
	
	@Override
	protected String optionSummary() {
		String result = "";
		String connector = ", ";
		String currentConnector = "";
		if (getOption().getTitle() != null) {
			result += currentConnector + JSoupUtils.cleanNone(getOption().getTitle());
			currentConnector = connector;
		}
		
		result += currentConnector + weekDayFormatter.format(getOption().getZonedStart(zoneId));
		result += " " + formatter.format(getOption().getZonedStart(zoneId));
		long dayJumps = getOption().getZonedDayJumps(zoneId);
		if (dayJumps > 0) result += " (+" + dayJumps +")";
		result += " " + timeFormatter.format(getOption().getZonedStart(zoneId));
		currentConnector = connector;
				
		if (getOption().getLocation() != null) {
			result += currentConnector + getOption().getLocation();
			currentConnector = connector;
		}
				
		return result;
	}
	
	
	
}
