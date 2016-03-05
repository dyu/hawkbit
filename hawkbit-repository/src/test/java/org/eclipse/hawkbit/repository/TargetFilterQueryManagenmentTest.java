/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import static org.junit.Assert.assertEquals;

import org.eclipse.hawkbit.AbstractIntegrationTest;
import org.eclipse.hawkbit.repository.exception.EntityAlreadyExistsException;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.junit.Test;

/**
 * Test class for {@link TargetFilterQueryManagement}.
 * 
 *
 *
 */



public class TargetFilterQueryManagenmentTest extends AbstractIntegrationTest {

    @Test
    
    public void createTargetFilterQuery() {
        final String filterName = "new target filter";
        final TargetFilterQuery targetFilterQuery = targetFilterQueryManagement
                .createTargetFilterQuery(new TargetFilterQuery(filterName, "name==PendingTargets001"));
        assertEquals("Retrieved newly created custom target filter", targetFilterQuery,
                targetFilterQueryManagement.findTargetFilterQueryByName(filterName));
    }

    @Test(expected = EntityAlreadyExistsException.class)
    
    public void createDuplicateTargetFilterQuery() {
        final String filterName = "new target filter duplicate";
        targetFilterQueryManagement
                .createTargetFilterQuery(new TargetFilterQuery(filterName, "name==PendingTargets001"));

        targetFilterQueryManagement
                .createTargetFilterQuery(new TargetFilterQuery(filterName, "name==PendingTargets001"));
    }

    @Test
    
    public void deleteTargetFilterQuery() {
        final String filterName = "delete_target_filter_query";
        final TargetFilterQuery targetFilterQuery = targetFilterQueryManagement
                .createTargetFilterQuery(new TargetFilterQuery(filterName, "name==PendingTargets001"));
        targetFilterQueryManagement.deleteTargetFilterQuery(targetFilterQuery.getId());
        assertEquals("Returns null as the target filter is deleted", null,
                targetFilterQueryManagement.findTargetFilterQueryById(targetFilterQuery.getId()));

    }

    @Test
    
    public void updateTargetFilterQuery() {
        final String filterName = "target_filter_01";
        final TargetFilterQuery targetFilterQuery = targetFilterQueryManagement
                .createTargetFilterQuery(new TargetFilterQuery(filterName, "name==PendingTargets001"));

        final String newQuery = "status==UNKNOWN";
        targetFilterQuery.setQuery(newQuery);
        targetFilterQueryManagement.updateTargetFilterQuery(targetFilterQuery);
        assertEquals("Returns updated target filter query", newQuery, targetFilterQueryManagement
                .findTargetFilterQueryByName(filterName).getQuery());

    }

}
