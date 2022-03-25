package com.ljt.study.rocketmq.core;

import org.apache.rocketmq.common.message.MessageExt;

@FunctionalInterface
public interface MessageErrorHandler {

	/**
	 * Handle the given error, possibly rethrowing it as a fatal exception.
	 */
	void handleError(MessageExt message, Exception e);

	default boolean isPushAgain() {
		return true;
	}

}