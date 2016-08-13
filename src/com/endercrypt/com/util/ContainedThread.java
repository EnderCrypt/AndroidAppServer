package com.endercrypt.com.util;

import java.util.Optional;

public abstract class ContainedThread
{
	private Optional<Thread> thread = Optional.empty();

	public boolean start()
	{
		if (thread.isPresent())
			return false;
		thread = Optional.of(new Thread(new ThreadWorker()));
		getThread().start();
		return true;
	}

	public boolean interrupt()
	{
		if (thread.isPresent() == false)
			return false;
		getThread().interrupt();
		return true;
	}

	public boolean isRunning()
	{
		return (thread.isPresent());
	}

	public Thread getThread()
	{
		return thread.get();
	}

	private class ThreadWorker implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ContainedThread.this.run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			thread = Optional.empty();
		}
	}

	protected abstract void run() throws Exception;
}
