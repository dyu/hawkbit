/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.controller;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.hawkbit.AbstractIntegrationTest;
import org.eclipse.hawkbit.MockMvcResultPrinter;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.rest.resource.JsonBuilder;
import org.junit.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "im", "test" })

public class ConfigDataTest extends AbstractIntegrationTest {

    @Test
    public void requestConfigDataIfEmpty() throws Exception {
        final Target target = new Target("4712");
        final Target savedTarget = targetManagement.createTarget(target);

        final long current = System.currentTimeMillis();
        mvc.perform(get("/{tenant}/controller/v1/4712", tenantAware.getCurrentTenant()))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$config.polling.sleep", equalTo("00:01:00")))
                .andExpect(jsonPath("$_links.configData.href", equalTo(
                        "http://localhost/" + tenantAware.getCurrentTenant() + "/controller/v1/4712/configData")));
        Thread.sleep(1); // is required: otherwise processing the next line is
                         // often too fast and
                         // the following assert will fail
        assertThat(targetManagement.findTargetByControllerID("4712").getTargetInfo().getLastTargetQuery())
                .isLessThanOrEqualTo(System.currentTimeMillis());
        assertThat(targetManagement.findTargetByControllerID("4712").getTargetInfo().getLastTargetQuery())
                .isGreaterThanOrEqualTo(current);

        savedTarget.getTargetInfo().getControllerAttributes().put("dsafsdf", "sdsds");

        final Target updateControllerAttributes = controllerManagament.updateControllerAttributes(
                savedTarget.getControllerId(), savedTarget.getTargetInfo().getControllerAttributes());
        // request controller attributes need to be false because we don't want
        // to request the
        // controller attributes again
        assertThat(updateControllerAttributes.getTargetInfo().isRequestControllerAttributes()).isFalse();

        mvc.perform(get("/{tenant}/controller/v1/4712", tenantAware.getCurrentTenant()))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$config.polling.sleep", equalTo("00:01:00")))
                .andExpect(jsonPath("$_links.configData.href").doesNotExist());
    }

    @Test
    public void putConfigData() throws Exception {
        targetManagement.createTarget(new Target("4717"));

        // initial
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("dsafsdf", "sdsds");

        long current = System.currentTimeMillis();
        mvc.perform(put("/{tenant}/controller/v1/4717/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isOk());
        Thread.sleep(1); // is required: otherwise processing the next line is
                         // often too fast and
                         // the following assert will fail
        assertThat(targetManagement.findTargetByControllerID("4717").getTargetInfo().getLastTargetQuery())
                .isLessThanOrEqualTo(System.currentTimeMillis());
        assertThat(targetManagement.findTargetByControllerID("4717").getTargetInfo().getLastTargetQuery())
                .isGreaterThanOrEqualTo(current);
        assertThat(
                targetManagement.findTargetByControllerIDWithDetails("4717").getTargetInfo().getControllerAttributes())
                        .isEqualTo(attributes);

        // update
        attributes.put("sdsds", "123412");
        current = System.currentTimeMillis();
        mvc.perform(put("/{tenant}/controller/v1/4717/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isOk());
        Thread.sleep(1); // is required: otherwise processing the next line is
                         // often too fast and
                         // the following assert will fail
        assertThat(targetManagement.findTargetByControllerID("4717").getTargetInfo().getLastTargetQuery())
                .isLessThanOrEqualTo(System.currentTimeMillis());
        assertThat(targetManagement.findTargetByControllerID("4717").getTargetInfo().getLastTargetQuery())
                .isGreaterThanOrEqualTo(current);
        assertThat(
                targetManagement.findTargetByControllerIDWithDetails("4717").getTargetInfo().getControllerAttributes())
                        .isEqualTo(attributes);
    }

    @Test
    public void putToMuchConfigData() throws Exception {
        targetManagement.createTarget(new Target("4717"));

        // initial
        Map<String, String> attributes = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            attributes.put("dsafsdf" + i, "sdsds" + i);
        }
        mvc.perform(put("/{tenant}/controller/v1/4717/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        attributes = new HashMap<>();
        attributes.put("on too many", "sdsds");
        mvc.perform(put("/{tenant}/controller/v1/4717/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    public void badConfigData() throws Exception {
        final Target target = new Target("4712");
        final Target savedTarget = targetManagement.createTarget(target);

        // not allowed methods
        mvc.perform(post("/{tenant}/controller/v1/4712/configData", tenantAware.getCurrentTenant()))
                .andDo(MockMvcResultPrinter.print()).//
                andExpect(status().isMethodNotAllowed());

        mvc.perform(get("/{tenant}/controller/v1/4712/configData", tenantAware.getCurrentTenant()))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isMethodNotAllowed());

        mvc.perform(delete("/{tenant}/controller/v1/4712/configData", tenantAware.getCurrentTenant()))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isMethodNotAllowed());

        // bad content type
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("dsafsdf", "sdsds");
        mvc.perform(put("/{tenant}/controller/v1/4712/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaTypes.HAL_JSON))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isUnsupportedMediaType());

        // non existing target
        mvc.perform(put("/{tenant}/controller/v1/456456/configData", tenantAware.getCurrentTenant())
                .content(JsonBuilder.configData("", attributes, "closed")).contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isNotFound());

        // bad body
        mvc.perform(put("/{tenant}/controller/v1/4712/configData", tenantAware.getCurrentTenant())
                .content("{\"id\": \"51659181\"}").contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultPrinter.print()).andExpect(status().isBadRequest());
    }
}
