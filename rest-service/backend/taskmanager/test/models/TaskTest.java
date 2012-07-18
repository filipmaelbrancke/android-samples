package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Test;

public class TaskTest {

	@Test
	public void createTask() {
		running(fakeApplication(), new Runnable() {
			
			@Override
			public void run() {
				Task task = new Task();
				task.label = "test";
				task.save();
				assertThat(task.id).isNotNull();
			}
		}
		);
	}
}
