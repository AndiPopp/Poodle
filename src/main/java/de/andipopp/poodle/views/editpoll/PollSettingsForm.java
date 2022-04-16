package de.andipopp.poodle.views.editpoll;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;

/**
 * A type of {@link FormLayout} to manage settings for all types of {@link AbstractPoll}s.
 * The form has input fields for all possible types of polls which are named to be bound by a {@link Binder}.
 * The input fields to be shown are configured via {@link #configureInputFields(AbstractPoll)}.
 * @author Andi Popp
 *
 */
public class PollSettingsForm extends PollBindableForm {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Input field for {@link AbstractPoll#isEnableIfNeedBe()}
	 */
	private Checkbox enableIfNeedBe = new Checkbox("Enable 'if-need-be' answers.");
	
	/**
	 * Input field for {@link AbstractPoll#isEnableAbstain()}
	 */
	private Checkbox enableAbstain = new Checkbox("Allow abstaining from options.");

	/**
	 * Construct a new form and configure it according to the poll
	 * @param poll the poll used for {@link #configureInputFields(AbstractPoll)}
	 */
	public PollSettingsForm(AbstractPoll<?, ?> poll) {
		configureInputFields(poll);
		this.setMaxWidth("800px");  //two column layout starts to look weird at certain width
	}
	
	/**
	 * Configure the fields to be shown on this form
	 * @param poll
	 */
	public void configureInputFields(AbstractPoll<?, ?> poll) {
		removeAll();
		
		//Currently we only have these settings
		add(enableIfNeedBe, enableAbstain);
	}
	
}
