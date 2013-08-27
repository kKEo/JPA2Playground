package org.maziarz.jpa2.webapp;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class InputPanel extends Panel {

	public InputPanel(String id, IModel<String> model) {
		super(id, model);

		add(new TextArea<String>("javaSources", model));

	}

	private static final long serialVersionUID = 1L;

}
