package conf;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.GET;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import play.libs.WS;
import play.mvc.Result;

public class RoutesTest {
	
	@Test
	public void testTasksRoute() {
		running(fakeApplication(), new Runnable() {
			
			@Override
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/tasks"));
				assertThat(result).isNotNull();
			}
		}
		);
	}
	
	@Test
	public void testTasksJsonRoute() {
		running(fakeApplication(), new Runnable() {
			
			@Override
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/tasksJson"));
				assertThat(result).isNotNull();
			}
		}
		);
	}

	@Test
	public void testInServer() {
		running(testServer(6666), new Runnable() {
			
			@Override
			public void run() {
				assertThat(WS.url("http://localhost:6666").get().get().getStatus()).isEqualTo(SEE_OTHER);
			}
		}
		);
	}
}
