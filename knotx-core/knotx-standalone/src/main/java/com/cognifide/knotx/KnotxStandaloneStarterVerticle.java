/*
 * Knot.x - Reactive microservice assembler - Standalone Knot.x
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
package com.cognifide.knotx;

import com.cognifide.knotx.engine.TemplateEngineVerticle;
import com.cognifide.knotx.repository.RepositoryVerticle;
import com.cognifide.knotx.server.KnotxServerVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.rxjava.core.AbstractVerticle;

public class KnotxStandaloneStarterVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new RepositoryVerticle(), new DeploymentOptions().setConfig(config().getJsonObject("repository")));
    vertx.deployVerticle(new TemplateEngineVerticle(), new DeploymentOptions().setConfig(config().getJsonObject("engine")));
    vertx.deployVerticle(new KnotxServerVerticle(), new DeploymentOptions().setConfig(config().getJsonObject("server")));
  }
}
