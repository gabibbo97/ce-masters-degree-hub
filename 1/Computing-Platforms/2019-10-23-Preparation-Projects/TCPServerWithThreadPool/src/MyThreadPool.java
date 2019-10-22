import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import logger.MyLogger;

public class MyThreadPool implements Executor {

	public class Worker extends Thread {

		public boolean busy = false;
		public boolean shutdown = false;

		private final BlockingQueue<Runnable> tasksQueue;

		public Worker(BlockingQueue<Runnable> tasksQueue) {
			this.tasksQueue = tasksQueue;
		}

		@Override
		public void run() {
			MyLogger.getInstance().log("Worker alive");
			while (!shutdown) {
				busy = false;
				try {
					final Runnable task = tasksQueue.take();
					busy = true;
					task.run();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			MyLogger.getInstance().log("Worker stopping");
		}
	}

	private final BlockingQueue<Runnable> tasksQueue;
	private final List<MyThreadPool.Worker> workers;

	private final int maxThreads = Runtime.getRuntime().availableProcessors();

	public MyThreadPool() {
		tasksQueue = new ArrayBlockingQueue<>(maxThreads * 4);
		workers = new ArrayList<>();
	}

	@Override
	public void execute(Runnable runnable) {
		long idleWorkers = workers.stream().filter(worker -> !worker.busy).count();
		final long workersCount = workers.size();
		if (workersCount < maxThreads && idleWorkers == 0) {
			final MyThreadPool.Worker worker = new MyThreadPool.Worker(tasksQueue);
			worker.start();
			workers.add(worker);
			MyLogger.getInstance().log("Spawned a worker thread");
		}

		tasksQueue.add(runnable);

		idleWorkers = workers.stream().filter(worker -> !worker.busy).count();
		if (tasksQueue.size() == 0 && idleWorkers > 0) {
			workers.stream().filter(worker -> !worker.busy).forEach(worker -> {
				worker.shutdown = true;
				MyLogger.getInstance().log("Stopped a worker thread");
			});

		}
	}

}
