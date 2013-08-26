package org.maziarz.jpa2.webapp;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.maziarz.jpa2.GenerateDdlsSeviceImpl;

public class GeneratePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public GeneratePage(final PageParameters parameters) {
		super(parameters);

		final Model<String> inputModel = new Model<String>();
		inputModel.setObject("");

		Form<String> form = new Form<String>("generateDdlForm", inputModel);

		InputPanel input = new InputPanel("inputPanel", inputModel);
		form.add(input);

		final Model<String> outputModel = new Model<String>();
		outputModel.setObject("");
		final OutputPanel outputPanel = new OutputPanel("outputPanel", outputModel);
		outputPanel.setOutputMarkupId(true);
		add(outputPanel);

		AjaxButton ajaxButton = new AjaxButton("generate") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

				Object modelObject = form.getDefaultModelObject();

				GenerateDdlsSeviceImpl s = new GenerateDdlsSeviceImpl();
				Map<String, String> toBeProcessed = new HashMap<String, String>();

				toBeProcessed.put("AllInOne", (String) modelObject);

				outputModel.setObject(s.generate(toBeProcessed));

				target.add(outputPanel);
			}

		};

		form.add(ajaxButton);

		add(form);

	}
}
