package jp.cloudgarden.server.threads;

import jp.cloudgarden.server.jax.CloudController;

public class ScheduleCheckTread extends Thread {
	private CloudController controller;
	private boolean isRunning = false;
	private int intervalSec = 60;

	public ScheduleCheckTread(CloudController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			controller.checkSchedules();

			try {
				Thread.sleep(intervalSec * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopThread(){
		this.isRunning = false;
	}

	public boolean isRunning(){
		return isRunning;
	}

}
