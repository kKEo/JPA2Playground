package org.maziarz.jpa2.webapp;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.maziarz.jpa2.GenerateDdlsSeviceImpl;
import org.maziarz.jpa2.JavaSourceInputScanner;

public class GeneratePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public GeneratePage(final PageParameters parameters) {
		super(parameters);

		final Model<String> inputModel = new Model<String>();
		inputModel.setObject("");

		final Model<String> outputModel = new Model<String>();
		final OutputPanel outputPanel = new OutputPanel("outputPanel", outputModel);
		outputPanel.setOutputMarkupId(true);
		add(outputPanel);
		
		
		Form<String> form = new Form<String>("generateDdlForm", inputModel) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				
				Object modelObject = this.getDefaultModelObject();

				String resp = "";
				try {
				GenerateDdlsSeviceImpl s = new GenerateDdlsSeviceImpl();
				
				Map<String, String> toBeProcessed = JavaSourceInputScanner.process((String) modelObject);
				
				
				resp = s.generate(toBeProcessed);
				} catch (Exception e) {
					resp = e.toString();
				}

				outputModel.setObject(resp);

			}
			
		};
		
		Button submitButton = new Button("submitButton");
		submitButton.add(new AjaxFormSubmitBehavior(form, "click"){
			
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				System.out.println("Click");
				target.add(outputPanel);
			}
			private static final long serialVersionUID = 1L;});
		
		add(submitButton);

		InputPanel input = new InputPanel("inputPanel", inputModel);
		form.add(input);



		AjaxButton ajaxButton = new AjaxButton("generate") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				System.out.println("Nothing here");
			}

		};

		form.add(ajaxButton);

		add(form);

	}
}
