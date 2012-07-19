package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.mvc.Result;

public class ApplicationTest {

	@Test
	public void callIndex() {
		Result result = callAction(controllers.routes.ref.Application.index());
		assertThat(status(result)).isEqualTo(SEE_OTHER);
		assertThat(redirectLocation(result)).isEqualToIgnoringCase("/tasks");
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
	
	@Test
	public void createNewTask() {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			
			@Override
			public void run() {
				Result result = callAction(controllers.routes.ref.Application.newTask());
				
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				
				Map<String, String> data = new HashMap<String, String>();
				
				// label is required
				data.put("label", null);
				
				result = callAction(
						controllers.routes.ref.Application.newTask(), 
						fakeRequest().withFormUrlEncodedBody(data)
				);
				
				assertThat(status(result)).isEqualTo(BAD_REQUEST);
				
				data.put("label", "foo");
				
				result = callAction(
						controllers.routes.ref.Application.newTask(), 
						fakeRequest().withFormUrlEncodedBody(data)
				);
				
				assertThat(status(result)).isEqualTo(SEE_OTHER);
				assertThat(redirectLocation(result)).isEqualToIgnoringCase("/tasks");
				
				result = callAction(controllers.routes.ref.Application.tasks());
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentAsString(result)).containsIgnoringCase("5 task(s)");  // 4 in initial data + this one
			}
		}
		);
	}
}
