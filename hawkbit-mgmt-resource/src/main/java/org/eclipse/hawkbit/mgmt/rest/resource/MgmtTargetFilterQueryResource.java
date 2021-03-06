/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.MgmtId;
import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.distributionset.MgmtDistributionSet;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQuery;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQueryRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetFilterQueryRestApi;
import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.TargetFilterQueryManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource handling target CRUD operations.
 */
@RestController
public class MgmtTargetFilterQueryResource implements MgmtTargetFilterQueryRestApi {
    private static final Logger LOG = LoggerFactory.getLogger(MgmtTargetFilterQueryResource.class);

    @Autowired
    private TargetFilterQueryManagement filterManagement;

    @Autowired
    private DistributionSetManagement distributionSetManagement;

    @Autowired
    private EntityFactory entityFactory;

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> getFilter(@PathVariable("filterId") Long filterId) {
        final TargetFilterQuery findTarget = findFilterWithExceptionIfNotFound(filterId);
        // to single response include poll status
        final MgmtTargetFilterQuery response = MgmtTargetFilterQueryMapper.toResponse(findTarget);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PagedList<MgmtTargetFilterQuery>> getFilters(
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) int pagingOffsetParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) int pagingLimitParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) String sortParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SEARCH, required = false) String rsqlParam) {

        final int sanitizedOffsetParam = PagingUtility.sanitizeOffsetParam(pagingOffsetParam);
        final int sanitizedLimitParam = PagingUtility.sanitizePageLimitParam(pagingLimitParam);
        final Sort sorting = PagingUtility.sanitizeTargetFilterQuerySortParam(sortParam);

        final Pageable pageable = new OffsetBasedPageRequest(sanitizedOffsetParam, sanitizedLimitParam, sorting);
        final Slice<TargetFilterQuery> findTargetFiltersAll;
        final Long countTargetsAll;
        if (rsqlParam != null) {
            final Page<TargetFilterQuery> findFilterPage = filterManagement
                    .findTargetFilterQueryByFilter(pageable, rsqlParam);
            countTargetsAll = findFilterPage.getTotalElements();
            findTargetFiltersAll = findFilterPage;
        } else {
            findTargetFiltersAll = filterManagement.findAllTargetFilterQuery(pageable);
            countTargetsAll = filterManagement.countAllTargetFilterQuery();
        }

        final List<MgmtTargetFilterQuery> rest = MgmtTargetFilterQueryMapper
                .toResponse(findTargetFiltersAll.getContent());
        return new ResponseEntity<>(new PagedList<MgmtTargetFilterQuery>(rest, countTargetsAll), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> createFilter(@RequestBody MgmtTargetFilterQueryRequestBody filter) {
        final TargetFilterQuery createdTarget = filterManagement
                .createTargetFilterQuery(MgmtTargetFilterQueryMapper.fromRequest(entityFactory, filter));

        return new ResponseEntity<>(MgmtTargetFilterQueryMapper.toResponse(createdTarget), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> updateFilter(@PathVariable("filterId") Long filterId,
            @RequestBody MgmtTargetFilterQueryRequestBody targetFilterRest) {

        final TargetFilterQuery existingFilter = findFilterWithExceptionIfNotFound(filterId);
        LOG.debug("updating target filter query {}", existingFilter.getId());
        if (targetFilterRest.getName() != null) {
            existingFilter.setName(targetFilterRest.getName());
        }
        if (targetFilterRest.getQuery() != null) {
            existingFilter.setQuery(targetFilterRest.getQuery());
        }

        final TargetFilterQuery updateFilter = filterManagement.updateTargetFilterQuery(existingFilter);

        return new ResponseEntity<>(MgmtTargetFilterQueryMapper.toResponse(updateFilter), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteFilter(@PathVariable("filterId") Long filterId) {
        final TargetFilterQuery filter = findFilterWithExceptionIfNotFound(filterId);
        filterManagement.deleteTargetFilterQuery(filter.getId());
        LOG.debug("{} target filter query deleted, return status {}", filterId, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> postAssignedDistributionSet(@PathVariable("filterId") Long filterId,
            @RequestBody MgmtId dsId) {
        final TargetFilterQuery filter = findFilterWithExceptionIfNotFound(filterId);

        DistributionSet distributionSet;
        distributionSet = distributionSetManagement.findDistributionSetById(dsId.getId());
        if (distributionSet == null) {
            throw new EntityNotFoundException("DistributionSet with Id {" + dsId + "} does not exist");
        }

        filter.setAutoAssignDistributionSet(distributionSet);

        final TargetFilterQuery updateFilter = filterManagement.updateTargetFilterQuery(filter);

        return new ResponseEntity<>(MgmtTargetFilterQueryMapper.toResponse(updateFilter), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MgmtDistributionSet> getAssignedDistributionSet(@PathVariable("filterId") Long filterId) {
        final TargetFilterQuery filter = findFilterWithExceptionIfNotFound(filterId);
        DistributionSet autoAssignDistributionSet = filter.getAutoAssignDistributionSet();
        MgmtDistributionSet distributionSetRest = MgmtDistributionSetMapper.toResponse(autoAssignDistributionSet);
        final HttpStatus retStatus = distributionSetRest == null ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(distributionSetRest, retStatus);
    }

    @Override
    public ResponseEntity<Void> deleteAssignedDistributionSet(@PathVariable("filterId") Long filterId) {
        final TargetFilterQuery filter = findFilterWithExceptionIfNotFound(filterId);

        filter.setAutoAssignDistributionSet(null);

        filterManagement.updateTargetFilterQuery(filter);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private TargetFilterQuery findFilterWithExceptionIfNotFound(final Long filterId) {
        final TargetFilterQuery filter = filterManagement.findTargetFilterQueryById(filterId);
        if (filter == null) {
            throw new EntityNotFoundException("TargetFilterQuery with Id {" + filterId + "} does not exist");
        }
        return filter;
    }

}
