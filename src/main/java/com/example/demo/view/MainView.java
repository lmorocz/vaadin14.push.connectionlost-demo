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

package com.example.demo.view;

import java.util.Optional;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

@Route
@Push
public class MainView extends VerticalLayout {

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		add(
				new H2("Hit F5 in a Chromium based browser then watch the app log for LoggingTomcatEndpointThreadPoolExecutor messages!"),
				new Paragraph(),
				new H3("Current Session/UI is " + getUiId())
		);
	}

	public static String getUiId() {
		return String.format("%s/%s",
				Optional.ofNullable(VaadinSession.getCurrent()).flatMap(s -> Optional.ofNullable(s.getSession()).map(WrappedSession::getId)).orElse("?"),
				Optional.ofNullable(UI.getCurrent()).map(UI::getUIId).orElse(-1)
		);
	}

}