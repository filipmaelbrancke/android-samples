package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import org.junit.Test;

import play.mvc.Result;

public class ApplicationTest {

	@Test
	public void callIndex() {
		Result result = callAction(controllers.routes.ref.Application.index());
		assertThat(status(result)).isEqualTo(SEE_OTHER);
	}
	
	@Test
	public void callTasks() {
		running(fakeApplication(), new Runnable() {
			
			@Override
			public void run() {
				Result result = callAction(controllers.routes.ref.Application.tasks());
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentType(result)).isEqualToIgnoringCase("text/html");
				assertThat(charset(result)).isEqualToIgnoringCase("utf-8");
				assertThat(contentAsString(result)).containsIgnoringCase("task");
			}
		}
		);
	}
}
