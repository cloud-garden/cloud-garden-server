package jp.cloudgarden.server.threads;

import jp.cloudgarden.server.jax.CloudController;

public class ScheduleCheckThread extends Thread {
	private static CloudController controller;
	private boolean isRunning = false;
	private int intervalSec = 60;

	public ScheduleCheckThread(CloudController controller) {
		System.out.println("schedule check thread is generated : "+Thread.currentThread().toString());
		ScheduleCheckThread.controller = controller;
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
