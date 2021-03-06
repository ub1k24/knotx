/*
 * Knot.x - Reactive microservice assembler - Templating Engine Verticle
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.engine.impl;

import com.cognifide.knotx.api.TemplateEngineRequest;
import com.cognifide.knotx.engine.TemplateEngineConfiguration;
import com.cognifide.knotx.engine.parser.HtmlFragment;
import com.cognifide.knotx.engine.service.ServiceEngine;
import com.cognifide.knotx.engine.service.ServiceEntry;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.HttpClient;
import rx.Observable;

public class TemplateSnippetProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateSnippetProcessor.class);

  private static final String START_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- start compiled snippet -->";

  private static final String END_WEBSERVICE_CALL_DEBUG_MARKER = "<!-- end compiled snippet -->";

  private final ServiceEngine serviceEngine;

  private final boolean templateDebug;

  public TemplateSnippetProcessor(HttpClient httpClient, TemplateEngineConfiguration configuration) {
    this.serviceEngine = new ServiceEngine(httpClient, configuration);
    this.templateDebug = configuration.templateDebug();
  }

  public Observable<String> processSnippet(final HtmlFragment fragment, TemplateEngineRequest request) {
    return fragment.getServices()
        .filter(serviceEntry -> serviceEntry.canServeRequest(fragment, request))
        .doOnNext(this::traceService)
        .flatMap(serviceEngine::findServiceLocation)
        .flatMap(serviceItem -> serviceEngine.doServiceCall(serviceItem, request),
            (serviceEntry, serviceResult) -> serviceEntry.setResult(serviceResult))
        .map(ServiceEntry::getResultWithNamespaceAsKey)
        .reduce(new HashMap<String, Object>(), (allResults, serviceResults) -> {
          allResults.putAll(serviceResults);
          return allResults;
        })
        .map(results -> applyData(fragment, results))
        .defaultIfEmpty(fragment.getContent());
  }

  private String applyData(final HtmlFragment snippet, Map<String, Object> serviceResult) {
    LOGGER.trace("Applying data to snippet {}", snippet);
    final StringBuilder result = new StringBuilder();

    result.append(startComment());
    result.append(snippet.getContentWithContext(serviceResult));
    result.append(endComment());

    return result.toString();
  }


  private String startComment() {
    return snippetComment(START_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String endComment() {
    return snippetComment(END_WEBSERVICE_CALL_DEBUG_MARKER);
  }

  private String snippetComment(String commentTemplate) {
    String debugLine = StringUtils.EMPTY;
    if (templateDebug) {
      debugLine = commentTemplate;
    }
    return debugLine;
  }

  private void traceService(ServiceEntry serviceEntry) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Found service call definition: {}", serviceEntry.getServiceUri());
    }
  }

}
