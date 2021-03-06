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
package com.cognifide.knotx.engine.placeholders;

import com.cognifide.knotx.api.TemplateEngineRequest;

public interface PlaceholderSubstitutor {

  /**
   * Get the replacement value from the supplied request and placeholder name
   *
   * @param request     the supplied template engine request
   * @param placeholder the placeholder name
   * @return the replacement value, or null if no replacement can be get
   */
  String getValue(TemplateEngineRequest request, String placeholder);

}
