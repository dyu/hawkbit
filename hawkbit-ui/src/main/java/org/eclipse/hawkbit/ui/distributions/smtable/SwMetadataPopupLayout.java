/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.distributions.smtable;

import java.util.List;

import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.SoftwareManagement;
import org.eclipse.hawkbit.repository.SpPermissionChecker;
import org.eclipse.hawkbit.repository.model.SoftwareModule;
import org.eclipse.hawkbit.repository.model.SoftwareModuleMetadata;
import org.eclipse.hawkbit.ui.common.AbstractMetadataPopupLayout;
import org.eclipse.hawkbit.ui.distributions.event.MetadataEvent;
import org.eclipse.hawkbit.ui.distributions.event.MetadataEvent.MetadataUIEvent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

/**
 * Pop up layout to display software module metadata.
 */
@SpringComponent
@ViewScope
public class SwMetadataPopupLayout extends AbstractMetadataPopupLayout<SoftwareModule, SoftwareModuleMetadata> {

    private static final long serialVersionUID = -1252090014161012563L;

    @Autowired
    private transient SoftwareManagement softwareManagement;

    @Autowired
    private transient EntityFactory entityFactory;

    @Autowired
    protected SpPermissionChecker permChecker;

    @Override
    protected void checkForDuplicate(final SoftwareModule entity, final String value) {
        softwareManagement.findSoftwareModuleMetadata(entity.getId(), value);
    }

    /**
     * Create metadata for SWModule.
     */
    @Override
    protected SoftwareModuleMetadata createMetadata(final SoftwareModule entity, final String key, final String value) {
        final SoftwareModuleMetadata swMetadata = softwareManagement
                .createSoftwareModuleMetadata(entityFactory.generateSoftwareModuleMetadata(entity, key, value));
        setSelectedEntity(swMetadata.getSoftwareModule());
        eventBus.publish(this, new MetadataEvent(MetadataUIEvent.CREATE_SOFTWARE_MODULE_METADATA, swMetadata));
        return swMetadata;
    }

    /**
     * Update metadata for SWModule.
     */
    @Override
    protected SoftwareModuleMetadata updateMetadata(final SoftwareModule entity, final String key, final String value) {
        final SoftwareModuleMetadata swMetadata = softwareManagement
                .updateSoftwareModuleMetadata(entityFactory.generateSoftwareModuleMetadata(entity, key, value));
        setSelectedEntity(swMetadata.getSoftwareModule());
        return swMetadata;
    }

    @Override
    protected List<SoftwareModuleMetadata> getMetadataList() {
        return softwareManagement.findSoftwareModuleMetadataBySoftwareModuleId(getSelectedEntity().getId());
    }

    /**
     * delete metadata for SWModule.
     */
    @Override
    protected void deleteMetadata(final SoftwareModule entity, final String key, final String value) {
        final SoftwareModuleMetadata swMetadata = entityFactory.generateSoftwareModuleMetadata(entity, key, value);
        softwareManagement.deleteSoftwareModuleMetadata(entity.getId(), key);
        eventBus.publish(this, new MetadataEvent(MetadataUIEvent.DELETE_SOFTWARE_MODULE_METADATA, swMetadata));
    }

    @Override
    protected boolean hasCreatePermission() {
        return permChecker.hasCreateDistributionPermission();
    }

    @Override
    protected boolean hasUpdatePermission() {
        return permChecker.hasUpdateDistributionPermission();
    }
}
