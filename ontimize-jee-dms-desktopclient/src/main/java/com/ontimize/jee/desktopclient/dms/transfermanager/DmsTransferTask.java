package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.util.Observable;

import com.ontimize.jee.desktopclient.dms.taskmanager.ITask;
import com.ontimize.jee.desktopclient.dms.taskmanager.TaskStatus;

public class DmsTransferTask implements ITask {

	private final AbstractDmsTransferable transferable;

	public DmsTransferTask(AbstractDmsTransferable transferable) {
		super();
		this.transferable = transferable;
	}

	public AbstractDmsTransferable getTransferable() {
		return this.transferable;
	}

	@Override
	public String getName() {
		return this.transferable.getName();
	}

	@Override
	public Number getSize() {
		return this.transferable.getSize();
	}

	@Override
	public Number getProgress() {
		return this.transferable.getProgress();
	}

	@Override
	public TaskStatus getStatus() {
		switch (this.transferable.getStatus()) {
			case CANCELLED:
				return TaskStatus.CANCELLED;
			case COMPLETED:
				return TaskStatus.COMPLETED;
			case ERROR:
				return TaskStatus.ERROR;
			case ON_PREPARE:
				return TaskStatus.ON_PREPARE;
			case PAUSED:
				return TaskStatus.PAUSED;
			case DOWNLOADING:
			case UPLOADING:
				return TaskStatus.RUNNING;
			default:
				return null;
		}
	}

	@Override
	public void pause() {
		this.transferable.pause();
	}

	@Override
	public void resume() {
		this.transferable.resume();
	}

	@Override
	public void cancel() {
		this.transferable.cancel();
	}

	@Override
	public Observable getObservable() {
		return this.transferable;
	}

	@Override
	public void onTaskClicked() {
		// do nothing
	}

	@Override
	public String getDescription() {
		return null;
	}

}
