/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import javax.validation.constraints.NotNull;

import org.eclipse.hawkbit.im.authentication.SpPermission.SpringEvalExpressions;
import org.eclipse.hawkbit.repository.exception.RSQLParameterSyntaxException;
import org.eclipse.hawkbit.repository.exception.RSQLParameterUnsupportedFieldException;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Management service for {@link TargetFilterQuery}s.
 *
 */
public interface TargetFilterQueryManagement {

    /**
     * creating new {@link TargetFilterQuery}.
     *
     * @param customTargetFilter
     * @return the created {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_CREATE_TARGET)
    TargetFilterQuery createTargetFilterQuery(@NotNull TargetFilterQuery customTargetFilter);

    /**
     * Delete target filter query.
     *
     * @param targetFilterQueryId
     *            IDs of target filter query to be deleted
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_DELETE_TARGET)
    void deleteTargetFilterQuery(@NotNull Long targetFilterQueryId);

    /**
     * Verifies provided filter syntax.
     * 
     * @param query
     *            to verify
     * 
     * @return <code>true</code> if syntax is valid
     * 
     * @throws RSQLParameterUnsupportedFieldException
     *             if a field in the RSQL string is used but not provided by the
     *             given {@code fieldNameProvider}
     * @throws RSQLParameterSyntaxException
     *             if the RSQL syntax is wrong
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    boolean verifyTargetFilterQuerySyntax(String query);

    /**
     *
     * Retrieves all target filter query{@link TargetFilterQuery}.
     *
     * @param pageable
     *            pagination parameter
     * @return the found {@link TargetFilterQuery}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findAllTargetFilterQuery(@NotNull Pageable pageable);

    /**
     * Counts all target filter queries
     * @return the number of all target filter queries
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Long countAllTargetFilterQuery();

    /**
     * Retrieves all target filter query which {@link TargetFilterQuery}.
     *
     *
     * @param pageable
     *            pagination parameter
     * @param name
     *            name filter
     * @return the page with the found {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findTargetFilterQueryByName(@NotNull Pageable pageable, String name);

    /**
     * Retrieves all target filter query which {@link TargetFilterQuery}.
     *
     *
     * @param pageable
     *            pagination parameter
     * @param rsqlFilter
     *            RSQL filter string
     * @return the page with the found {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findTargetFilterQueryByFilter(@NotNull Pageable pageable, String rsqlFilter);

    /**
     * Retrieves all target filter query which {@link TargetFilterQuery}.
     *
     *
     * @param pageable
     *            pagination parameter
     * @param distributionSet
     *            the auto assign distribution set
     * @return the page with the found {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findTargetFilterQueryByAutoAssignDS(@NotNull Pageable pageable,
            DistributionSet distributionSet);

    /**
     * Retrieves all target filter query which {@link TargetFilterQuery}.
     *
     *
     * @param pageable
     *            pagination parameter
     * @param distributionSet
     *            the auto assign distribution set
     * @param rsqlParam
     *            RSQL filter
     * @return the page with the found {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findTargetFilterQueryByAutoAssignDS(@NotNull Pageable pageable,
            DistributionSet distributionSet, String rsqlParam);

    /**
     * Retrieves all target filter query with auto assign DS which {@link TargetFilterQuery}.
     *
     *
     * @return the page with the found {@link TargetFilterQuery}
     * @param pageable
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    Page<TargetFilterQuery> findTargetFilterQueryWithAutoAssignDS(@NotNull Pageable pageable);

    /**
     * Find target filter query by id.
     *
     * @param targetFilterQueryId
     *            Target filter query id
     * @return the found {@link TargetFilterQuery}
     *
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    TargetFilterQuery findTargetFilterQueryById(@NotNull Long targetFilterQueryId);

    /**
     * Find target filter query by name.
     *
     * @param targetFilterQueryName
     *            Target filter query name
     * @return the found {@link TargetFilterQuery}
     *
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_READ_TARGET)
    TargetFilterQuery findTargetFilterQueryByName(@NotNull String targetFilterQueryName);

    /**
     * updates the {@link TargetFilterQuery}.
     *
     * @param targetFilterQuery
     *            to be updated
     * @return the updated {@link TargetFilterQuery}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_UPDATE_TARGET)
    TargetFilterQuery updateTargetFilterQuery(@NotNull TargetFilterQuery targetFilterQuery);
}
