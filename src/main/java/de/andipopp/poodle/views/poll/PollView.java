package de.andipopp.poodle.views.poll;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.jsoup.Jsoup;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.service.PollService;
import de.andipopp.poodle.data.service.UserService;
import de.andipopp.poodle.data.service.VoteService;
import de.andipopp.poodle.util.JSoupUtils;
import de.andipopp.poodle.util.NotAUuidException;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.MainLayout;
import de.andipopp.poodle.views.poll.date.DateOptionListView;


@PageTitle("Poodle Poll")
@Route(value = "poll", layout = MainLayout.class)
@AnonymousAllowed
public class PollView extends VerticalLayout implements BeforeEnterObserver {

	private static final long serialVersionUID = 1L;
	
	public static final String ID_PARAMETER_NAME = "pollId";
	
	private PollService pollService;
	
	private VoteService voteService;

	private AbstractPoll<?,?> poll;

	private User currentUser;
	
	/* =====================
	 * = Layout Components =
	 * ===================== */
	

	private VerticalLayout content;
	
	private VerticalLayout pollContent;
	
	private HorizontalLayout header = new HorizontalLayout();
	
	private H6 subtitle = new H6();
	
	private VerticalLayout info = new VerticalLayout();

	ViewToggleState state = ViewToggleState.list;
	
	ViewToggleButton viewToggleButton = new ViewToggleButton();
	
	OptionListView<?, ?> listView;
	
	/**
	 * @param pollService
	 */
	public PollView(UserService userService, PollService pollService, VoteService voteService) {
		//remember the current user
		if (VaadinRequest.getCurrent().getUserPrincipal()!=null) {
			String userName = VaadinRequest.getCurrent().getUserPrincipal().getName(); 
			this.currentUser = userService.get(userName);
		}else {
			this.currentUser = null;
		}
		
    	//hook up the poll and vote service 
		this.pollService = pollService;
		this.voteService = voteService;
		
		this.setDefaultHorizontalComponentAlignment(Alignment.START);
		
		//we use content as an intermediary, so we remove padding from this
		this.setPadding(false);
		//add a not found as default content
		this.content = new VerticalLayout();
		this.content.add(notFound());
	    this.add(content);
//	    this.content.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
	}
	
	

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		if (queryParameters.getParameters().containsKey(ID_PARAMETER_NAME)) {
			try {
				String pollIdBase64url = queryParameters.getParameters().get(ID_PARAMETER_NAME).get(0);
				UUID pollId = UUIDUtils.base64urlToUuid(pollIdBase64url);
				Optional<AbstractPoll<?,?>> opt = pollService.get(pollId);
				if (!opt.isEmpty()) {
					loadPoll(opt.get());
				}
			} catch (NotAUuidException e) {
				//do nothing, keep the "not found"
			}
		}
	}
	
	private void loadPoll(AbstractPoll<?,?> poll) {
		//load the poll itself
		this.poll = poll;
		//set the current vote	

		//add the poll specific content
		this.pollContent = new VerticalLayout();
		this.pollContent.setPadding(false);
		this.pollContent.add(metaInfBlock());
		this.pollContent.setSpacing(false);
//		this.pollContent.getStyle().set("border", "2px dotted FireBrick"); //for debug purposes
		
		if (poll instanceof DatePoll) {
			listView = new DateOptionListView((DatePoll) poll, currentUser); //TODO read default from user settings
			this.pollContent.add(listView);
		}
		
		//strip content from all its components (especially the "not found")
		this.content.removeAll();
		HorizontalLayout pollContentWrapper = new HorizontalLayout(pollContent);
		pollContentWrapper.setPadding(false);
		pollContentWrapper.setMinWidth("50%");
		
		VerticalLayout horizontalAlignmentWrapper = new VerticalLayout();
		horizontalAlignmentWrapper.setSizeFull();
		horizontalAlignmentWrapper.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		horizontalAlignmentWrapper.add(pollContentWrapper);
		horizontalAlignmentWrapper.setPadding(false);
		this.content.add(horizontalAlignmentWrapper);
	}

	private static VerticalLayout notFound() {
		VerticalLayout notFound = new VerticalLayout();
		notFound.setSpacing(false);
		
	    Image img = new Image("images/empty-plant.png", "placeholder plant");
	    img.setWidth("200px");
	    notFound.add(img);
	
	    notFound.add(new H2("Poll not found"));
	    notFound.add(new Paragraph("Sorry, the poll could not be found. Do you have the correct URL?"));
	    notFound.setSizeFull();
	    notFound.setJustifyContentMode(JustifyContentMode.CENTER);
	    notFound.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	    notFound.getStyle().set("text-align", "center");
	    
	    return notFound;
	}
	
	/* ===============
	 * = Sub-Layouts =
	 * =============== */
	
	private Component metaInfBlock() {
		VerticalLayout metaInfBlock = new VerticalLayout();
		metaInfBlock.setPadding(false);
		metaInfBlock.setDefaultHorizontalComponentAlignment(Alignment.START);
		metaInfBlock.add(configureHeader());
		metaInfBlock.add(configureSubtitle());
		metaInfBlock.add(configureInfo());
//		metaInfBlock.getStyle().set("border", "2px dotted Red"); //for debug purposes
		return metaInfBlock; //new HorizontalLayout(metaInfBlock);
	}
	
	private Component configureHeader() {
		header.removeAll();
		header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		Avatar ownerAvatar = poll.getOwner().getAvatar();
		ownerAvatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
		ownerAvatar.getStyle().set("border", "3px solid black") ;
		this.content.add(ownerAvatar);
		H3 title = new H3(poll.getTitle());
		title.getStyle().set("display", "inline");
		title.getStyle().set("margin-top", "0ex");
		title.getStyle().set("margin-bottom", "0ex");
//		title.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		header.add(ownerAvatar, title);
//		header.getStyle().set("border", "2px dotted Red") ; //for debug purposes
		
		if (poll.getOwner().equals(currentUser)) {
			Icon editIcon = new Icon(VaadinIcon.EDIT);
			editIcon.setSize("var(var(--lumo-size-xl)"); //TODO does not work 
			header.add(new Icon(VaadinIcon.EDIT));
		}
		
		return header;
	}
	
	private Component configureSubtitle() {
		String text = "created ";
		Duration duration = Duration.between(poll.getCreateDate(), Instant.now());
		text += duration.toDays() +" day(s) ago ";
		text += "by " + poll.getOwner().getName();
		text +=  " and retained until "+poll.getDeleteDate();
		subtitle.setText(text);
		subtitle.getStyle().set("margin-top", "0ex");
//		subtitle.getStyle()
		return subtitle;
	}
	
	private Component configureInfo() {
		info.removeAll();
		HorizontalLayout lastElement = null;
		if (poll.getDescription() != null && !poll.getDescription().isBlank()) {
			HorizontalLayout description = new HorizontalInfoContainer(true);
			description.add(new Html("<p>" + Jsoup.clean(poll.getDescription(), JSoupUtils.BASIC) + "</p>"));
			description.setJustifyContentMode(JustifyContentMode.BETWEEN);
			info.add(description);
			lastElement = description;
		}
		if (poll instanceof DatePoll) {
			DatePoll datePoll = (DatePoll) poll;
			if (datePoll.getLocation() != null && !datePoll.getLocation().isBlank()) {
				HorizontalLayout location = new HorizontalInfoContainer(true);
				location.add(new HorizontalInfoContainer(false, new Icon(VaadinIcon.MAP_MARKER), new Html("<span>"+Jsoup.clean(datePoll.getLocation(), JSoupUtils.BASIC)+"</span>")));
				location.setJustifyContentMode(JustifyContentMode.BETWEEN);
				info.add(location);
				lastElement = location;
			}	
		}
		lastElement.add(viewToggleButton);
		info.setSpacing(false);
		info.setPadding(false);
//		info.getStyle().set("border", "2px dotted Green") ; //for debug purposes
		return info;
	}
	
	private static class HorizontalInfoContainer extends HorizontalLayout {

		private static final long serialVersionUID = 1L;

		/**
		 * 
		 */
		public HorizontalInfoContainer(boolean fullWidth) {
			super();
			setAttributes(fullWidth);
		}
		
		/**
		 * @param children
		 */
		public HorizontalInfoContainer(boolean fullWidth, Component... children) {
			super(children);
			setAttributes(fullWidth);
		}

		private void setAttributes(boolean fullWidth) {
			this.setPadding(false);
			if (fullWidth) this.setWidthFull();
			this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
//			this.getStyle().set("border", "2px dotted DarkOrange") ; //for debug purposes
		}
		
	}
	
	
    @NpmPackage(value = "line-awesome", version = "1.3.0")
    public static class LineAwesomeIcon extends Span {
        private static final long serialVersionUID = 1L;

		public LineAwesomeIcon(String lineawesomeClassnames) {
            if (!lineawesomeClassnames.isEmpty()) {
                addClassNames(lineawesomeClassnames);
                this.getStyle().set("font-size", "x-large");
            }
        }
    }
    
    private enum ViewToggleState {
		list, table
	}
    
    private class ViewToggleButton extends Button {
    	
    	private static final long serialVersionUID = 1L;
		
		Icon table = new Icon(VaadinIcon.TABLE);
		
		Icon list = new Icon(VaadinIcon.LINES_LIST);
		
		/**
		 * @param state
		 */
		ViewToggleButton() {
			this.setMinWidth("1em");
			setLabel();
		}
		
		private void setLabel() {
			switch (state) {
			case list:
				setIcon(table);
				break;
			case table:
				setIcon(list);
				break;
			default:
				break;
			
			}
		}
    	
    	
    	
    }
}