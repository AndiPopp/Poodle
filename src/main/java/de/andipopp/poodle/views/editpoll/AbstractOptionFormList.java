package de.andipopp.poodle.views.editpoll;

import java.util.List;

import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.views.components.HasValueFields;

public abstract class AbstractOptionFormList<F extends AbstractOptionForm> extends VerticalLayout 
		implements HasValueFields {

	private final AbstractPoll<?, ?> poll;
	
	private List<F> optionForms;

	private HorizontalLayout toolbar = new HorizontalLayout();
	
	private Button addOptionButton = new Button("Add Option");
	
	/**
	 * @param poll
	 */
	public AbstractOptionFormList(AbstractPoll<?, ?> poll) {
		this.poll = poll;
		toolbar.setDefaultVerticalComponentAlignment(Alignment.END);
		toolbar.add(addOptionButton);
		this.add(toolbar);
		addOptionButton.addClickListener(e -> addEmptyForm());
		addOptionButton.addClassName("primary-text");
		addOptionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
	}
	
	protected abstract void buildList();
	
	/**
	 * Getter for {@link #poll}
	 * @return the {@link #poll}
	 */
	public AbstractPoll<?, ?> getPoll() {
		return poll;
	}

	/**
	 * Getter for {@link #optionForms}
	 * @return the {@link #optionForms}
	 */
	public List<F> getOptionForms() {
		return optionForms;
	}

	/**
	 * Setter for {@link #optionForms}
	 * @param optionForms the {@link #optionForms} to set
	 */
	public void setOptionForms(List<F> optionForms) {
		this.optionForms = optionForms;
	}
	
	@Override
	public void addValueChangeListenerToFields(ValueChangeListener<ValueChangeEvent<?>> listener) {
		for(AbstractOptionForm form : getOptionForms()) {
			form.addValueChangeListenerToFields(listener);
		}
	}
	
	/**
	 * Add a new option form to this list
	 * @param form the option form to add
	 */
	public void addForm(F form) {
		form.addRemoveOptionFormEventListener(e -> removeForm(form));
		optionForms.add(form);
		this.add(form);
	}
	
	/**
	 * Remove a form from this layout
	 * @param form the form to remove
	 */
	public void removeForm(F form) {
		optionForms.remove(form);
		this.remove(form);
	}
	
	/**
	 * Add a new form with a complete new option
	 */
	public abstract void addEmptyForm();
}
