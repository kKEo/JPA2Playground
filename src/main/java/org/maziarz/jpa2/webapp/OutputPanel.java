package org.maziarz.jpa2.webapp;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class OutputPanel extends Panel {

	public OutputPanel(String id, IModel<String> model) {
		super(id, model);

		add(new TextArea<String>("sqlStmts", model));

	}

	private static final long serialVersionUID = 1L;

}
