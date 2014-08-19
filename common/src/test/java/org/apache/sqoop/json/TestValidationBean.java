/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.json;

import org.apache.sqoop.common.Direction;
import org.apache.sqoop.validation.Status;
import org.apache.sqoop.validation.Validation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class TestValidationBean {

  @Test
  public void testJobValidationBeanSerialization() {
    // Serialize it to JSON object
    JobValidationBean bean = new JobValidationBean(
      getValidation(Status.FINE),
      getValidation(Status.UNACCEPTABLE),
      getValidation(Status.FINE)
    );
    JSONObject json = bean.extract(false);

    // "Move" it across network in text form
    String string = json.toJSONString();

    // Retrieved transferred object
    JSONObject retrievedJson = (JSONObject) JSONValue.parse(string);
    JobValidationBean retrievedBean = new JobValidationBean();
    retrievedBean.restore(retrievedJson);

    assertNull(retrievedBean.getId());

    Validation.FormInput fa = new Validation.FormInput("f", "i");
    Validation.FormInput fb = new Validation.FormInput("f2", "i2");

    Validation fromConnector = retrievedBean.getConnectorValidation(Direction.FROM);
    assertEquals(Status.FINE, fromConnector.getStatus());
    assertEquals(2, fromConnector.getMessages().size());
    assertTrue(fromConnector.getMessages().containsKey(fa));
    assertEquals(new Validation.Message(Status.FINE, "d"),
        fromConnector.getMessages().get(fa));

    Validation toConnector = retrievedBean.getConnectorValidation(Direction.TO);
    assertEquals(Status.FINE, toConnector.getStatus());
    assertEquals(2, toConnector.getMessages().size());
    assertTrue(toConnector.getMessages().containsKey(fa));
    assertEquals(new Validation.Message(Status.FINE, "d"),
        toConnector.getMessages().get(fa));

    Validation framework = retrievedBean.getFrameworkValidation();
    assertEquals(Status.UNACCEPTABLE, framework.getStatus());
    assertEquals(2, framework.getMessages().size());
    assertTrue(framework.getMessages().containsKey(fb));
    assertEquals(new Validation.Message(Status.UNACCEPTABLE, "c"),
      framework.getMessages().get(fb));
  }

  @Test
  public void testJobValidationBeanId() {
    // Serialize it to JSON object
    JobValidationBean bean = new JobValidationBean(
        getValidation(Status.FINE),
        getValidation(Status.FINE),
        getValidation(Status.FINE)
    );
    bean.setId((long) 10);
    JSONObject json = bean.extract(false);

    // "Move" it across network in text form
    String string = json.toJSONString();

    // Retrieved transferred object
    JSONObject retrievedJson = (JSONObject) JSONValue.parse(string);
    JobValidationBean retrievedBean = new JobValidationBean();
    retrievedBean.restore(retrievedJson);

    assertEquals((Long)(long) 10, retrievedBean.getId());
  }

  @Test
  public void testConnectionValidationBeanSerialization() {
    // Serialize it to JSON object
    ConnectionValidationBean bean = new ConnectionValidationBean(
        getValidation(Status.FINE),
        getValidation(Status.UNACCEPTABLE)
    );
    JSONObject json = bean.extract(false);

    // "Move" it across network in text form
    String string = json.toJSONString();

    // Retrieved transferred object
    JSONObject retrievedJson = (JSONObject) JSONValue.parse(string);
    ConnectionValidationBean retrievedBean = new ConnectionValidationBean();
    retrievedBean.restore(retrievedJson);

    assertNull(retrievedBean.getId());

    Validation.FormInput fa = new Validation.FormInput("f", "i");
    Validation.FormInput fb = new Validation.FormInput("f2", "i2");

    Validation connector = retrievedBean.getConnectorValidation();
    assertEquals(Status.FINE, connector.getStatus());
    assertEquals(2, connector.getMessages().size());
    assertTrue(connector.getMessages().containsKey(fa));
    assertEquals(new Validation.Message(Status.FINE, "d"),
        connector.getMessages().get(fa));

    Validation framework = retrievedBean.getFrameworkValidation();
    assertEquals(Status.UNACCEPTABLE, framework.getStatus());
    assertEquals(2, framework.getMessages().size());
    assertTrue(framework.getMessages().containsKey(fb));
    assertEquals(new Validation.Message(Status.UNACCEPTABLE, "c"),
        framework.getMessages().get(fb));
  }

  @Test
  public void testConnectionValidationBeanId() {
    // Serialize it to JSON object
    ConnectionValidationBean bean = new ConnectionValidationBean(
        getValidation(Status.FINE),
        getValidation(Status.FINE)
    );
    bean.setId((long) 10);
    JSONObject json = bean.extract(false);

    // "Move" it across network in text form
    String string = json.toJSONString();

    // Retrieved transferred object
    JSONObject retrievedJson = (JSONObject) JSONValue.parse(string);
    ConnectionValidationBean retrievedBean = new ConnectionValidationBean();
    retrievedBean.restore(retrievedJson);

    assertEquals((Long)(long) 10, retrievedBean.getId());
  }

  public Validation getValidation(Status status) {
    Map<Validation.FormInput, Validation.Message> messages =
      new HashMap<Validation.FormInput, Validation.Message>();

    messages.put(
      new Validation.FormInput("f", "i"),
      new Validation.Message(status, "d"));
    messages.put(
      new Validation.FormInput("f2", "i2"),
      new Validation.Message(status, "c"));

    return new Validation(status, messages);
  }
}
