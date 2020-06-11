/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.view.MainView;
import com.vaadin.flow.internal.CurrentInstance;

@Configuration
public class TomcatCustomizerConfig {

	private static final class LoggingTomcatEndpointThreadPoolExecutor extends ThreadPoolExecutor {

		private static final Logger LOG = LoggerFactory.getLogger(LoggingTomcatEndpointThreadPoolExecutor.class);

		public LoggingTomcatEndpointThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			if (!CurrentInstance.getInstances().isEmpty()) {
				LOG.warn("Vaadin CurrentInstance is not empty after execution, clearing now. UI: {} ", MainView.getUiId());
				CurrentInstance.clearAll();
			}
			super.afterExecute(r, t);
		}

	}

	@Bean
	public TomcatProtocolHandlerCustomizer<AbstractProtocol<?>> protocolHandlerCustomizer() {
		return ph -> ph.setExecutor(createExecutor(true, ph.getThreadPriority(), ph.getMinSpareThreads(), ph.getMaxThreads()));
	}

	/**
	 * Based on org.apache.tomcat.util.net.AbstractEndpoint#createExecutor()
	 */
	private ThreadPoolExecutor createExecutor(boolean daemon, int threadPriority, int minSpareThreads, int maxThreads) {
		TaskQueue taskqueue = new TaskQueue();
		TaskThreadFactory tf = new TaskThreadFactory("demo-exec-", daemon, threadPriority);
		ThreadPoolExecutor executor = new LoggingTomcatEndpointThreadPoolExecutor(minSpareThreads, maxThreads, 60, TimeUnit.SECONDS, taskqueue, tf);
		taskqueue.setParent(executor);
		return executor;
	}

}